package com.jmcoin.test;

import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

public class TestNetworkServer {

    void run(){
        MultiThreadedServer server = new MultiThreadedServer(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        new Thread(server).start();

//        try {
//            Thread.sleep(20 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Stopping Server");
//        server.stop();
    }
    public static void main(String args[])
    {
        TestNetworkServer tn = new TestNetworkServer();
//        while(true){
            tn.run();
//        }
    }
}
