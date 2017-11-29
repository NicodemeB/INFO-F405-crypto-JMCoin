package com.jmcoin.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class TestBlockValidation {
	
	private static HashMap<PrivateKey, PublicKey> keys = new HashMap<>();
	
	private static Transaction findInBlockChain(Chain chain, String hashTrans) {
		for(String s : chain.getBlocks().keySet()) {
			Block b = chain.getBlocks().get(s);
			for(Transaction trans : b.getTransactions()) {
				if(trans.getHash().equals(hashTrans))return trans;
			}
		}
		return null;
	}
	
	/**
	 * Validates the {@link Transaction}
	 * Iterates over {@link Input} and {@link Output} and validates
	 * @param chain
	 * @param trans
	 * @return
	 * @throws IOException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	private static boolean validateTrans(Chain chain, Transaction trans) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException {
		if(!SignaturesVerification.verifyTransaction(trans.getSignature(), trans, trans.getPubKey())) return false;
		//TODO Maxime does the job
		return false;
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		createKeys("connard");
		createKeys("connasse");
		createKeys("test");
		
		PrivateKey keyConnard = keys.keySet().iterator().next();
		PrivateKey keyConnasse = keys.keySet().iterator().next();
		PrivateKey keyTest = keys.keySet().iterator().next();
		
		Chain chain = new Chain();
		List<Block> blocks = new ArrayList<>();
		
		Block genesis = new Block();
		List<Transaction> transGenesisList = new ArrayList<>();
		Input inGenesis = new Input();
		inGenesis.setAmount(0);
		inGenesis.setPrevTransactionHash(null);
		Output outGenesis = new Output();
		outGenesis.setAmount(42);
		outGenesis.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(keyConnard)));
		Output outGenesisBack = new Output();
		outGenesisBack.setAmount(0);
		outGenesisBack.setAddress(null);
		Transaction transGenesis = new Transaction();
		transGenesis.setOutputBack(outGenesisBack);
		transGenesis.setOutputOut(outGenesis);
		transGenesis.addInput(inGenesis);
		transGenesis.setPubKey(keys.get(keyConnasse));
		transGenesisList.add(transGenesis);
//		transGenesis.setSignature(SignaturesVerification.);
		genesis.setTransactions(transGenesisList);
		genesis.setPrevHash(null);
		
		
		blocks.add(genesis);
		try {
			buildBlock(blocks);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*for(Transaction transaction : block1.getTransactions()) {
			if (!validateTrans(chain, transaction))return;
		}*/
		System.out.println("It's alright");
	}
	
	private static void buildBlock(List<Block> blocks) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
		Mining mining = new Mining();
		for(int i = 0; i < blocks.size(); i++) {
			if(i != 0) {
				blocks.get(i).setPrevHash(blocks.get(i-1).getFinalHash());
			}
			mining.setBlock(blocks.get(i));
			mining.mine();
			blocks.get(i).setTimeCreation(System.currentTimeMillis());
		}
	}
	
	 public static void createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException{
		KeyGenerator keyGen = new KeyGenerator(1024);
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);
        keys.put(privateKey, publicKey);
     }
}
