package com.jmcoin.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
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
		if(!SignaturesVerification.verifyTransaction(trans.getSignature(), trans.getBytes(false), trans.getPubKey())) return false;
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
				
		//genesis
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
		genesis.setTransactions(transGenesisList);
		genesis.setPrevHash(null);		
		try {
			buildBlock(genesis, new PrivateKey[] {keyConnard, keyConnasse, keyTest});
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		/*for(Transaction transaction : block1.getTransactions()) {
			if (!validateTrans(chain, transaction))return;
		}*/
		System.out.println("It's alright");
	}
	
	private static Chain buildBlock(Block genesis, PrivateKey[] privKeys) throws NoSuchAlgorithmException, InterruptedException, ExecutionException, InvalidKeyException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException {
		Random rand = new Random();
		Chain chain = new Chain();
		Mining mining = new Mining();
		Block prevBlock = null;
		for(int i = 0; i < 10; i++) {
			Block block;
			if(i != 0) {
				block = new Block();
				block.setPrevHash(prevBlock.getFinalHash());
				prevBlock = block;
				List<Transaction> transactions = new ArrayList<>();
				int limit = rand.nextInt(5);
				PrivateKey privKey = privKeys[rand.nextInt(3)];
				for(int j = 0; j < limit; j++) {
					Transaction transaction = new Transaction();
					for(int k = 0; k < limit; k++) {
						Input in = new Input();
						in.setAmount(rand.nextInt(20));
//						in.setPrevTransactionHash(//prevTransactionHash);
						transaction.getInputs().add(in);
					}
					Output outputOut = new Output();
					Output outputBack = new Output();
					transaction.setOutputOut(outputOut);
					transaction.setOutputBack(outputBack);
					transaction.setSignature(SignaturesVerification.signTransaction(transaction.getBytes(false), privKey));
					transaction.setPubKey(keys.get(privKey));
					transactions.add(transaction);
				}
				block.setTransactions(transactions);				
			}
			else {
				block = genesis;
				prevBlock = genesis;
			}
			block.setDifficulty(4);
			mining.setBlock(block);
			mining.mine();
			block.setTimeCreation(System.currentTimeMillis());
			chain.getBlocks().put(block.getFinalHash(), block);
			System.out.println(new Gson().toJson(block));
		}
		return chain;
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
