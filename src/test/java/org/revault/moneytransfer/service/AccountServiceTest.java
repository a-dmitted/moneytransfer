package org.revault.moneytransfer.service;

import org.junit.*;
import org.revault.moneytransfer.entity.Account;

import javax.inject.Inject;

public class AccountServiceTest {
    private static AccountService accountService;
    @BeforeClass
    public static void setUp(){
        accountService = new AccountServiceImpl();
    }

    @Test
    public void testInsertAccount(){
        Account account = new Account(100L);
        account.setNumber("0000 0000 0000 0000");
        accountService.save(account);
    }

    @Test
    public void testUpdateAccount(){
        Account account = new Account(100L);
        account.setNumber("0000 0000 0000 0001");
        accountService.save(account);
        Account account1 = accountService.retreive("0000 0000 0000 0001");
        account1.setAmount(200L);
        accountService.save(account1);
    }

    @Test
    public void testDeleteAccount(){
        Account account = new Account(100L);
        account.setNumber("0000 0000 0000 0002");
        accountService.save(account);
        accountService.delete("0000 0000 0000 0002");
    }

}
