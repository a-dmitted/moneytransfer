package org.revault.moneytransfer.api;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import org.revault.moneytransfer.MoneyTransferApp;
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.api.data.Fail;
import org.revault.moneytransfer.api.data.Transfer;
import org.revault.moneytransfer.configure.ApplicationBinder;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.*;
import javax.transaction.Transaction;
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
    private TransactionService transactionService;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        /*
        *  To bootstrap the tests we need register AbstractBinder in similar way as we do that
        *  in MoneyTransferApp() while starting ther server.
        *  Whe need and instantiated object of TransactionService with instantiated AccountService
        *  to be injected into MoneyTransferResource.
        */
        ResourceConfig config = new ResourceConfig(MoneyTransferResource.class, AccountResource.class);
        config.register(new InjectableProvider());
        final Binder b = new AbstractBinder() {
            @Override
            public void configure() {
                bindAsContract(TransactionService.class);
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
        Account debitAccBefore = new Account("0000 0000 0000 0000", 1000L);
        transactionService.getAccountService().save(debitAccBefore);
        Account creditAccBefore = new Account("1111 1111 1111 1111", 1000L);
        transactionService.getAccountService().save(creditAccBefore);

        Transfer transfer = new Transfer("0000 0000 0000 0000", "1111 1111 1111 1111", 100L);
        Entity<Transfer> transferEntity = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("moneytransfer/transfer").request().put(transferEntity);

        Account debitAccAfter = transactionService.getAccountService().retrieve("0000 0000 0000 0000");
        Account creditAccAfter = transactionService.getAccountService().retrieve("1111 1111 1111 1111");
        assertEquals(900L, (long)debitAccAfter.getAmount());
        assertEquals(1100L, (long)creditAccAfter.getAmount());
    }

    @Test
    public void testMoneyTransferNotEnough() throws DaoException {
        /*
         * During a test two accounts are created. A transaction is attempting make a money
         * transfer, but amount of the transactions exceeds an amount on the debit account.
         * Exception is being thrown. The amounts of both accounts left unchanged.
         * Service returns a predefined error message.
         */
        Account debitAccBefore = new Account("2222 2222 2222 2222", 1000L);
        transactionService.getAccountService().save(debitAccBefore);
        Account creditAccBefore = new Account("3333 3333 3333 3333", 1000L);
        transactionService.getAccountService().save(creditAccBefore);

        Transfer transfer = new Transfer("2222 2222 2222 2222", "3333 3333 3333 3333", 1100L);
        Entity<Transfer> transferEntity = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("moneytransfer/transfer").request().put(transferEntity);

        Account debitAccAfter = transactionService.getAccountService().retrieve("2222 2222 2222 2222");
        Account creditAccAfter = transactionService.getAccountService().retrieve("3333 3333 3333 3333");
        assertEquals(1000L, (long)debitAccAfter.getAmount());
        assertEquals(1000L, (long)creditAccAfter.getAmount());
        assertEquals("Precondition Failed", response.getStatusInfo().getReasonPhrase());
        /*
         * The error messages returned from the service are driven by err.Error enum
         */
        assertEquals("1001: Not enough money on the debit accout.", response.readEntity(Fail.class).getMessage());
    }

    @Test
    public void testMoneyTransferConcurrent() throws DaoException, InterruptedException {
        /*
         * During a test two accounts are created. A transaction is attempting make a money
         * transfer. It succeeds, amounts of both accounts are changed respectively.
         */

        final Semaphore available = new Semaphore(2, true);

        Account debitAccBefore = new Account("1000 0000 0000 0000", 1000L);
        transactionService.getAccountService().save(debitAccBefore);
        Account creditAccBefore1 = new Account("1000 0000 0000 1111", 1000L);
        transactionService.getAccountService().save(creditAccBefore1);
        Account creditAccBefore2 = new Account("1000 0000 0000 2222", 1000L);
        transactionService.getAccountService().save(creditAccBefore2);

        Thread thread1 = new Thread(()->{
            try {
                available.acquire();
                available.acquire();
                //LOGGER.info("Semaphore has been aquired...");
            }catch(InterruptedException ex){ };
            EntityManager em = transactionService.getAccountService().getEmf().createEntityManager();
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
                sleep(3000);
            }catch(Exception ex){};
            LOGGER.info("Releasing credit accounts...");
            tx.commit();

        });

        Thread thread2 = new Thread(()-> {
            try {
                try{
                    sleep(1000);
                }catch(Exception ex){};
                available.acquire();
                LOGGER.info("Transaction 1 has begun...");
                Transfer transfer = new Transfer("1000 0000 0000 0000", "1000 0000 0000 1111", 100L);
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
                try{
                    sleep(1000);
                }catch(Exception ex){};
                available.acquire();
                LOGGER.info("Transaction 2 has begun...");
                Transfer transfer = new Transfer("1000 0000 0000 0000", "1000 0000 0000 2222", 100L);
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


        Account debitAccAfter = transactionService.getAccountService().retrieve("1000 0000 0000 0000");
        Account creditAccAfter1 = transactionService.getAccountService().retrieve("1000 0000 0000 1111");
        Account creditAccAfter2 = transactionService.getAccountService().retrieve("1000 0000 0000 2222");
        //assertEquals(900L, (long)debitAccAfter.getAmount());
        //assertEquals(1100L, (long)creditAccAfter.getAmount());


    }

    class InjectableProvider extends AbstractBinder implements Factory<TransactionService> {

        @Override
        protected void configure() {
            bindFactory(this).to(TransactionService.class).in(Singleton.class);
        }

        public TransactionService provide() {
            return transactionService;
        }

        public void dispose(TransactionService service) {
            transactionService = null;
        }
    }
}