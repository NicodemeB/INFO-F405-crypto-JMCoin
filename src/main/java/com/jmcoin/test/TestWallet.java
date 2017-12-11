package com.jmcoin.test;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
/**
 *
 * @author Famille
 */
public class TestWallet {
    public static void main(String args[]){
    	try {
    		UserNode userNode = new UserNode(args[0]);
    		//PrivateKey privKey = userNode.getWallet().getKeys().keySet().iterator().next();
    		//TestMasterNode.runMaster(privKey, userNode.getWallet().getKeys().get(privKey));
			//TestRelay.run();
	    	UserJMProtocolImpl protocol = new UserJMProtocolImpl(userNode, args[1]);
	    	userNode.getWallet().computeBalance(protocol);
	    	System.out.println(userNode.getWallet().getAddresses());
	    	System.out.println("Balance: "+userNode.getWallet().getBalance());
	    	/*userNode.createTransaction(protocol, SignaturesVerification.DeriveJMAddressFromPubKey(userNode.getWallet().getKeys().get(privKey).getEncoded()), "@destination", 12, privKey, userNode.getWallet().getKeys().get(privKey));
	    	userNode.getWallet().computeBalance(protocol);
	    	System.out.println("Balance: "+userNode.getWallet().getBalance());*/
		} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e) {
			e.printStackTrace();
		} 	
    }
}

