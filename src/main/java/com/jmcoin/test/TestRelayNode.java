package com.jmcoin.test;

import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

public class TestRelayNode {
	public static void main(String[] args) {
		RelayNodeJMProtocolImpl impl = new RelayNodeJMProtocolImpl();
		for(int i = 0 ;i < 5; i++) {
			Input in = new Input();
			in.setAmount(i);
			in.setPrevTransHash("H"+i);
			in.setSignature("S"+i);
			Output out = new Output();
			out.setAmount(i+42);
			out.setPublicKey("Pk"+i);
			Transaction trans = new Transaction();
			trans.addInputOutput(in, out);
			impl.getPeer().getUnverifiedTransactions().add(trans);
		}
		MultiThreadedServer server = new MultiThreadedServer(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        new Thread(server).start();
        
	}
}
