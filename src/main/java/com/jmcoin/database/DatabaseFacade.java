package com.jmcoin.database;

import com.jmcoin.model.Chain;

public class DatabaseFacade {

    private DatabaseFacade(){}

    public static void storeBlockChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().persist(chain);
        Connection.getTransaction().commit();
    }

    public static Chain getStoredChain(){
        Connection.getTransaction().begin();
        Chain chain = (Chain) Connection.getManager().createNamedQuery("Chain.findAll").getSingleResult();
        Connection.getTransaction().commit();
        return chain;
    }

    public static void removeBlockChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().remove(chain);
        Connection.getTransaction().commit();
    }

}
