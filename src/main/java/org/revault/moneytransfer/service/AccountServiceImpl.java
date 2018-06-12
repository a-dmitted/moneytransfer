package org.revault.moneytransfer.service;

import org.revault.moneytransfer.entity.Account;

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
        Account account = null;

        try {
            account = em.find(Account.class, number);
        }catch (Exception e) {
            System.out.println("Error retreiving Account: " + e.getMessage());
        }
        /*
        finally {
            em.close();
        }*/

        return account;
    }

    @Override
    public void delete(String number) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Account account = em.find(Account.class, number);

            if(account == null) {
                System.out.println("Error deleting Account: Account not found");
            }
            else {
                em.remove(account);
            }

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error deleting Account: " + e.getMessage());

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

            em.merge(account);

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error saving Account: " + e.getMessage());

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

            em.merge(account1);
            em.merge(account2);

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error saving two Accounts: " + e.getMessage());

            transaction.rollback();
        } /*finally {
            em.close();
        }*/
    }


}
