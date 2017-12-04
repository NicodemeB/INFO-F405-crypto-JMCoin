package com.jmcoin.test;

import java.io.IOException;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class TestMasterNode {
	
	public static void runMaster() throws IOException {
		MultiThreadedServer<MasterJMProtocolImpl> server = new MultiThreadedServer<MasterJMProtocolImpl>(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl());
		new Thread(server).start();
	}
	
	public static void main(String[] args) {
		try {
			runMaster();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
