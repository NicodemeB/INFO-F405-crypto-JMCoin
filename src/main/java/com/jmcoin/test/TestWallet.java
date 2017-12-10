package com.jmcoin.test;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
/**
 *
 * @author Famille
 */
public class TestWallet {
    public static void main(String args[]){
    	try {
			TestMasterNode.runMaster(null, null);
			TestRelay.run();
	    	UserNode userNode = new UserNode("a");
	    	userNode.getWallet().createKeys("a");
	    	UserJMProtocolImpl protocol = new UserJMProtocolImpl(userNode);
	    	userNode.getWallet().computeBalance(protocol);
		} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | InvalidKeyLengthException e) {
			e.printStackTrace();
		}
    	
    }
}

