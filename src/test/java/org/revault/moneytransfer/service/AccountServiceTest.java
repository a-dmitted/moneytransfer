package org.revault.moneytransfer.service;

import org.junit.*;
import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.err.DaoException;

public class AccountServiceTest{
    private static AccountService accountService;
    @BeforeClass
    public static void setUp(){
        accountService = new AccountServiceImpl();
    }

    @Test
    public void testInsertAccount() throws DaoException {
        Account account= new Account("0000 0000 0000 0000", 100L);
        accountService.save(account);
    }

    @Test
    public void testUpdateAccount()throws DaoException {
        Account account= new Account("0000 0000 0000 0001", 100L);
        accountService.save(account);
        Account account1 = accountService.retrieve("0000 0000 0000 0001");
        account1.setAmount(200L);
        accountService.save(account1);
    }

    @Test
    public void testDeleteAccount() throws DaoException {
        Account account= new Account("0000 0000 0000 0002", 100L);
        accountService.save(account);
        accountService.delete("0000 0000 0000 0002");
    }

}
