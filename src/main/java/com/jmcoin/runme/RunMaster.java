package com.jmcoin.runme;

import java.io.IOException;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MasterNode;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;

public class RunMaster {
	
	public static void main(String[] args) {
		try {
			new Thread(new MultiThreadedServer(
					NetConst.MASTER_NODE_LISTEN_PORT,
					new MasterJMProtocolImpl(MasterNode.getInstance()))).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
