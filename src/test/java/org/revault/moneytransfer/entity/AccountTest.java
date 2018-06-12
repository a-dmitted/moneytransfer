package org.revault.moneytransfer.entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        account = new Account(100L);
        account.setNumber("0000 0000 0000 0000");
    }

    @Test
    public void isEnoughTest(){
        isEnough = account.isEnough(200L);
        assertEquals(isEnough, false);
    }

    @Test
    public void isEnoughAndSetTest(){
        account.setAmount(300L);
        isEnough = account.isEnough(200L);
        assertEquals(isEnough, true);
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
