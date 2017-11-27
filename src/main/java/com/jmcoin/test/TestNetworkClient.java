package com.jmcoin.test;

import com.jmcoin.network.Client;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

import java.io.IOException;

public class TestNetworkClient {
    private static boolean iHaveSomethingToReceive = false;
    private static boolean iHaveSomethingToSend = false;

    public static void main(String args[]){
        try
        {
            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, "localhost");
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
