package com.jmcoin.runme;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Transaction;
import com.jmcoin.network.NetConst;
import com.jmcoin.network.UserJMProtocolImpl;
import com.jmcoin.network.UserNode;

public class CreateTransaction {
	
	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("3 arguments are required:");
			System.out.println("(1) password of the wallet (String)");
			System.out.println("(2) destination address (can be random) (String)");
			System.out.println("(3) amount to send (Double)");
			System.out.println("(4) hostname (String)");
			return;
		}
		String amount = args[2];
		double doubleAmount = 0.0;
		try {
			doubleAmount = Double.parseDouble(amount);
		}
		catch(NumberFormatException nfe) {
			System.out.println("The amount was not a valid integer");
			return;
		}
		UserNode node = null;
		try {
			node = new UserNode(args[0]);			
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException e1) {
			System.out.println("Cannot create node");
			return;
		}
		if(node!= null && !node.getWallet().getKeys().keySet().isEmpty()) {
			try {
				UserJMProtocolImpl protocol = new UserJMProtocolImpl(node, args[3]);
				PrivateKey privKey = node.getWallet().getKeys().keySet().iterator().next();
				node.getWallet().computeBalance(protocol);
				System.out.println("Addresses: "+ node.getWallet().getAddresses());
				System.out.println("Balance: " + node.getWallet().getBalance());
				Transaction transaction = node.createTransaction(protocol,
						SignaturesVerification.DeriveJMAddressFromPubKey(node.getWallet().getKeys().get(privKey).getEncoded()),
						args[1],
						doubleAmount,
						privKey,
						node.getWallet().getKeys().get(privKey));
				if(transaction == null) {
					System.out.println("Invalid transaction");
					return;
				}
				protocol.getClient().sendMessage(protocol.craftMessage(NetConst.TAKE_MY_NEW_TRANSACTION, node.getGson().toJson(transaction)));
				node.getWallet().computeBalance(protocol);
				System.out.println("Transaction: done");
				System.out.println("Balance: " + node.getWallet().getBalance());
				System.out.println("--------------------------------------------");
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
					| IOException e) {
				e.printStackTrace();
				System.out.println("Cannot create transaction");
			}
		}
	}
}
