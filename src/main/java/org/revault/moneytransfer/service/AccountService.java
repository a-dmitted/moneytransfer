package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.err.ServiceExeption;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public interface AccountService {
    public Account retrieve(String number) throws DaoException;
    public void delete(String number) throws DaoException;
    public void save(Account account) throws DaoException ;
    public void saveTwo(Account account1, Account account2) throws DaoException ;
    public void makeTransfer(String debitAcc, String creditAcc, Long amount) throws ServiceExeption;
    public EntityManagerFactory getEmf();
}
