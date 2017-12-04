package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Block;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Reward;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class MinerNode extends Peer{
	
	private Wallet wallet;
	private MinerJMProtocolImpl protocol;
	private Mining mining;
	
	public MinerNode(String email) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		super();
		this.protocol = new MinerJMProtocolImpl(this);
		this.mining = new Mining(this.protocol);
		this.wallet = new Wallet(email);
		this.portBroadcast = NetConst.MINER_BROADCAST_PORT;
	}
	
	public Mining getMining() {
		return this.mining;
	}

	public Block buildBlock() throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		Block block = new Block();
		this.protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_DIFFICULTY, null));
		while(this.mining.getDifficulty()== null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_REWARD_AMOUNT, null));
		while(this.mining.getRewardAmount() == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null));
		while(this.mining.getUnverifiedTransaction() == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int difficulty = this.mining.getDifficulty();
		Transaction trans[] = this.mining.getUnverifiedTransaction();
		if(trans != null) {
			for(int i = 0; i < trans.length; i++) {
				//TODO verify transaction
				block.getTransactions().add(trans[i]);
			}
		}
		int intRewardAmount = 0;
		int tmp = this.mining.getRewardAmount();
    	intRewardAmount = block.getSize() >= Block.MAX_BLOCK_SIZE ? tmp : intRewardAmount * ((tmp / Block.MAX_BLOCK_SIZE)+1);
        PrivateKey privKey = this.wallet.getKeys().keySet().iterator().next();
        PublicKey pubKey = this.wallet.getKeys().get(privKey);
		Reward reward = new Reward();
		Output out = new Output();
		out.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(pubKey));
		out.setAmount(intRewardAmount);
		reward.setOutputOut(out);
		reward.setOutputBack(new Output());
		reward.setPubKey(pubKey);
		reward.setSignature(SignaturesVerification.signTransaction(reward.getBytes(false), privKey));

		block.setDifficulty(difficulty);
		block.setTimeCreation(System.currentTimeMillis());
		block.setPrevHash(null); //FIXME find prev block in the chain or let the master do the job*/
		return block;
	}
}
