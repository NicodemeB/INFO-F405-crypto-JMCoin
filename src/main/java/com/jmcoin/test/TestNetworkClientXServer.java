package com.jmcoin.test;

import com.jmcoin.network.*;

import java.io.IOException;

public class TestNetworkClientXServer {
    void run(){

        MultiThreadedServer server = new MultiThreadedServer(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        new Thread(server).start();
    }
    public static void main(String args[])
    {
        TestNetworkClientXServer tn = new TestNetworkClientXServer();
        tn.run();

        try
        {
            Client cli = new Client(NetConst.MASTER_NODE_LISTEN_PORT, "localhost");
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
