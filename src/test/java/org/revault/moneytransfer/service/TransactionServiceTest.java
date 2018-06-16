package org.revault.moneytransfer.service;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Before;
import org.junit.Test;
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.configure.ApplicationBinder;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.err.ServiceExeption;

import javax.inject.Inject;

public class TransactionServiceTest {
    @Inject
    private TransactionService transactionService;

    @Before
    public void setUp() {
        final Binder b = new AbstractBinder() {
            @Override
            public void configure() {
                bindAsContract(TransactionService.class);
            }
        };
        final ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder(), b);
        locator.inject(this);
    }

    @Test
    public void test() throws DaoException {
        AccountService accountService = transactionService.getAccountService();
        accountService.save(new Account("0000 0000", 1000L));
        accountService.save(new Account("1111 1111", 1000L));
        try {
            transactionService.makeTransfer("0000 0000", "1111 1111", 100L);
        }
        catch (ServiceExeption serviceExeption) {
            serviceExeption.printStackTrace();
        }
        Account account1 = accountService.retrieve("0000 0000");
        Account account2 = accountService.retrieve("1111 1111");
    }

}
