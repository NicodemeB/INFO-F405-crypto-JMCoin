package com.jmcoin.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Block;
import com.jmcoin.network.Client;
import com.jmcoin.network.MasterJMProtocolImpl;
import com.jmcoin.network.MinerNode;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.ReceiverThread;

public class TestMiningFullProcess {
	
	/**
	 * To run this routine, keys have to exist. If it's not the case, please open TestWallet, uncomment "before w.createKeys("a");"
	 * and run TestWallet to create keys.
	 * To pass appropriate arguments:
	 * Run As > Run configurations > Arguments > Program Arguments and write 'a a' (without quotes) > Run 
	 * @param args
	 */
	public static void main(String[] args){
		MinerNode miner;
		try {
			miner = new MinerNode(args[0], args[1]);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
			System.out.println("TestMiningFullProcess: Cannot create Miner/Wallet");
			return;
		}
		TestMasterNode.runMaster();
		TestNetworkClientXServer.run();
		
        try{
            Client cli = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, new MasterJMProtocolImpl());
            cli.sendMessage(NetConst.CONNECTION_REQUEST);
            Thread t = new Thread(new ReceiverThread<Client>(cli));
            t.start();
            Thread thread = new Thread(cli);
            thread.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
			Block  block = miner.buildBlock();
			miner.mine(block);
		} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException | IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}

}
