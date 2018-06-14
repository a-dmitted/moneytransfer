package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.ServiceExeption;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.revault.moneytransfer.err.Error.NOT_ENOUGH_MONEY;

@Singleton
public class TransactionServiceImpl implements TransactionService{
    @Inject
    private AccountService accountService;

    @Override
    public void makeTransfer(String debitAcc, String creditAcc, Long amount) throws ServiceExeption {
       Account debitAccountEntity = accountService.retreive(debitAcc);
       Account creditAccountEntity = accountService.retreive(creditAcc);
       if(isEnough(debitAccountEntity, amount)){
           debitAccountEntity.setAmount(debitAccountEntity.getAmount()-amount);
           creditAccountEntity.setAmount(creditAccountEntity.getAmount()+amount);
           accountService.saveTwo(debitAccountEntity, creditAccountEntity);
       }
       else{
           throw new ServiceExeption(NOT_ENOUGH_MONEY);
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
