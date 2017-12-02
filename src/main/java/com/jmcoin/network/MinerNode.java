package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Reward;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class MinerNode extends Peer{
	
	private Wallet wallet;
	
	public MinerNode(String email, String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		super();
		new MinerJMProtocolImpl(this);
		this.wallet = new Wallet(email, password);
		this.portBroadcast = NetConst.MINER_BROADCAST_PORT;
	}
	
	public void mine(Block block) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, InterruptedException, ExecutionException {
		Mining mining = new Mining();
		mining.mine(block);
		JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.TAKE_MY_MINED_BLOCK, new Gson().toJson(block));
	}

	public Block buildBlock() throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		Block block = new Block();
		String unvf = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null);
		String diff = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_DIFFICULTY, null);
		String rewardAmount = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_REWARD_AMOUNT, null);
		int difficulty = -1;
		try {
			difficulty = Integer.parseInt(diff);
		}
		catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		if(difficulty == -1 || unvf == null) return null;
		Transaction trans[] = IOFileHandler.getFromJsonString(unvf, Transaction[].class);//cherche les transactions non vérifiées
		if(trans != null) {
			for(int i = 0; i < trans.length; i++) {
				//TODO verify transaction
				block.getTransactions().add(trans[i]);
			}
		}
		int intRewardAmount = 0;
        try {
        	int tmp = Integer.parseInt(rewardAmount);
        	intRewardAmount = block.getSize() >= Block.MAX_BLOCK_SIZE ? tmp : intRewardAmount * ((tmp / Block.MAX_BLOCK_SIZE)+1);
        }
        catch (NumberFormatException  nfe) {
			nfe.printStackTrace();
			intRewardAmount = trans.length;
		}
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
		block.setPrevHash(null); //FIXME find prev block in the chain or let the master do the job
		return block;
	}

	public void stopMining() {
		System.out.println("-------------------Stop mining-------------------");
	}
}
