package com.jmcoin.test;


import com.jmcoin.network.BroadcastingEchoServer;
import com.jmcoin.network.RelayNodeJMProtocolImpl;

import java.io.IOException;

public class TestBroadcastServer {
    public static void main (String[] agrs){
        try {
            BroadcastingEchoServer server = new BroadcastingEchoServer(new RelayNodeJMProtocolImpl());
            server.run();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
