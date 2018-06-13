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
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.configure.ApplicationBinder;
import org.revault.moneytransfer.service.TransactionService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


public class MoneyTransferResourceTest extends JerseyTest {
    @Inject
    private TransactionService transactionService;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig config = new ResourceConfig(MoneyTransferResource.class, AccountResource.class);
        config.register(new InjectableProvider());

        /*
        *  We use ApplicationBinder to bind implementations to interfaces
        *  and allow dependency injection
        */
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
    public void testMoneyTransfer(){
        /*
         * Emulating a repository
         */
        Account debitAccBefore = new Account("0000 0000 0000 0000", 1000L);
        transactionService.getAccountService().save(debitAccBefore);
        Account creditAccBefore = new Account("1111 1111 1111 1111", 1000L);
        transactionService.getAccountService().save(creditAccBefore);

        /*
         * Making a transaction
         */

        Response response = target("moneytransfer/transfer")
                .queryParam("debitAcc","0000 0000 0000 0000")
                .queryParam("creditAcc", "1111 1111 1111 1111")
                .queryParam("amount", 100L).request().get();

        /*
         * Verifying the accounts state after transaction
         */
        Account debitAccAfter = transactionService.getAccountService().retreive("0000 0000 0000 0000");
        Account creditAccAfter = transactionService.getAccountService().retreive("1111 1111 1111 1111");
        assertEquals(900L, (long)debitAccAfter.getAmount());
        assertEquals(1100L, (long)creditAccAfter.getAmount());
    }

    @Test
    public void testMoneyTransferNotEnough(){
        /*
         * Emulating a repository
         */
        Account debitAccBefore = new Account("2222 2222 2222 2222", 1000L);
        transactionService.getAccountService().save(debitAccBefore);
        Account creditAccBefore = new Account("3333 3333 3333 3333", 1000L);
        transactionService.getAccountService().save(creditAccBefore);

        /*
         * Making a transaction
         */

        Response response = target("moneytransfer/transfer")
                .queryParam("debitAcc","2222 2222 2222 2222")
                .queryParam("creditAcc", "3333 3333 3333 3333")
                .queryParam("amount", 1100L).request().get();

        /*
         * Verifying the accounts state after transaction
         */
        Account debitAccAfter = transactionService.getAccountService().retreive("2222 2222 2222 2222");
        Account creditAccAfter = transactionService.getAccountService().retreive("3333 3333 3333 3333");
        assertEquals(900L, (long)debitAccAfter.getAmount());
        assertEquals(1100L, (long)creditAccAfter.getAmount());
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