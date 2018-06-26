package org.revault.moneytransfer.configure;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.revault.moneytransfer.service.AccountService;
import org.revault.moneytransfer.service.AccountServiceImpl;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure(){
        bind(AccountServiceImpl.class).to(AccountService.class).in(Singleton.class);
    }
}
