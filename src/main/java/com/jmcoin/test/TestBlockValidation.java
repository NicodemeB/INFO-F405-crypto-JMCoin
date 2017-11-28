package com.jmcoin.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class TestBlockValidation {
	
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
			int alreadySpent;
			for(Output output : trans.getOutputs()) {
				if(output.getInputIndex() == i && Arrays.equals(output.getPubKey().getEncoded(), trans.getPubKey().getEncoded()))
					spent+=output.getAmount();
			}
			//if(input.getAmount() != spent) 
			Transaction prevTrans = findInBlockChain(chain, input.getPrevTransHash());
			if(prevTrans == null) {
				//TODO ok, there is no prev. trans. Means that it's where the money was created
			}
			else {
				int index = input.getOutputPrevTrans();
				if(index == Input.INDEX_REWARD) {
					//it's a reward
				}
				else {
					Output prevOut = prevTrans.getOutputs().get(index);
					if(!SignaturesVerification.verifyTransaction(trans.getSignature(), trans, prevOut.getPubKey())) {
						return false;
					}
					//check balance
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
			

		}
		return false;
		
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException {
		Chain chain = new Chain();//TODO change this
		List<Transaction> transactions = new ArrayList<>();
		for(Transaction transaction : transactions) {
			if (!validateTrans(chain, transaction))return;
		}
		System.out.println("It's alright");
	}

}
