package com.jmcoin.test;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class TestMasterNode {
	public static void main(String[] args) {
		MultiThreadedServer server = new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl());
		new Thread(server).start();
	}
}
