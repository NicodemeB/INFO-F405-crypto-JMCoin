package com.jmcoin.test;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class TestMasterNode {
	
	public static void runMaster() {
		MultiThreadedServer server = new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl());
		new Thread(server).start();
	}
	
	public static void main(String[] args) {
		runMaster();
	}
}
