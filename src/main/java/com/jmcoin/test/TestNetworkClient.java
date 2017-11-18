package com.jmcoin.test;

import com.jmcoin.network.Client;

import java.io.IOException;

public class TestNetworkClient {
    public static void main(String args[]){
        try
        {
            Client cli = new Client(9000, "localhost");
            cli.sendMessage("test");
            System.out.println(cli.readMessage());
            cli.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
