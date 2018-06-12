package org.revault.moneytransfer.service;

import org.revault.moneytransfer.entity.Account;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionServiceImpl implements TransactionService {
    @Inject
    private AccountService accountService;

    @Override
    public void makeTransfer(String debitAcc, String creditAcc, Long amount) {
       Account debitAccount = accountService.retreive(debitAcc);
       Account creditAccount = accountService.retreive(creditAcc);
       if(debitAccount.isEnough(amount)){
           debitAccount.setAmount(debitAccount.getAmount()-amount);
           creditAccount.setAmount(creditAccount.getAmount()+amount);
           accountService.saveTwo(debitAccount, creditAccount);
       }
    }
    @Override
    public AccountService getAccountService(){
        return this.accountService;
    }
}
