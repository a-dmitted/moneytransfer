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
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AccountResourceTest extends JerseyTest {

    @Inject
    private AccountService accountService;


    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig config = new ResourceConfig(AccountResource.class);
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
    public void testRetreiveAnAccount() throws DaoException {
        final String ACC = "0000 1000 0000 0000";
        final long INITIAL_AMOUNT = 1000L;
        Account account = new Account(ACC, INITIAL_AMOUNT);
        accountService.save(account);

        Response response = target("accountresource/retrieve/"+ACC).request().get();
        AccountEntity account1 = response.readEntity(AccountEntity.class);

        assertEquals(INITIAL_AMOUNT, (long)account1.getAmount());
    }

    @Test
    public void testSaveAnAccount() throws DaoException {
        final String ACC = "0000 2000 0000 0000";
        final long INITIAL_AMOUNT = 1000L;
        Account account = new Account(ACC, INITIAL_AMOUNT);
        Entity<Account> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("accountresource/save").request().post(accountEntity);
        Account createdAccount = accountService.retrieve(ACC);
        assertEquals(INITIAL_AMOUNT, (long)createdAccount.getAmount());
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
