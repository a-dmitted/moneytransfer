package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.DaoException;

import javax.inject.Singleton;
import javax.persistence.*;

import static org.revault.moneytransfer.err.Error.*;

@Singleton
public class AccountServiceImpl implements AccountService{
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("moneytransfer-unit");
    private EntityManager em = emf.createEntityManager();
    @Override
    public Account retrieve(String number) throws DaoException{
        AccountEntity accountEntity = null;

        try {
            accountEntity = em.find(AccountEntity.class, number);
        }catch (Exception e) {
            throw new DaoException(RETRIEVING_ERR);
        }

        if (accountEntity==null) throw new DaoException(RETRIEVING_ERR);

        return entityToAccount(accountEntity);
    }

    @Override
    public void delete(String number) throws DaoException {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            AccountEntity accountEntity = em.find(AccountEntity.class, number);

            if(accountEntity == null) {
                throw new DaoException(DELETING_ERR);
            }
            else {
                em.remove(accountEntity);
            }

            transaction.commit();
        } catch (Exception e) {
            rollbackAtcitveTran(transaction);
            throw new DaoException(DELETING_ERR);
        }
    }

    @Override
    public void save(Account account)  throws DaoException{
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            em.merge(accountToEntity(account));

            transaction.commit();
        } catch (Exception e) {
            rollbackAtcitveTran(transaction);
            throw new DaoException(SAVING_ERR);
        }
    }

    @Override
    public void saveTwo(Account account1, Account account2) throws DaoException {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            em.merge(accountToEntity(account1));
            em.merge(accountToEntity(account2));

            transaction.commit();
        } catch (Exception e) {
            rollbackAtcitveTran(transaction);
            throw new DaoException(SAVING_TWO_ERR);

        }
    }

    private void rollbackAtcitveTran(EntityTransaction txn) throws DaoException{
        if (txn.isActive()) {
            try {
                txn.rollback();
            } catch (PersistenceException | IllegalStateException e) {
                throw new DaoException(ROLLBACK_ERR);
            }
        }
    }

    // =========== Helpers ================
    private Account entityToAccount(AccountEntity entity) {
        Account account = new Account();

        if (entity != null) {
            account.setNumber(entity.getNumber());
            account.setAmount(entity.getAmount());
        }
        return account;
    }

    private AccountEntity accountToEntity(Account account) {
        AccountEntity entity = new AccountEntity();

        if (account != null) {
            entity.setNumber(account.getNumber());
            entity.setAmount(account.getAmount());
        }

        return entity;
    }
}
