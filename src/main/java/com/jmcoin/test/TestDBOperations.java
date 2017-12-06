package com.jmcoin.test;

import com.google.gson.Gson;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.database.DatabaseFacade;
import com.jmcoin.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

public class TestDBOperations {

    public static void main(String[] args) throws Exception {
        Chain c = new Chain();
        Key[] keys = createKeys("andhisnameJohnCena!");
        //genesis
        Block genesis = new Block();
        Input inGenesis = new Input();
        inGenesis.setPrevTransactionHash(null);
        Output outGenesis = new Output();
        outGenesis.setAmount(42);
        outGenesis.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(keys[1].getEncoded()));
        Output outGenesisBack = new Output();
        outGenesisBack.setAmount(0);
        outGenesisBack.setAddress(null);
        Transaction transGenesis = new Transaction();
        transGenesis.setOutputBack(outGenesisBack);
        transGenesis.setOutputOut(outGenesis);
        addInput(inGenesis, transGenesis);
        transGenesis.setPubKey(keys[1].getEncoded());
        transGenesis.setSignature(SignaturesVerification.signTransaction(transGenesis.getBytes(false), (PrivateKey) keys[0]));
/*        Reward reward = new Reward();
        reward.setOutputBack(null);
        Output rewardOuputOut = new Output();
        rewardOuputOut.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey((PublicKey) keys[1]));
        rewardOuputOut.setAmount(50);
        reward.setOutputOut(rewardOuputOut);
        reward.setPubKey((PublicKey) keys[1]);
        reward.setSignature(SignaturesVerification.signTransaction(reward.getBytes(false), (PrivateKey) keys[0]));
        genesis.getTransactions().add(reward);*/
        genesis.getTransactions().add(transGenesis);
        genesis.setPrevHash(null);
        addOutputs(outGenesis, outGenesisBack, transGenesis);
        addBlock(genesis, c);
        DatabaseFacade.storeBlockChain(c);
        System.out.println(new Gson().toJson(DatabaseFacade.getStoredChain()));
        showMeTheObjectTypeOfEachChildrenBlockInTheGivenChain(DatabaseFacade.getStoredChain());
        //DatabaseFacade.removeBlockChain(c);
    }

    public static Key[] createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = new KeyGenerator(1024);
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        AES.encrypt(128, AESpw, inputPrivateKey, encryptedPrivateKey);
        return new Key[]{privateKey, publicKey};
    }

    private static void addBlock(Block b, Chain chain) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chain.class.getDeclaredField("blocks");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
		Map<String, Block> blocks = (Map<String, Block>) f.get(chain);
        blocks.put(b.getFinalHash() + b.getTimeCreation(), b);
    }

    private static void addOutputs(Output outOut, Output outBack, Transaction t) throws NoSuchFieldException, IllegalAccessException {
        Field outputOut = Transaction.class.getDeclaredField("outputOut");
        Field outputBack = Transaction.class.getDeclaredField("outputBack");
        outputOut.setAccessible(true);
        outputBack.setAccessible(true);
        outputOut.set(t, outOut);
        outputBack.set(t, outBack);
    }

    private static void addInput(Input in, Transaction t) throws NoSuchFieldException, IllegalAccessException {
        Field inputs = Transaction.class.getDeclaredField("inputs");
        inputs.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<Input> input = (List<Input>) inputs.get(t);
        input.add(in);
    }

    //#norage
    private static void showMeTheObjectTypeOfEachChildrenBlockInTheGivenChain(Chain c) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chain.class.getDeclaredField("blocks");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
		Map<String, Block> blocks = (Map<String, Block>) f.get(c);
        blocks.entrySet().forEach(entry -> {
            for (Transaction tr : entry.getValue().getTransactions()) {
                System.out.println(tr.getClass());
            }
        });
    }

}
