package org.revault.moneytransfer.entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.revault.moneytransfer.api.data.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;

public class AccountTest {
    EntityManagerFactory emf;
    EntityManager em;
    private Account account;
    boolean isEnough;
    @Before
    public void setUp(){
        emf = Persistence.createEntityManagerFactory("moneytransfer-unit");
        em = emf.createEntityManager();
        account = new Account("0000 0000 0000 0000", 100L);
    }

    @Test
    public void persistEntityTest(){
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            em.merge(account);

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Error saving Account: " + e.getMessage());

            transaction.rollback();
        }
    }

    @After
    public void cleanUp(){
        em.close();
        emf.close();
    }

}
