package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;

public interface AccountService {
    public Account retreive(String number);
    public void delete(String number);
    public void save(Account account);
    public void saveTwo(Account account1, Account account2);
}
