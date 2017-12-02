package com.jmcoin.test;

import com.jmcoin.crypto.AES;
import com.jmcoin.model.Block;
import com.jmcoin.network.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

public class TestNetworkClient2 {
    /*private static boolean iHaveSomethingToReceive = false;
    private static boolean iHaveSomethingToSend = false;*/

    public static void run(){
        try
        {

            Client cli = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, new RelayNodeJMProtocolImpl());
//            cli.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null));

            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();


            cli.sendMessage(JMProtocolImpl.craftMessage(NetConst.BROADCAST_DEBUG, null));


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws ClassNotFoundException{
        run();
    }


}

