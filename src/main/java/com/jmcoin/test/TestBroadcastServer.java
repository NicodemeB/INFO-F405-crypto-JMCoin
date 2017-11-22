package com.jmcoin.test;


import com.jmcoin.network.BroadcastingClient;
import com.jmcoin.network.BroadcastingEchoServer;

import java.io.IOException;

public class TestBroadcastServer {
    public static void main (String[] agrs){
        try {
            BroadcastingEchoServer server = new BroadcastingEchoServer();
            server.run();

        } catch (IOException e){
            e.printStackTrace();
        }
    }


    //TEST

}
