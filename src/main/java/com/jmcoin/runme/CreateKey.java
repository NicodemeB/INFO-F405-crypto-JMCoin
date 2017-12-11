package com.jmcoin.runme;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.UserNode;

public class CreateKey {
	
	/**
	 * Please type the password
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UserNode node = new UserNode(args[0]);
			node.getWallet().createKeys(args[0]);
			System.out.println("Keys creation: Done");
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | InvalidKeyLengthException e) {
			e.printStackTrace();
		}
	}
}
