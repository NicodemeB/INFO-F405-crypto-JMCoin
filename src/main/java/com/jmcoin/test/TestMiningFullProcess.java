package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.MinerJMProtocolImpl;
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
		try {
			TestMasterNode.runMaster(null, null);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			TestRelay.run();
		} catch (IOException e1) {
			System.err.println("Nobody is listening call to you so clearly, but you don't want to hear me");
			System.err.println("-------------------------------------------------------------------------");
			e1.printStackTrace();
		}
		MinerNode minerHard;
		try {
			minerHard = new MinerNode("a");
			MinerJMProtocolImpl minerJMProtocolImpl1 = new MinerJMProtocolImpl(minerHard);
			minerHard.startMining(minerJMProtocolImpl1);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
			System.out.println("TestMiningFullProcess: Cannot create Miner/Wallet");
			return;
		}
	}

}
