package com.jmcoin.test;

import com.jmcoin.network.Client;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.ReceiverThread;

import java.io.IOException;

public class TestNetworkClient {
    private static boolean iHaveSomethingToReceive = false;
    private static boolean iHaveSomethingToSend = false;

    public static void main(String args[]){
        try
        {
            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME);
            cli.sendMessage(NetConst.CONNECTION_REQUEST);
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();

            try {
                Thread.sleep(4000);
                cli.sendMessage("test");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
