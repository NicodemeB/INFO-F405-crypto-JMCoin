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

public class TestMiner {

	public static void main(String[] args) {
		MinerNode miner;
		try {
			miner = new MinerNode("a");
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
			System.out.println("TestMiningFullProcess: Cannot create Miner/Wallet");
			return;
		}
        try {
			Block block = miner.buildBlock();
			miner.getMining().mine(block);
		} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException | IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}
