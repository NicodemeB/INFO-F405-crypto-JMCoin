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

public class TestMineur {
	public static void main(String[] args) {
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
