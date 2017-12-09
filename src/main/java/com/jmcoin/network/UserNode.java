package com.jmcoin.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class UserNode extends Peer{

	private Wallet wallet;
	private Map<String,Output> usedOutputs;
	private static final String DELIMITER = "%";
	
	public UserNode(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		this.wallet = new Wallet(password);
	}
	
	public Transaction[] getAvailableTransactionsForAddress(UserJMProtocolImpl protocol,String fromAddress, Map<String,Output> unspentOutputs, Wallet wallet){
		Transaction[] addressTransactions;
		ArrayList<Transaction> availableTransactions = new ArrayList<Transaction>();
		try {
			addressTransactions = protocol.downloadObject(NetConst.GIVE_ME_TRANS_TO_THIS_ADDRESS, "[\""+fromAddress+"\"]", protocol.getClient());
			for(int i = 0 ; i < addressTransactions.length; i++){
				
				String keyOut = Hex.toHexString(addressTransactions[i].getHash())+DELIMITER+addressTransactions[i].getOutputOut().getAddress();
				String keyBack = Hex.toHexString(addressTransactions[i].getHash())+DELIMITER+addressTransactions[i].getOutputBack().getAddress();
					
				if(addressTransactions[i].getOutputBack().getAddress().equals(fromAddress)){	
					//Si l'output est contenue dans le pool des output disponibles et que l'output n'est pas en attente
					if((unspentOutputs.containsKey(keyBack)) && wallet.getPendingOutputs().containsKey(keyBack) == false){
						availableTransactions.add(addressTransactions[i]);
					}
				}
				else if((addressTransactions[i].getOutputOut().getAddress().equals(fromAddress))){
					if((unspentOutputs.containsKey(keyOut)) && wallet.getPendingOutputs().containsKey(keyOut) == false){
						availableTransactions.add(addressTransactions[i]);
					}
				}
			}
			return (Transaction[])availableTransactions.toArray();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public Transaction createTransaction(UserJMProtocolImpl protocol, String fromAddress, String toAddress,
		double amountToSend, PrivateKey privKey, PublicKey pubKey, Wallet wallet) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, FileNotFoundException, SignatureException{
		Map<String,Output> pendingOutputs = this.wallet.getPendingOutputs();
		Map<String, Output> unspentOutputs = protocol.downloadObject(NetConst.GIVE_ME_UNSPENT_OUTPUTS, null, protocol.getClient());
		wallet.updatePendingOutputs(unspentOutputs);
		Transaction [] addressTransactions = getAvailableTransactionsForAddress(protocol,fromAddress, unspentOutputs, wallet);
		if(addressTransactions == null) return null;
		Transaction tr = new Transaction();
        double totalOutputAmount = 0;
        int i = 0;
        while( i < addressTransactions.length && totalOutputAmount < amountToSend)
        {
        		String key = Hex.toHexString(addressTransactions[i].getHash());
            if(addressTransactions[i].getOutputBack().getAddress().equals(fromAddress)){
            		//verifier si Out pas encore utilisée localement
            		key += DELIMITER+addressTransactions[i].getOutputBack().getAddress();
            		if(pendingOutputs.containsKey(key) == false)
            		{
            			totalOutputAmount+= addressTransactions[i].getOutputBack().getAmount();
                     tr.addInput(new Input(addressTransactions[i].getOutputBack().getAmount(),addressTransactions[i].getHash()));
                     this.usedOutputs.put(key, addressTransactions[i].getOutputBack());
            		}
            }
            else if((addressTransactions[i].getOutputOut().getAddress().equals(fromAddress))){
	            	key += DELIMITER+addressTransactions[i].getOutputOut().getAddress();
	            	//verifier si Out pas encore utilisée localement
	            	if(pendingOutputs.containsKey(key) == false)
	        		{
		            	totalOutputAmount+= addressTransactions[i].getOutputOut().getAmount();
		            	tr.addInput(new Input(addressTransactions[i].getOutputOut().getAmount(),addressTransactions[i].getHash()));
		            	this.usedOutputs.put(key, addressTransactions[i].getOutputBack());
	        		}
            }
            else {
            		System.out.println("Wallet : No output belonging to this address");
            		return null;
            }  
            i++;
        }
        if(amountToSend <= totalOutputAmount){
            Output oOut = new Output(amountToSend, toAddress);
            Output oBack = new Output(totalOutputAmount-amountToSend, fromAddress);
            tr.setPubKey(pubKey.getEncoded());
            tr.setOutputBack(oBack);
            tr.setOutputOut(oOut);
            tr.setSignature(SignaturesVerification.signTransaction(tr.getBytes(false), privKey));
            tr.computeHash();
          	this.wallet.getPendingOutputs().putAll(usedOutputs);
            return tr;
        }
        else{
            System.out.println("Wallet: Insuficient amount for that address");
        }
		return null;
    }

	public Wallet getWallet() {
		return wallet;
	}
	
	public void debugUserNode(UserJMProtocolImpl protocol) throws IOException {
		Chain c1 = protocol.downloadObject(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null, protocol.getClient());
		System.err.println("User: chain received " + c1);
		Chain c2 = protocol.downloadObject(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null, protocol.getClient());
		System.err.println("User: chain received " + c2);
	}
}
