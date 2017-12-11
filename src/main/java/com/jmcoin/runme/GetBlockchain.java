package com.jmcoin.runme;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Chain;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class GetBlockchain {
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("1 argument is required:");
			System.out.println("(1) password of the wallet (String)");
			System.out.println("(2) (optional) host name (String)");
			return;
		}
		UserNode node;
		try {
			node = new UserNode(args[0]);	
			String hostname = args.length < 2 ? "localhost" : args[1];
			UserJMProtocolImpl protocol = new UserJMProtocolImpl(node, hostname);
			Chain chain = protocol.downloadObject(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null, protocol.getClient());
			System.out.println("Blockchain " + node.getGson().toJson(chain));
		} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e) {
			e.printStackTrace();
			System.out.println("Cannot get blockchain");
		}
	}

}
