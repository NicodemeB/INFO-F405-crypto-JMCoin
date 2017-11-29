package com.jmcoin.model;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bouncycastle.util.encoders.Hex;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

/**
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 * @author Enzo Borel
 */
public class Mining{
	
	private Block block;
	
	public Mining() { //TODO add a way to identify miner
		this.block = new Block();
	}
	
	public Mining(Block block) {
		this.block = block;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public void buildBlock(PublicKey pubKey, PrivateKey privKey) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
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
		if(difficulty == -1 || unvf == null) return;
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
		Reward reward = new Reward();
		Output out = new Output();
		out.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(pubKey));
		out.setAmount(intRewardAmount);
		reward.setOutputOut(out);
		reward.setOutputBack(new Output());
		reward.setPubKey(pubKey);
		reward.setSignature(SignaturesVerification.signTransaction(reward, privKey));
	}
	
	public String mine() throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<String> callable = new MiningThread(block);
            Future<String> value = executor.submit(callable);
            executor.shutdown();
            return value.get();
	}
	
	private class MiningThread implements Callable<String> {
	
		private Block block;
		private MessageDigest digest;
		
		
		public MiningThread(Block block) throws NoSuchAlgorithmException {
			this.block = block;
			this.digest = MessageDigest.getInstance("SHA-256");
		}
		
		private byte[] calculateHash(int nonce) {
		   block.setNonce(nonce);
		   this.digest.update(block.toString().getBytes());
		   return this.digest.digest();
                }

		private boolean verifyAndSetHash(int nonce) {
			byte[] hash;
			if(this.block.verifyHash((hash = calculateHash(nonce)))){
		        this.block.setFinalHash(Hex.toHexString(hash));
		        this.block.setNonce(nonce);
				return true;
		    }
			return false;
		}

		@Override
		public String call() throws Exception {
            if(block.getSize() > Block.MAX_BLOCK_SIZE) return null;
            int nonce = Integer.MIN_VALUE;
            while(nonce < Integer.MAX_VALUE){
                    if (verifyAndSetHash(nonce++)) return block.getFinalHash();
            }
            if (verifyAndSetHash(nonce)) return block.getFinalHash();
            return null;
		}
	}
}
