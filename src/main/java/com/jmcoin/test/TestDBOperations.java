package com.jmcoin.test;

import com.google.gson.Gson;
import com.jmcoin.database.DatabaseFacade;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;

import java.lang.reflect.Field;
import java.util.Map;

public class TestDBOperations {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Chain c = new Chain();
        Block block = new Block();
        block.setFinalHash("aaa");
        block.setTimeCreation(System.currentTimeMillis());
        addBlock(block, c);
        DatabaseFacade.storeBlockChain(c);
        c = DatabaseFacade.getStoredChain();
        System.out.println(new Gson().toJson(DatabaseFacade.getStoredChain()));
    }

    private static void addBlock(Block b, Chain chain) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chain.class.getDeclaredField("blocks");
        f.setAccessible(true);
        Map<String, Block> blocks = (Map<String, Block>) f.get(chain);
        blocks.put(b.getFinalHash() + b.getTimeCreation(), b);
    }

}
