package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.err.ServiceExeption;

import javax.inject.Singleton;
import javax.persistence.*;

import static org.revault.moneytransfer.err.Error.*;

@Singleton
public class AccountServiceImpl implements AccountService{
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("moneytransfer-unit");
    @Override
    public Account retrieve(String number) throws DaoException{
        EntityManager em = emf.createEntityManager();
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
        EntityManager em = emf.createEntityManager();
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
        EntityManager em = emf.createEntityManager();
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
        EntityManager em = emf.createEntityManager();
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

    @Override
    public void makeTransfer(String debitAcc, String creditAcc, Long amount) throws ServiceExeption {
        try{
            EntityManager em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            AccountEntity debitAccountEntity = em.find(AccountEntity.class, debitAcc, LockModeType.PESSIMISTIC_WRITE);
            AccountEntity creditAccountEntity = em.find(AccountEntity.class, creditAcc, LockModeType.PESSIMISTIC_WRITE);
            if(isEnough(debitAccountEntity, amount)){
                debitAccountEntity.setAmount(debitAccountEntity.getAmount()-amount);
                creditAccountEntity.setAmount(creditAccountEntity.getAmount()+amount);
                em.merge(debitAccountEntity);
                em.merge(creditAccountEntity);
                transaction.commit();
            }
            else{
                rollbackAtcitveTran(transaction);
                throw new ServiceExeption(NOT_ENOUGH_MONEY);
            }
        }
        catch(ServiceExeption ex){
            throw ex;
        }
        catch(Exception ex){
            throw new ServiceExeption(DATA_LAYER_ERR);
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

    @Override
    public EntityManagerFactory getEmf() {
        return emf;
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

    private boolean isEnough(AccountEntity account, Long amount){
        if(account.getAmount()>=amount)
            return true;
        else
            return false;

    }
}
