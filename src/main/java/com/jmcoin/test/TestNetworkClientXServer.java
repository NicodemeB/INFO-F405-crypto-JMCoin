package com.jmcoin.test;

import com.jmcoin.network.Client;
import com.jmcoin.network.MultiThreadedServer;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

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
            cli.sendMessage((Object)"client");
            boolean loop = true;
            do {
                if (cli.iHaveSomethingToReceive()){
                    System.out.println("Received " + cli.getMessage());
                    cli.iHaveSomethingToSend();
                } else if (cli.doIHaveSomethingToSend()){
                    cli.sendMessage("something");
                }
                Thread.sleep(10);

            } while (loop);
            cli.close();
            System.out.println("close");
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
