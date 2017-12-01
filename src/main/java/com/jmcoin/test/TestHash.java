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
import java.util.List;

import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class TestHash {

	static PrivateKey privKey;
	static PublicKey pubKey;
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		
		Block genesis = new Block();
		List<Transaction> transGenesisList = new ArrayList<>();
		Input inGenesis = new Input();
		inGenesis.setAmount(0);
		inGenesis.setPrevTransactionHash(null);		
		Output outGenesis = new Output();
		outGenesis.setAmount(42);
		Output outGenesisBack = new Output();
		outGenesisBack.setAmount(45);
		outGenesisBack.setAddress("connard");
		Transaction transGenesis = new Transaction();
		transGenesis.setOutputBack(outGenesisBack);
		transGenesis.setOutputOut(outGenesis);
		transGenesis.getInputs().add(inGenesis);
		genesis.setTransactions(transGenesisList);
		genesis.setPrevHash(null);
		createKeys("coucoucocu");
		transGenesis.setSignature(SignaturesVerification.signTransaction(transGenesis.getBytes(false), privKey));
		System.out.println(SignaturesVerification.verifyTransaction(transGenesis.getSignature(), transGenesis.getBytes(false), pubKey));
		try {
			transGenesis.computeHash();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
        pubKey = publicKey;
        privKey = privateKey;
     }
}
