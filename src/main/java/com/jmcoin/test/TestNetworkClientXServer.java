package com.jmcoin.test;

import com.jmcoin.network.*;

import java.io.IOException;

public class TestNetworkClientXServer {
    
	public static void run() throws IOException{
        MultiThreadedServer server = new MultiThreadedServer(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        new Thread(server).start();
    }
    
    public static void main(String args[]){
        try{
        	run();
            Client cli = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, new MasterJMProtocolImpl());
            cli.sendMessage(NetConst.CONNECTION_REQUEST);
            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
