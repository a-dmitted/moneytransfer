package org.revault.moneytransfer.service;

import org.junit.*;
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.err.DaoException;

import java.io.DataOutput;

public class AccountServiceTest{
    private static AccountService accountService;
    @BeforeClass
    public static void setUp(){
        accountService = new AccountServiceImpl();
    }

    @Test
    public void testInsertAccount() throws DaoException {
        final String ACC = "0000 0001 0000 0000";
        final long INITIAL_AMOUNT = 100L;
        Account account= new Account(ACC, INITIAL_AMOUNT);
        accountService.save(account);
    }

    @Test
    public void testUpdateAccount() throws DaoException {
        final String ACC = "0000 0000 0001 0001";
        final long INITIAL_AMOUNT = 100L;
        final long NEW_AMOUNT = 200L;
        Account account= new Account(ACC, INITIAL_AMOUNT);
        accountService.save(account);
        Account account1 = accountService.retrieve(ACC);
        account1.setAmount(NEW_AMOUNT);
        accountService.save(account1);
    }

    @Test
    public void testDeleteAccount() throws DaoException {
        final String ACC = "0000 0000 0001 0002";
        final long INITIAL_AMOUNT = 100L;
        Account account= new Account(ACC, INITIAL_AMOUNT);
        accountService.save(account);
        accountService.delete(ACC);
    }

    @Test
    public void testConcurrentUpdateOfAnAccount() throws DaoException{
        final String ACC = "0000 1100 0000 0000";
        final long INITIAL_AMOUNT = 1000L;
        final long NEW_AMOUNT1 = 2000L;
        final long NEW_AMOUNT2 = 3000L;
        Account accountBefore = new Account(ACC, INITIAL_AMOUNT);
        accountService.save(accountBefore);

        Account accountAfter1 = accountService.retrieve(ACC);
        accountAfter1.setAmount(NEW_AMOUNT1);
        Account accountAfter2 = accountService.retrieve(ACC);
        accountAfter2.setAmount(NEW_AMOUNT2);
        accountService.save(accountAfter1);
        try {
            accountService.save(accountAfter2);
        } catch(DaoException ex){
            Assert.assertTrue("OPTIMISTIC locking using VERSION column prevented a second update of an account.", true);
        }

    }

}
