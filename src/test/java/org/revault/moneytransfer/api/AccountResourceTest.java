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

import org.revault.moneytransfer.AccountHelper;
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.configure.ApplicationBinder;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.service.TransactionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AccountResourceTest extends JerseyTest {

    @Inject
    private TransactionService transactionService;

    private AccountHelper accountHelper;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig config = new ResourceConfig(AccountResource.class);
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
    public void testRetreiveAnAccount(){
        Account account = new Account("0000 0000", 1000L);
        transactionService.getAccountService().save(account);

        Response response = target("accountresource/retrieve/0000 0000").request().get();
        AccountEntity accountEntity1 = response.readEntity(AccountEntity.class);

        assertEquals(1000L, (long)accountEntity1.getAmount());
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
