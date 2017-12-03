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
import com.jmcoin.network.MinerNode;

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
			miner = new MinerNode(args[0]);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
			System.out.println("TestMiningFullProcess: Cannot create Miner/Wallet");
			return;
		}
		TestMasterNode.runMaster();
		try {
			TestRelay.run();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        TestNetworkClient.run();
        try {
			Block  block = miner.buildBlock();
			miner.mine(block);
		} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException | IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}
