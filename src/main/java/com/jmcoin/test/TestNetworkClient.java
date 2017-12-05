package com.jmcoin.test;

import com.jmcoin.network.Client;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.ReceiverThread;
import com.jmcoin.network.RelayNode;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

import java.io.IOException;

public class TestNetworkClient {

    public static void run(){
        try
        {
            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, new RelayNodeJMProtocolImpl(new RelayNode()));
            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws ClassNotFoundException{
        run();
    }


}
