package org.revault.moneytransfer.service;

import org.revault.moneytransfer.entity.Account;

public interface TransactionService {
    public Account getByNumber(int number);
    public void makeTransfer(int debitAcc, int creditAcc, Long amount);
}
