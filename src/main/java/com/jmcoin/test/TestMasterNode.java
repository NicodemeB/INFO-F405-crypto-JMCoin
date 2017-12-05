package com.jmcoin.test;

import java.io.IOException;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MasterNode;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class TestMasterNode {
	
	public static void runMaster() throws IOException {
		new Thread(new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl(MasterNode.getInstance()))).start();
	}
	
	public static void main(String[] args) {
		try {
			runMaster();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
