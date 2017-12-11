package com.jmcoin.runme;

import java.io.IOException;

import com.jmcoin.network.ClientSC;
import com.jmcoin.network.MultiThreadedServerClient;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.RelayNode;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

public class RunRelay {
	
	public static void main(String[] args) {
		String hostname = args.length < 1 ? "localhost" : args[0];
		System.out.println("Running on: "+hostname);
		try {
			RelayNodeJMProtocolImpl rmp = new RelayNodeJMProtocolImpl(new RelayNode());
			MultiThreadedServerClient server = new MultiThreadedServerClient(NetConst.RELAY_NODE_LISTEN_PORT, rmp);
	        ClientSC cli = new ClientSC(NetConst.MASTER_NODE_LISTEN_PORT, hostname, rmp, server);
	        server.setClient(cli);
	        new Thread(server).start();
	        Thread.sleep(1000);
	        cli.sendMessage(NetConst.CONNECTION_REQUEST);
	        new Thread(cli).start();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
