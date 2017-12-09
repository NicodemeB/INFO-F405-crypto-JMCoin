package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class TestBlockChainCaching {
	public static void main(String[] args) {
		try {
			/*TestMasterNode.runMaster();
			TestRelay.run();*/
			UserNode user = new UserNode("a");
			UserJMProtocolImpl userProtocol = new UserJMProtocolImpl(user);
			user.debugUserNode(userProtocol);
			
		}
		catch(NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | IOException e) {
			e.printStackTrace();
		}
	}
}
