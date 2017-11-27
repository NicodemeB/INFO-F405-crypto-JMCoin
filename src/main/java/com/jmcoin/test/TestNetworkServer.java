package com.jmcoin.test;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

public class TestNetworkServer {

    void run(){
        MultiThreadedServer server = new MultiThreadedServer(NetConst.MASTER_NODE_LISTEN_PORT, new MasterJMProtocolImpl());
        new Thread(server).start();

    }
    public static void main(String args[])
    {
        TestNetworkServer tn = new TestNetworkServer();
        tn.run();
    }
}
