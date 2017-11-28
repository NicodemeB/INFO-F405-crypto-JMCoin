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
            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, "localhost");
            cli.sendMessage((Object)"ConnectionRequest");
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
            try {
                Thread t = new Thread( new ReceiverThread(cli, cli.in));
                t.start();
                do {
                    if (cli.getToSend() != null){
                        System.out.println("to send : " + cli.getToSend().toString());
                        cli.sendMessage(cli.getToSend());
                    }
                    Thread.sleep(100);
                } while (true);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    cli.close();
                    System.out.println("close");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
