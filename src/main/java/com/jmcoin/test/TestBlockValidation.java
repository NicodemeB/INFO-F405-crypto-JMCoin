package com.jmcoin.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.util.encoders.Hex;

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
import com.jmcoin.model.Wallet;
import com.jmcoin.util.BytesUtil;

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
		if(SignaturesVerification.verifyTransaction(trans.getSignature(), trans, trans.getPubKey())) return false;
		for(int i = 0; i < trans.getInputs().size(); i++) {
			Input input = trans.getInputs().get(i);
			int spent = 0;
			for(Output output : trans.getOutputs()) {
				if(output.getInputIndex() == i && Arrays.equals(output.getPubKey().getEncoded(), trans.getPubKey().getEncoded()))
					spent+=output.getAmount();
			}
			if(input.getAmount() > spent)return false; 
			Transaction prevTrans = findInBlockChain(chain, input.getPrevTransHash());
			if(prevTrans == null) {
				//TODO ok, there is no prev. trans. Means that it's where the money was created
			}
			else {
				int index = input.getOutputPrevTrans();
				if(index == Input.INDEX_REWARD)
					continue; //should not happen since prevTrans was null
				Output prevOut = prevTrans.getOutputs().get(index);
				if(!SignaturesVerification.verifyTransaction(trans.getSignature(), trans, prevOut.getPubKey())) {
					return false;
				}
				int prevSpent = prevOut.getAmount();
				Input prevInput = prevTrans.getInputs().get(prevOut.getInputIndex());
				while((index = prevInput.getOutputPrevTrans()) != Input.INDEX_REWARD) {
					Transaction prevTrans2 = findInBlockChain(chain, prevInput.getPrevTransHash());
					Output prevOut2 = prevTrans2.getOutputs().get(index);
					prevSpent+=prevOut2.getAmount();
					prevInput = prevTrans2.getInputs().get(prevOut2.getInputIndex());
				}
				if (prevInput.getAmount() < prevSpent+spent) {
					return false;
				}
			}
			

		}
		return false;
		
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		createKeys("connard");
		createKeys("connasse");
		createKeys("test");
		
		List<Block> blocks = new ArrayList<>();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		
		Input genesis = new Input();
		genesis.setAmount(0);
		genesis.setOutputPrevTrans(Input.INDEX_REWARD);
		Output out = new Output();
		out.setAmount(10);
		out.setPubKey(keys.get(keys.keySet().iterator().next()));
		Transaction trans1 = new Transaction();
		trans1.addInputOutput(genesis,out);
		digest.update(BytesUtil.toByteArray(trans1));
		trans1.setHash(Hex.toHexString(digest.digest()));
		Block block1 = new Block();
		block1.getTransactions().add(trans1);
		
		Block block2 = new Block();
		
		blocks.add(block1);
		blocks.add(block2);
		try {
			buildBlock(blocks);
		} catch (InterruptedException | ExecutionException e) {
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
