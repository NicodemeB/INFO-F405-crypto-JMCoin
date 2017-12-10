package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class TestBlockChainCaching {
	public static void main(String[] args) {
		try {
			KeyGenerator genKey = new KeyGenerator(1024);
			genKey.createKeys();
			TestMasterNode.runMaster(genKey.getPrivateKey(), genKey.getPublicKey());
			TestRelay.run();
			UserNode user = new UserNode("a");
			UserJMProtocolImpl userProtocol = new UserJMProtocolImpl(user);
			user.getWallet().computeBalance(userProtocol);
			System.out.println(user.getWallet().getBalance());
		}
		catch(NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | IOException e) {
			e.printStackTrace();
		}
	}
}
