package com.jmcoin.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Connection {

    private Connection(){}

    private static EntityManagerFactory factory;
    private static EntityManager manager;

    /**
     * Return the manager of Entity
     * @return an the manager of Entity
     */
    public static EntityManager getManager() {
        if (manager == null || !manager.isOpen()) {
            if (factory == null || !factory.isOpen()) {
                factory = Persistence.createEntityManagerFactory("JMCoinPU");
            }
            manager = factory.createEntityManager();
        }
        return manager;
    }

    /**
     * Return a transaction
     * @return a transaction of the manager
     */
    public static EntityTransaction getTransaction() {
        return getManager().getTransaction();
    }


}
