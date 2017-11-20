package com.jmcoin.test;


import com.jmcoin.network.BroadcastingClient;
import com.jmcoin.network.BroadcastingEchoServer;

import java.io.IOException;

public class TestBroadcastClient {
    public static void main (String[] agrs){
        try {
            BroadcastingClient client = new BroadcastingClient(1);
            client.discoverServers("test\0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
