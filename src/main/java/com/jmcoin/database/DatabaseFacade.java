package com.jmcoin.database;

import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Transaction;

import javax.persistence.NoResultException;
import java.util.Arrays;
import java.util.List;

public class DatabaseFacade {

    private DatabaseFacade(){}

    public static void storeBlockChain(Chain chain){
        Connection.getTransaction().begin();
        if(!Connection.getManager().createNamedQuery("Chain.findAll").getResultList().isEmpty()){
            Connection.getTransaction().rollback();
            throw new IllegalStateException("There's already a chain in the database");
        }
        Connection.getManager().persist(chain);
        Connection.getTransaction().commit();
    }

    public static Chain getStoredChain(){
        Connection.getTransaction().begin();
        Chain chain;
        try {
            chain = (Chain) Connection.getManager().createNamedQuery("Chain.findAll").getSingleResult();
        }catch(NoResultException e){
            chain = null;
        }
        Connection.getTransaction().commit();
        return chain;
    }

    public static void updateChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().merge(chain);
        Connection.getTransaction().commit();
    }

    public static void removeBlockChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().remove(chain);
        Connection.getTransaction().commit();
    }

    public static List<Transaction> getAllTransactionsWithAddress(String... addresses){
        List<String> addr = Arrays.asList(addresses);
        Connection.getTransaction().begin();
        @SuppressWarnings("unchecked")
		List<Transaction> transactions = Connection.getManager().createQuery("SELECT t from Transaction t where t.outputOut.address IN :addr1 or t.outputBack.address IN :addr2")
                .setParameter("addr1", addr)
                .setParameter("addr2", addr).getResultList();
        Connection.getTransaction().commit();
        return transactions;
    }

    public static Block getBlockWithHash(String finalHash){
        Connection.getTransaction().begin();
        Block b;
        try {
            b = (Block) Connection.getManager().createQuery("SELECT b from Block b where b.finalHash = :finalHash").setParameter("finalHash", finalHash).getSingleResult();
        }catch(NoResultException e){
            b = null;
        }
        Connection.getTransaction().commit();
        return b;
    }

    public static Block getLastBlock() {
        Connection.getTransaction().begin();
        Block b;
        try {
            b = (Block) Connection.getManager().createQuery("SELECT b from Block b where b.id = (SELECT max(b2.id) from Block b2)").getSingleResult();
        }catch(NoResultException e){
            b = null;
        }
        Connection.getTransaction().commit();
        return b;
    }

}
