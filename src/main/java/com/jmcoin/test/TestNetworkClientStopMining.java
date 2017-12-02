package com.jmcoin.test;

import com.jmcoin.network.*;

import java.io.IOException;

public class TestNetworkClientStopMining {

    public static void run(){
        try
        {
            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, new RelayNodeJMProtocolImpl());
            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();
            cli.sendMessage(JMProtocolImpl.craftMessage(NetConst.SEND_BROADCAST_DEBUG, null));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws ClassNotFoundException{
        run();
    }


}

