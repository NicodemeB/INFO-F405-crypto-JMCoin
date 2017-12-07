package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Bundle;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public abstract class Peer {
		
	protected Bundle<? extends Object> bundle;
	
	public Peer() {
		this.bundle = new Bundle<>();
	}
	
	public Bundle<? extends Object> getBundle() {
		return bundle;
	}
	
	protected <T> Bundle<T> createBundle(Class<T> type) {
		Bundle<T> bundle = new Bundle<>();
		setBundle(bundle);
		return bundle;
	}
	
	protected void setBundle(Bundle<? extends Object> bundle) {
		this.bundle = bundle;
	}
	protected Boolean verifyBlockTransaction(Transaction trans, Chain chain, Output[] unspentOutputs) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException {
		if(SignaturesVerification.verifyTransaction(trans.getSignature(), trans.getBytes(false), KeyGenerator.getPublicKey(trans.getPubKey()))) 
		{
			String address = SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey());
			for(Input input : trans.getInputs()) 
			{
				Transaction prevTrans = chain.findInBlockChain(input.getPrevTransactionHash());
				if(prevTrans != null) 
				{ 
					// ce n'est pas un reward
					Output outToMe = null; //in previous transaction, find the output took as new input (outToMe)
					if(prevTrans.getOutputBack().getAddress().equals(address))
						outToMe = prevTrans.getOutputBack();
					else if (prevTrans.getOutputOut().getAddress().equals(address))
						outToMe = prevTrans.getOutputOut();
					else
						return false; //not normal
					
					boolean unspent = false;
					for(Output uo : unspentOutputs) {
						if(uo.equals(outToMe)) {
							unspent = true;
						}
					}
					if(!unspent)	return false; // Output déja dépensée
					
					if(outToMe.getAmount() == input.getAmount())
					{	
						//remove from unspent outputs
					}
					else 
					{
						return false; // Not normal, les montants doivent correspondre car on consomme tout l'ouput
					}	
				}
			}
			return true;
		}
		else
		{
			return false; // signatures non confirmée : vérifier si on ne retire pas les output à la toute fin quand tout est bon
		}
	}
}
