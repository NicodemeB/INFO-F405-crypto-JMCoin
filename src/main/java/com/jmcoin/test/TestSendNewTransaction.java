package com.jmcoin.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Transaction;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MasterNode;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class TestSendNewTransaction {
	
	public static void main(String[] args) {
		try {
			new Thread(new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl(MasterNode.getInstance()))).start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			TestRelay.run();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		UserNode node = null;
		try {
			node = new UserNode("a");
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
		}
		UserJMProtocolImpl protocol = null;
		try {
			protocol = new UserJMProtocolImpl(node);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Map<PrivateKey, PublicKey> keys= node.getWallet().getKeys();
		PrivateKey privKey = keys.keySet().iterator().next();
		PublicKey pubKey = keys.get(privKey);
		try {
			Transaction transaction = node.createTransaction(protocol, "connard", "connasse", 0, privKey, pubKey);
			if(transaction != null)
				protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_NEW_TRANSACTION, IOFileHandler.toJson(transaction)));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
				| IOException e) {
			e.printStackTrace();
		}
	}

}
