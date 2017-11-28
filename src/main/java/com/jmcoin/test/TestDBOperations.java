package com.jmcoin.test;

import com.google.gson.Gson;
import com.jmcoin.database.DatabaseFacade;
import com.jmcoin.model.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestDBOperations {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Chain c = new Chain();
        Block block = new Block();
        block.setFinalHash("aaa");
        block.setTimeCreation(System.currentTimeMillis());
        List<Transaction> t = new ArrayList<>();
        Transaction tr = new Transaction();
        Input in = new Input();
        in.setAmount(2);
        in.setHashSha256("hashIn");
        in.setSignature("signIn");
        Output out = new Output();
        out.setAmount(2);
        out.setInputIndex(42);
        out.setPublicKey("pub");
        addInputOutput(in, out, tr);
        t.add(tr);
        block.setTransactions(t);
        addBlock(block, c);
        DatabaseFacade.storeBlockChain(c);
        System.out.println(new Gson().toJson(DatabaseFacade.getStoredChain()));
    }

    private static void addBlock(Block b, Chain chain) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chain.class.getDeclaredField("blocks");
        f.setAccessible(true);
        Map<String, Block> blocks = (Map<String, Block>) f.get(chain);
        blocks.put(b.getFinalHash() + b.getTimeCreation(), b);
    }

    private static void addInputOutput(Input in, Output out, Transaction t) throws NoSuchFieldException, IllegalAccessException {
        Field inputs = Transaction.class.getDeclaredField("inputs");
        inputs.setAccessible(true);
        Field outputs = Transaction.class.getDeclaredField("outputs");
        outputs.setAccessible(true);
        List<Input> input = (List<Input>) inputs.get(t);
        List<Output> output = (List<Output>) outputs.get(t);
        input.add(in);
        output.add(out);
    }

}
