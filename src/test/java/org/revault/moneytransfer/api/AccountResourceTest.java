package org.revault.moneytransfer.api;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.revault.moneytransfer.AccountHelper;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.service.TransactionService;

import javax.inject.Singleton;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AccountResourceTest extends JerseyTest {

    @Mock
    private TransactionService transactionServiceMock;

    private AccountHelper accountHelper;

    @Override
    protected Application configure() {
        MockitoAnnotations.initMocks(this);

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig config = new ResourceConfig(AccountResource.class);
        config.register(new InjectableProvider());

        return config;
    }

    @Test
    public void testRetreiveAnAccount(){
        accountHelper = new AccountHelper();
        accountHelper.saveAccount("0000 0000", 1000L);

        when(transactionServiceMock.getAccountService()).thenReturn(accountHelper.getAccountService());

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
            return transactionServiceMock;
        }

        public void dispose(TransactionService service) {
            transactionServiceMock = null;
        }
    }


}
