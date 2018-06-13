package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionServiceImpl implements TransactionService {
    @Inject
    private AccountService accountService;

    @Override
    public void makeTransfer(String debitAcc, String creditAcc, Long amount) {
       Account debitAccountEntity = accountService.retreive(debitAcc);
       Account creditAccountEntity = accountService.retreive(creditAcc);
       if(isEnough(debitAccountEntity, amount)){
           debitAccountEntity.setAmount(debitAccountEntity.getAmount()-amount);
           creditAccountEntity.setAmount(creditAccountEntity.getAmount()+amount);
           accountService.saveTwo(debitAccountEntity, creditAccountEntity);
       }
    }
    @Override
    public AccountService getAccountService(){
        return this.accountService;
    }

    private boolean isEnough(Account account, Long amount){
        if(account.getAmount()>amount)
            return true;
        else
            return false;

    }
}
