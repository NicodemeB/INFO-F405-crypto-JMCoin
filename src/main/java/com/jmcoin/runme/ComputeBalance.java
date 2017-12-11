package com.jmcoin.runme;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class ComputeBalance {
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("1 argument is required:");
			System.out.println("(1) password of the wallet (String)");
			System.out.println("(2) (optional) hostname (String)");
			return;
		}
		UserNode node = null;
		try {
			node = new UserNode(args[0]);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			System.out.println("Cannot create node");
			return;
		}
		if(node!= null && !node.getWallet().getKeys().keySet().isEmpty()) {
			try {
				String hostname = args.length < 2 ? "localhost" : args[1];
				UserJMProtocolImpl protocol = new UserJMProtocolImpl(node, hostname);
				node.getWallet().computeBalance(protocol);
				System.out.println("Addresses: "+ node.getWallet().getAddresses());
				System.out.println("Balance: " + node.getWallet().getBalance());
				System.out.println("--------------------- DONE -----------------------");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Cannot get balance");
			}
		}
		else {
			System.out.println("Keys not found");
		}
	}
}
