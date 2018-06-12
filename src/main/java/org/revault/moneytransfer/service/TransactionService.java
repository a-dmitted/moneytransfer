package org.revault.moneytransfer.service;

import org.revault.moneytransfer.entity.Account;

public interface TransactionService {
    public void makeTransfer(String debitAcc, String creditAcc, Long amount);
    public AccountService getAccountService();
}
