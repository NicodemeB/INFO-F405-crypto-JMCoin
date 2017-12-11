package com.jmcoin.test;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Transaction;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class TestSendNewTransaction {
	
	public static void main(String[] args) {
		UserNode node = null;
		try {
			node = new UserNode("connard");
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
		}
		Map<PrivateKey, PublicKey> keys= node.getWallet().getKeys();
		PrivateKey privKey = keys.keySet().iterator().next();
		PublicKey pubKey = keys.get(privKey);

		try {
			TestMasterNode.runMaster(privKey, pubKey);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			TestRelay.run();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		UserJMProtocolImpl protocol = null;
		try {
			protocol = new UserJMProtocolImpl(node);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Transaction transaction = node.createTransaction(protocol, SignaturesVerification.DeriveJMAddressFromPubKey(pubKey.getEncoded()), "connard", 15, privKey, pubKey);
			protocol.getClient().sendMessage(protocol.craftMessage(NetConst.TAKE_MY_NEW_TRANSACTION, node.getGson().toJson(transaction)));
			System.out.println("****************************************************************************");
			transaction = node.createTransaction(protocol, SignaturesVerification.DeriveJMAddressFromPubKey(pubKey.getEncoded()), "connard", 15, privKey, pubKey);
			protocol.getClient().sendMessage(protocol.craftMessage(NetConst.TAKE_MY_NEW_TRANSACTION, node.getGson().toJson(transaction)));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
				| IOException e) {
			e.printStackTrace();
		}
	}
}
