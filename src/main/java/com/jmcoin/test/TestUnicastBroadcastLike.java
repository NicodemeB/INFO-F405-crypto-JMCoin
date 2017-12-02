package com.jmcoin.test;



import java.io.IOException;


public class TestUnicastBroadcastLike {
    public static void main(String[] args) {
        TestMasterNode.runMaster();

        try {
            TestRelay.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        TestNetworkClient.run();
//        TestNetworkClientStopMining.run();
    }
}
