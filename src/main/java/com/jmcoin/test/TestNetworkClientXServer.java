package com.jmcoin.test;

import com.jmcoin.network.*;

import java.io.IOException;

public class TestNetworkClientXServer {
    
	public static void run(){
        MultiThreadedServer server = new MultiThreadedServer(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        new Thread(server).start();
    }
    
    public static void main(String args[])
    {
        run();
        try
        {
            Client cli = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, new MasterJMProtocolImpl());
            cli.sendMessage(NetConst.CONNECTION_REQUEST);
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
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
