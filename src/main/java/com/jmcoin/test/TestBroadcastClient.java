package com.jmcoin.test;


import com.jmcoin.network.BroadcastingClient;

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
