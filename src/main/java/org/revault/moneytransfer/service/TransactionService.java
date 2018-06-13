package org.revault.moneytransfer.service;

public interface TransactionService {
    public void makeTransfer(String debitAcc, String creditAcc, Long amount);
    public AccountService getAccountService();
}
