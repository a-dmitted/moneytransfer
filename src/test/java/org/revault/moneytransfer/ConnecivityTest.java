package org.revault.moneytransfer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ConnecivityTest {
    EntityManagerFactory emf;
    EntityManager em;

    @Before
    public void setUp(){
        emf = Persistence.createEntityManagerFactory("moneytransfer-unit");
        em = emf.createEntityManager();
    }

    @Test
    public void testConnectivity(){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        tx.rollback();
    }

    @After
    public void cleanUp(){
        em.close();
        emf.close();
    }
}
