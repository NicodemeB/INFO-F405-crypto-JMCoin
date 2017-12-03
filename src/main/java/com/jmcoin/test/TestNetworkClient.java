package com.jmcoin.test;

import com.jmcoin.network.Client;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.ReceiverThread;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

import java.io.IOException;

public class TestNetworkClient {
    /*private static boolean iHaveSomethingToReceive = false;
    private static boolean iHaveSomethingToSend = false;*/

    public static void run(){
        try
        {
            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, new RelayNodeJMProtocolImpl());
//            cli.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null));
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();

//            try {
//                Thread.sleep(4000);
//                cli.sendMessage(NetConst.TAKE_MY_MINED_BLOCK);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws ClassNotFoundException{
        run();
    }


}
