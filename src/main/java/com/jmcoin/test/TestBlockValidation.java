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

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.util.Arrays;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

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
		//if(!SignaturesVerification.verifyTransaction(trans.getSignature(), trans, trans.getPubKey())) return false;
		//TODO Maxime does the job
		int total = 0;
		for(Input i : trans.getInputs()) {
			Transaction t  = i.getTransaction();
			Output output = null;
			if(t.getOutputOut().getAddress() == SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey()) && t.getOutputOut().getAmount() == i.getAmount()) {
				output = t.getOutputOut();
			}
			else if(t.getOutputBack().getAddress() == SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey()) && t.getOutputBack().getAmount() == i.getAmount()) {
				output = t.getOutputBack();
			}
			if(output == null) {
				return false;
			}
			String unvf = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_UNSPENT_OUTPUTS, null);
			Output[] unspentOutputs = IOFileHandler.getFromJsonString(unvf, Output[].class);
			boolean unspent = false;
			for(Output uo : unspentOutputs) {
				if(uo.equals(output)) {
					unspent = true;
				}
			}
			if(!unspent) {
				return false;
			}
			//if i.output is not in unspent ouputs pool -> false
			//if i.output.address is not this.outputs[0].address -> false
			total += i.getAmount();
		}
		total -= trans.getOutputOut().getAmount();
		total -= trans.getOutputBack().getAmount();
		System.out.println("total = " + total);
		if(total != 0)
			return false;
		
		return true	;
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
//		transGenesis.setSignature(SignaturesVerification.);
		genesis.setTransactions(transGenesisList);
		genesis.setPrevHash(null);

		Input input1 = new Input();
		input1.setAmount(45);
		Input input2 = new Input();
		input2.setAmount(50);
		Output outputOut = new Output();
		outputOut.setAmount(80);
		Output outputBack = new Output();
		outputBack.setAmount(15);
		Transaction transactionToverify = new Transaction();
		transactionToverify.addInput(input1);
		transactionToverify.addInput(input2);
		transactionToverify.setOutputOut(outputOut);
		transactionToverify.setOutputBack(outputBack);
		if(validateTrans(chain, transactionToverify)) {
			System.out.println("Trans OK");
		}else {
			System.out.println("Trans KO");
		}
		
		blocks.add(genesis);
		genesis.setPrevHash(null);		
		try {
			buildBlock(genesis, new PrivateKey[] {keyConnard, keyConnasse, keyTest});
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for(Transaction transaction : block1.getTransactions()) {
//			if (!validateTrans(chain, transaction))return;
//		}
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
					transaction.setSignature(SignaturesVerification.signTransaction(transaction, privKey));
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