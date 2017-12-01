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
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Genesis;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.network.MasterNode;

public class TestProcessBlock {
	
	private static HashMap<PrivateKey, PublicKey> keys = new HashMap<>();
	
	private static void mine(Block block) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
		Mining mining = new Mining();
		mining.mine(block);
	}
	
	public static PrivateKey createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException{
		KeyGenerator keyGen = new KeyGenerator(1024);
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);
        keys.put(privateKey, publicKey);
        return privateKey;
     }
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		PrivateKey unoKey = createKeys("uno!");
		PrivateKey dosKey = createKeys("dos!");
		PrivateKey treKey = createKeys("tre!");
		
		Genesis genesis = Genesis.getInstance();
		try {
			mine(genesis);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		Block block1 = new Block();
		Transaction trans1_1 = new Transaction();
		trans1_1.setPubKey(keys.get(dosKey));
		Input input1_1_1 = new Input();
		input1_1_1.setAmount(genesis.getOuputOut());
		input1_1_1.setPrevTransactionHash(genesis.getFinalHash().getBytes());
		Output outputOut1_1_1 = new Output();
		outputOut1_1_1.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(treKey))); //from Dos to Tre
		outputOut1_1_1.setAmount(30);
		Output outputBack1_1_1 = new Output();
		outputBack1_1_1.setAmount(12);
		outputBack1_1_1.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(trans1_1.getPubKey()));
		trans1_1.addInput(input1_1_1);
		trans1_1.setOutputBack(outputBack1_1_1);
		trans1_1.setOutputOut(outputOut1_1_1);
		trans1_1.setSignature(SignaturesVerification.signTransaction(trans1_1.getBytes(false), dosKey));
		Transaction trans1_2 = new Transaction();
		trans1_2.setPubKey(keys.get(dosKey));
		Input input1_1_2 = new Input();
		input1_1_2.setAmount(genesis.getOuputOut());
		input1_1_2.setPrevTransactionHash(genesis.getFinalHash().getBytes());
		Output outputOut1_1_2 = new Output();
		outputOut1_1_2.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(unoKey))); //from Dos to Uno
		outputOut1_1_2.setAmount(16);
		Output outputBack1_1_2 = new Output();
		outputBack1_1_2.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(trans1_2.getPubKey()));
		outputBack1_1_2.setAmount(26);
		trans1_2.addInput(input1_1_2);
		trans1_2.setOutputOut(outputOut1_1_2);
		trans1_2.setOutputBack(outputBack1_1_2);
		trans1_2.setSignature(SignaturesVerification.signTransaction(trans1_2.getBytes(false), dosKey));
		trans1_2.addInput(input1_1_2);
		trans1_1.computeHash();
		trans1_2.computeHash();
		block1.getTransactions().add(trans1_1);
		block1.getTransactions().add(trans1_2);
		block1.setDifficulty(1);
		block1.setPrevHash(genesis.getFinalHash());
		try {
			mine(block1);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(unoKey)));
		System.out.println(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(dosKey)));
		System.out.println(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(treKey)));
		System.out.println();
		MasterNode master = MasterNode.getInstance();
		master.processBlock(genesis);
		printUnspent(master);
		master.processBlock(block1);
		printUnspent(master);
	}
	
	private static void printUnspent(MasterNode master) {
		for(String key : master.getUnspentOutputs().keySet()) {
			Output unspent = master.getUnspentOutputs().get(key);
			System.out.println(key + "; amount= " + unspent.getAmount());
		}
		System.out.println("---------------------------------");
	}

}
