package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class TestUserNode {
	public static void main(String[] args) {
		try {
			TestMasterNode.runMaster();
			TestRelay.run();
			UserNode node = new UserNode("a");
			UserJMProtocolImpl protocol = new UserJMProtocolImpl(node);
			protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNSPENT_OUTPUTS, null));
			while(node.getUnspentOutputs() == null) {
				Thread.sleep(500);
			}
			System.out.println("------------------" +node.getUnspentOutputs() + "-----------------------");
			for(String s : node.getUnspentOutputs().keySet()) {
				System.out.println(node.getUnspentOutputs().get(s));
			}
			protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_LAST_BLOCK, null));
			while(node.getLastBlock() == null) {
				Thread.sleep(500);
			}
			System.out.println("------------------" +node.getLastBlock() + "-----------------------");
			
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
