package com.jmcoin.test;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

public class TestRelayNode {
	public static void main(String[] args) {
		MultiThreadedServer server = new MultiThreadedServer(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        new Thread(server).start();
	}
}
