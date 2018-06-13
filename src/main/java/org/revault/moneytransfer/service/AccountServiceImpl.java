package org.revault.moneytransfer.service;

import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.entity.AccountEntity;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

@Singleton
public class AccountServiceImpl implements AccountService{
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("moneytransfer-unit");
    private EntityManager em = emf.createEntityManager();
    @Override
    public Account retreive(String number){
        AccountEntity accountEntity = null;

        try {
            accountEntity = em.find(AccountEntity.class, number);
        }catch (Exception e) {
            System.out.println("Error retreiving AccountEntity: " + e.getMessage());
        }
        /*
        finally {
            em.close();
        }*/

        return entityToAccount(accountEntity);
    }

    @Override
    public void delete(String number) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            AccountEntity accountEntity = em.find(AccountEntity.class, number);

            if(accountEntity == null) {
                System.out.println("Error deleting AccountEntity: AccountEntity not found");
            }
            else {
                em.remove(accountEntity);
            }

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error deleting AccountEntity: " + e.getMessage());

            transaction.rollback();
        } /*
        finally {
            em.close();
        }*/
    }

    @Override
    public void save(Account account) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            em.merge(accountToEntity(account));

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error saving AccountEntity: " + e.getMessage());

            transaction.rollback();
        } /*finally {
            em.close();
        }*/
    }

    @Override
    public void saveTwo(Account account1, Account account2){
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            em.merge(accountToEntity(account1));
            em.merge(accountToEntity(account2));

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error saving two Accounts: " + e.getMessage());

            transaction.rollback();
        } /*finally {
            em.close();
        }*/
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
