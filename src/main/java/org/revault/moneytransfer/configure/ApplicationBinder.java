package org.revault.moneytransfer.configure;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.revault.moneytransfer.service.AccountService;
import org.revault.moneytransfer.service.AccountServiceImpl;
import org.revault.moneytransfer.service.TransactionService;
import org.revault.moneytransfer.service.TransactionServiceImpl;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure(){
        bind(AccountServiceImpl.class).to(AccountService.class).in(Singleton.class);
        bind(TransactionServiceImpl.class).to(TransactionService.class).in(Singleton.class);
    }
}
