package org.revault.moneytransfer.api;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.revault.moneytransfer.MoneyTransferApp;
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.api.data.Fail;
import org.revault.moneytransfer.api.data.Transfer;
import org.revault.moneytransfer.configure.ApplicationBinder;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;


public class MoneyTransferResourceTest extends JerseyTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(MoneyTransferApp.class);

    @Inject
    private AccountService accountService;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        /*
        *  To bootstrap the tests we need register AbstractBinder in similar way as we do that
        *  in MoneyTransferApp() while starting ther server.
        *  Whe need and instantiated object of AccountService
        *  to be injected into MoneyTransferResource.
        */
        ResourceConfig config = new ResourceConfig(MoneyTransferResource.class, AccountResource.class);
        config.register(new InjectableProvider());
        final Binder b = new AbstractBinder() {
            @Override
            public void configure() {
                bindAsContract(AccountResource.class);
            }
        };
        final ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder(), b);
        locator.inject(this);

        return config;
    }


    @Test
    public void testMoneyTransfer() throws DaoException{
        /*
         * During a test two accounts are created. A transaction is attempting make a money
         * transfer. It succeeds, amounts of both accounts are changed respectively.
         */
        final String DEBIT_ACC = "0000 0000 0000 0000";
        final String CREDIT_ACC = "1111 1111 1111 1111";
        final long INITIAL_AMOUNT = 1000L;
        final long AMOUNT_OF_TRANSFER = 100L;
        Account debitAccBefore = new Account(DEBIT_ACC, INITIAL_AMOUNT);
        accountService.save(debitAccBefore);
        Account creditAccBefore = new Account(CREDIT_ACC, INITIAL_AMOUNT);
        accountService.save(creditAccBefore);

        Transfer transfer = new Transfer(DEBIT_ACC, CREDIT_ACC, AMOUNT_OF_TRANSFER);
        Entity<Transfer> transferEntity = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("moneytransfer/transfer").request().put(transferEntity);

        Account debitAccAfter = accountService.retrieve(DEBIT_ACC);
        Account creditAccAfter = accountService.retrieve(CREDIT_ACC);
        assertEquals(INITIAL_AMOUNT-AMOUNT_OF_TRANSFER, (long)debitAccAfter.getAmount());
        assertEquals(INITIAL_AMOUNT+AMOUNT_OF_TRANSFER, (long)creditAccAfter.getAmount());
    }

    @Test
    public void testMoneyTransferNotEnough() throws DaoException {
        /*
         * During a test two accounts are created. A transaction is attempting make a money
         * transfer. It fails, as there is not enough money on the debit account.
         */
        final String DEBIT_ACC = "2222 2222 2222 2222";
        final String CREDIT_ACC = "3333 3333 3333 3333";
        final long INITIAL_AMOUNT = 1000L;
        final long AMOUNT_OF_TRANSFER = 1100L;
        Account debitAccBefore = new Account(DEBIT_ACC, INITIAL_AMOUNT);
        accountService.save(debitAccBefore);
        Account creditAccBefore = new Account(CREDIT_ACC, INITIAL_AMOUNT);
        accountService.save(creditAccBefore);

        Transfer transfer = new Transfer(DEBIT_ACC, CREDIT_ACC, AMOUNT_OF_TRANSFER);
        Entity<Transfer> transferEntity = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("moneytransfer/transfer").request().put(transferEntity);

        Account debitAccAfter = accountService.retrieve(DEBIT_ACC);
        Account creditAccAfter = accountService.retrieve(CREDIT_ACC);
        assertEquals(INITIAL_AMOUNT, (long)debitAccAfter.getAmount());
        assertEquals(INITIAL_AMOUNT, (long)creditAccAfter.getAmount());
        assertEquals("Precondition Failed", response.getStatusInfo().getReasonPhrase());
        /*
         * The error messages returned from the service are driven by err.Error enum
         */
        assertEquals("1001: Not enough money on the debit accout.", response.readEntity(Fail.class).getMessage());
    }

    @Test
    public void testMoneyTransferConcurrent() throws DaoException, InterruptedException {
        /*
         * During a test three accounts are created. Two transactions in different threads are attempting to make a
         * money transfer, from account D to accounts C1 and C2. But C1 and C2 are locked by the other transaction.
         * Due to PESSIMISTIC LOCKING mode both transactions will be serialized.
         */
        final Semaphore available = new Semaphore(2, true);
        final String DEBIT_ACC = "1000 0000 0000 0000";
        final String CREDIT_ACC1 = "1000 0000 0000 1111";
        final String CREDIT_ACC2 = "1000 0000 0000 2222";
        final Long INITIAL_AMOUNT = 1000L;
        final Long AMOUNT_OF_TRANSFER = 100L;
        Account debitAccBefore = new Account(DEBIT_ACC, INITIAL_AMOUNT);
        accountService.save(debitAccBefore);
        Account creditAccBefore1 = new Account(CREDIT_ACC1, INITIAL_AMOUNT);
        accountService.save(creditAccBefore1);
        Account creditAccBefore2 = new Account(CREDIT_ACC2, INITIAL_AMOUNT);
        accountService.save(creditAccBefore2);

        Thread thread1 = new Thread(()->{
            try {
                available.acquire();
                available.acquire();
            }catch(InterruptedException ex){ };
            EntityManager em = accountService.getEmf().createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            LOGGER.info("Locking credit accounts...");
            em.createQuery("select a from AccountEntity a where a.number = :accountNumber", AccountEntity.class)
                    .setParameter("accountNumber", creditAccBefore1.getNumber())
                    .setLockMode(LockModeType.PESSIMISTIC_READ)
                    .getResultList();
            em.createQuery("select a from AccountEntity a where a.number = :accountNumber", AccountEntity.class)
                    .setParameter("accountNumber", creditAccBefore2.getNumber())
                    .setLockMode(LockModeType.PESSIMISTIC_READ)
                    .getResultList();

            available.release();
            available.release();
            try{
                sleep(2000);
            }catch(Exception ex){};
            LOGGER.info("Releasing credit accounts...");
            tx.commit();

        });

        Thread thread2 = new Thread(()-> {
            try {
                try{sleep(1000);}catch(Exception ex){};
                available.acquire();
                LOGGER.info("Transaction 1 has begun...");
                Transfer transfer = new Transfer(DEBIT_ACC, CREDIT_ACC1, AMOUNT_OF_TRANSFER);
                Entity<Transfer> transferEntity = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);
                Response response = target("moneytransfer/transfer").request().put(transferEntity);
                LOGGER.info("Transaction 1 has finished...");
                available.release();
            } catch (InterruptedException ex) {
            }
            ;
        });

        Thread thread3 = new Thread(()-> {
            try {
                try{sleep(1000);}catch(Exception ex){};
                available.acquire();
                LOGGER.info("Transaction 2 has begun...");
                Transfer transfer = new Transfer(DEBIT_ACC, CREDIT_ACC2, AMOUNT_OF_TRANSFER);
                Entity<Transfer> transferEntity = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);
                Response response = target("moneytransfer/transfer").request().put(transferEntity);
                LOGGER.info("Transaction 2 has finished...");
                available.release();
            } catch (InterruptedException ex) {
            }
            ;
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();

        Account debitAccAfter = accountService.retrieve(DEBIT_ACC);
        Account creditAccAfter1 = accountService.retrieve(CREDIT_ACC1);
        Account creditAccAfter2 = accountService.retrieve(CREDIT_ACC2);
        assertEquals(INITIAL_AMOUNT - 2*AMOUNT_OF_TRANSFER, (long)debitAccAfter.getAmount());
        assertEquals(INITIAL_AMOUNT + AMOUNT_OF_TRANSFER, (long)creditAccAfter1.getAmount());
        assertEquals(INITIAL_AMOUNT + AMOUNT_OF_TRANSFER, (long)creditAccAfter2.getAmount());
    }

    class InjectableProvider extends AbstractBinder implements Factory<AccountService> {

        @Override
        protected void configure() {
            bindFactory(this).to(AccountService.class).in(Singleton.class);
        }

        public AccountService provide() {
            return accountService;
        }

        public void dispose(AccountService service) {
            accountService = null;
        }
    }
}