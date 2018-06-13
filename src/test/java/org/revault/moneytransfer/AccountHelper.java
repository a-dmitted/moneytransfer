package org.revault.moneytransfer;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.service.AccountServiceImpl;

public class AccountHelper {
    private AccountServiceImpl accountService;

    public AccountHelper() {
        this.accountService = new AccountServiceImpl();
    }

    public void saveAccount(String number, Long amount){
        Account account = new Account("0000 0000", 1000L);
        accountService.save(account);
    }

    public AccountServiceImpl getAccountService() {
        return accountService;
    }
}