package org.revault.moneytransfer.service;

import org.revault.moneytransfer.err.ServiceExeption;

public interface TransactionService {
    public void makeTransfer(String debitAcc, String creditAcc, Long amount) throws ServiceExeption;
    public AccountService getAccountService();
}
