package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.jmcoin.model.Chain;
import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MasterNode;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class TestMasterNode {
	
	public static void runMaster() throws IOException {
		MasterNode node = MasterNode.getInstance();
		try{
			node.debugMasterNode();
		}
		catch(NoSuchAlgorithmException | NoSuchProviderException p) {
			
		}
		new Thread(new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl(node))).start();
	}
	
	public static void main(String[] args) {
		try {
			runMaster();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Chain getChain() {
		Chain chain = new Chain();
		return chain;
	}
}
