package com.jmcoin.runme;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.MinerJMProtocolImpl;
import com.jmcoin.network.MinerNode;

public class CreateMiner {
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("1 argument is required:");
			System.out.println("(1) password of the wallet (String)");
			System.out.println("(2) (optional) hostname of the relay");
			return;
		}
		MinerNode minerNode;
		String hostname = args.length < 2 ? "localhost" : args[1];
		try {
			minerNode = new MinerNode(args[0]);
			minerNode.startMining(new MinerJMProtocolImpl(minerNode, hostname));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e) {
			e.printStackTrace();
			System.out.println("Unable to mine");
		}
	}
}
