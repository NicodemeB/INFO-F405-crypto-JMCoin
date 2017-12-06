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

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class UserNode extends Peer{

	private Wallet wallet;
	
	public UserNode(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		this.wallet = new Wallet(password);
	}
	
	public Transaction createTransaction(UserJMProtocolImpl protocol, String fromAddress, String toAddress,
			double amountToSend, PrivateKey privKey, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, FileNotFoundException, SignatureException{
    	Transaction[] addressTransactions = protocol.downloadObject(Transaction[].class, NetConst.GIVE_ME_TRANS_TO_THIS_ADDRESS, fromAddress, protocol.getClient());
    	Transaction tr = new Transaction();
        double totalOutputAmount = 0;
        for(int i = 0 ; i < addressTransactions.length; i++){
            if(addressTransactions[i].getOutputBack().getAddress().equals(fromAddress)){
                totalOutputAmount+= addressTransactions[i].getOutputBack().getAmount();
                tr.addInput(new Input(fromAddress,addressTransactions[i].getOutputBack().getAmount(),addressTransactions[i].getHash()));
            }
            else if((addressTransactions[i].getOutputOut().getAddress().equals(fromAddress))){
            	totalOutputAmount+= addressTransactions[i].getOutputOut().getAmount();
            	tr.addInput(new Input(fromAddress,addressTransactions[i].getOutputOut().getAmount(),addressTransactions[i].getHash()));
            }
            else{
            	System.out.println("Wallet : No output belonging to this address");
            }           
        }
        if(amountToSend <= totalOutputAmount){
            Output oOut = new Output(amountToSend, toAddress);
            Output oBack = new Output(totalOutputAmount-amountToSend, fromAddress);
            tr.setSignature(SignaturesVerification.signTransaction(tr.getBytes(false), privKey));
            tr.computeHash();
            tr.setPubKey(pubKey.getEncoded());
            tr.setOutputBack(oBack);
            tr.setOutputOut(oOut);
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
}
