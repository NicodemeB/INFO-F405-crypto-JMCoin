package com.jmcoin.test;


import com.jmcoin.network.BroadcastingClient;
import com.jmcoin.network.NetConst;

public class TestBroadcastClient {
    public static void main (String[] agrs){
        try {
            BroadcastingClient client = new BroadcastingClient(1, NetConst.RELAY_BROADCAST_PORT);
            client.discoverServers("test\0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
