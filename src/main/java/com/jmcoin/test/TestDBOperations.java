package com.jmcoin.test;

import com.google.gson.Gson;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.database.Connection;
import com.jmcoin.database.DatabaseFacade;
import com.jmcoin.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestDBOperations {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
        Chain c = new Chain();
        Key[] keys = createKeys("andhisnameJohnCena!");
        //genesis
  		Block genesis = new Block();
  		List<Transaction> transGenesisList = new ArrayList<>();
  		Input inGenesis = new Input();
  		inGenesis.setAmount(0);
  		inGenesis.setPrevTransactionHash(null);
  		Output outGenesis = new Output();
  		outGenesis.setAmount(42);
  		outGenesis.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey((PublicKey) keys[1]));
  		Output outGenesisBack = new Output();
  		outGenesisBack.setAmount(0);
  		outGenesisBack.setAddress(null);
  		Transaction transGenesis = new Transaction();
  		transGenesis.setOutputBack(outGenesisBack);
  		transGenesis.setOutputOut(outGenesis);
  		transGenesis.addInput(inGenesis);
  		transGenesis.setPubKey((PublicKey) keys[1]);
  		transGenesis.setSignature(SignaturesVerification.signTransaction(transGenesis.getBytes(false), (PrivateKey) keys[0]));
  		transGenesisList.add(transGenesis);
  		genesis.setTransactions(transGenesisList);
  		genesis.setPrevHash(null);
  		addInput(inGenesis, transGenesis);
  		addOutputs(outGenesis, outGenesisBack, transGenesis);
  		addInput(inGenesis, transGenesis);
        Reward reward= new Reward();
        reward.setOutputBack(null);
        Output rewardOuputOut = new Output();
        rewardOuputOut.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey((PublicKey) keys[1]));
        rewardOuputOut.setAmount(50);
        reward.setOutputOut(rewardOuputOut);
        reward.setPubKey((PublicKey) keys[1]);
        reward.setSignature(SignaturesVerification.signTransaction(reward.getBytes(false), (PrivateKey) keys[0]));
  		transGenesisList.add(reward);
        genesis.setTransactions(transGenesisList);
        addBlock(genesis, c);
        DatabaseFacade.storeBlockChain(c);
        System.out.println(new Gson().toJson(DatabaseFacade.getStoredChain()));
        showMeTheObjectTypeOfEachChildrenBlockInTheGivenChain(DatabaseFacade.getStoredChain());
        Connection.getTransaction().begin();
        Connection.getManager().remove(c);
        Connection.getTransaction().commit();
    }

	 public static Key[] createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException{
		KeyGenerator keyGen = new KeyGenerator(1024);
       keyGen.createKeys();
       PrivateKey privateKey = keyGen.getPrivateKey();
       PublicKey publicKey = keyGen.getPublicKey();
       char[] AESpw = password.toCharArray();
       ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
       ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
       AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);
       return new Key[] {privateKey, publicKey};
    }
	 
    private static void addBlock(Block b, Chain chain) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chain.class.getDeclaredField("blocks");
        f.setAccessible(true);
        Map<String, Block> blocks = (Map<String, Block>) f.get(chain);
        blocks.put(b.getFinalHash() + b.getTimeCreation(), b);
    }

    private static void addOutputs(Output outOut, Output outBack, Transaction t) throws NoSuchFieldException, IllegalAccessException {
        Field outputOut = Transaction.class.getDeclaredField("outputOut");
        Field outputBack = Transaction.class.getDeclaredField("outputBack");
        outputOut.setAccessible(true);
        outputBack.setAccessible(true);
        outputOut.set(outOut, t);
        outputBack.set(outBack, t);
    }
    
    private static void addInput(Input in, Transaction t) throws NoSuchFieldException, IllegalAccessException {
        Field inputs = Transaction.class.getDeclaredField("inputs");
        inputs.setAccessible(true);
        List<Input> input = (List<Input>) inputs.get(t);
        input.add(in);
    }
    //#norage
    private static void showMeTheObjectTypeOfEachChildrenBlockInTheGivenChain(Chain c) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chain.class.getDeclaredField("blocks");
        f.setAccessible(true);
        Map<String, Block> blocks = (Map<String, Block>) f.get(c);
        blocks.entrySet().forEach(entry ->{
            for(Transaction tr : entry.getValue().getTransactions()){
                System.out.println(tr.getClass());
            }
        });
    }

}