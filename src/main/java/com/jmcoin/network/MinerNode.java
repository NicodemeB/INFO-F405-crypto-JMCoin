package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class MinerNode extends Peer{

	private Wallet wallet;
	
	public MinerNode(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		super();
		this.wallet = new Wallet(password);
	}
	
	public Block buildBlock(MinerJMProtocolImpl protocol) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		int difficulty = protocol.downloadObject(Integer.class, NetConst.GIVE_ME_DIFFICULTY, null, protocol.getClient());
		Transaction[] transactions = protocol.downloadObject(Transaction[].class, NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null, protocol.getClient());
		Block lastBlock = protocol.downloadObject(Block.class, NetConst.GIVE_ME_LAST_BLOCK, null, protocol.getClient());
		Map<String, Output> unspentOutputs = protocol.downloadObject(new TypeToken<Map<String, Output>>(){}.getType(), NetConst.GIVE_ME_UNSPENT_OUTPUTS, null, protocol.getClient());
		Chain chain = protocol.downloadObject(Chain.class, NetConst.GIVE_ME_BLOCKCHAIN_COPY, null, protocol.getClient());
		Block block = new Block();
		if(transactions != null) {
			for(int i = 0; i < transactions.length; i++) {
				if(verifyBlockTransaction(transactions[i], chain, unspentOutputs))
					block.getTransactions().add(transactions[i]);
			}
		}
		int value = protocol.downloadObject(Integer.class, NetConst.GIVE_ME_REWARD_AMOUNT, null, protocol.getClient());
		double doubleRewardAmount = value * (1.0/NetConst.MAX_SENT_TRANSACTIONS);
		//TODO choose the key
    	PrivateKey privKey = this.wallet.getKeys().keySet().iterator().next();
        PublicKey pubKey = this.wallet.getKeys().get(privKey);
		Transaction reward = new Transaction();
		Output out = new Output();
		out.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(pubKey.getEncoded()));
		out.setAmount(doubleRewardAmount);
		reward.setOutputOut(out);
		reward.setOutputBack(new Output());
		reward.setPubKey(pubKey.getEncoded());
		reward.setSignature(SignaturesVerification.signTransaction(reward.getBytes(false), privKey));

		block.setDifficulty(difficulty);
		block.setTimeCreation(System.currentTimeMillis());
		block.setPrevHash(lastBlock.getFinalHash());
		return block;
	}
}
