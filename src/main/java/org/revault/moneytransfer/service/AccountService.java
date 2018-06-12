package org.revault.moneytransfer.service;

import org.revault.moneytransfer.entity.Account;

public interface AccountService {
    public Account retreive(String number);
    public void delete(String number);
    public void save(Account account);
}
