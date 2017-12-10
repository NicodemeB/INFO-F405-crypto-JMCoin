package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MasterNode;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class TestMasterNode {
	
	public static void runMaster(PrivateKey privKey, PublicKey pubKey) throws IOException {
		MasterNode node = MasterNode.getInstance();
		try{
			node.debugMasterNode(privKey, pubKey);
		}
		catch(NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		new Thread(new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl(node))).start();
	}
	
	public static void main(String[] args) {
		try {
			KeyGenerator key = new KeyGenerator(1024);
			key.createKeys();
			runMaster(key.getPrivateKey(), key.getPublicKey());
		} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
}
