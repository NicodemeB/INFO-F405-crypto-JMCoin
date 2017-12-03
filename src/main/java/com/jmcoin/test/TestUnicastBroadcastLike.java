package com.jmcoin.test;



import java.io.IOException;


public class TestUnicastBroadcastLike {
    public static void main(String[] args) {
        try {
			TestMasterNode.runMaster();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        try {
            TestRelay.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        TestNetworkClient.run();
//        TestNetworkClientStopMining.run();
    }
}
