package com.jmcoin.test;

import com.jmcoin.network.Client;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

import java.io.IOException;

public class TestNetworkClient {
    public static void main(String args[]){
        try
        {
            Client cli = new Client(NetConst.MASTER_NODE_LISTEN_PORT, "crypto-win.nicode.me");
            //Client cli = new Client(NetConst.MASTER_NODE_LISTEN_PORT, "localhost");

            cli.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY));
            System.out.println("Received answer from Master " +  cli.readMessage());
            cli.close();
            System.out.println("close");
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
