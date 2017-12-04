package com.jmcoin.model;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import org.bouncycastle.util.encoders.Hex;

import com.google.gson.Gson;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.MinerJMProtocolImpl;
import com.jmcoin.network.NetConst;
/**
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 * @author Enzo Borel
 */
public class Mining{
	
	private Integer difficulty;
	private Integer rewardAmount;
	private Transaction[] unverifiedTransaction;
	private MiningThread miningThread;
	private Output[] unspentOutputs;
	private Chain chain;
	
	public Mining(MinerJMProtocolImpl protocol) throws NoSuchAlgorithmException {
		this.miningThread = new MiningThread(protocol);
	}
	
	public Transaction[] getUnverifiedTransaction() {
		return unverifiedTransaction;
	}

	public void setUnverifiedTransaction(Transaction[] unverifiedTransaction) {
		this.unverifiedTransaction = unverifiedTransaction;
	}

	public Integer getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public Integer getRewardAmount() {
		return rewardAmount;
	}

	public void setRewardAmount(int rewardAmount) {
		this.rewardAmount = rewardAmount;
	}
	
	public void stopMining() {
		System.out.println("**************** !! STOP !! ****************");
		this.miningThread.setRunning(false);
	}
	
	public void mine(Block block) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
        this.miningThread.setBlock(block);
        this.miningThread.setRunning(true);
        this.miningThread.start();
	}
	
	public Output[] getUnspentOutputs() {
		return unspentOutputs;
	}

	public void setUnspentOutputs(Output[] unspentOutputs) {
		this.unspentOutputs = unspentOutputs;
	}

	public Chain getChain() {
		return chain;
	}

	public void setChain(Chain chain) {
		this.chain = chain;
	}
	
	private class MiningThread extends Thread{
	
		private Block block;
		private MessageDigest digest;
		private boolean running;
		private MinerJMProtocolImpl protocol;
		
		public MiningThread(MinerJMProtocolImpl protocolImpl) throws NoSuchAlgorithmException {
			this.digest = MessageDigest.getInstance("SHA-256");
			this.protocol = protocolImpl;
		}
		
		public void setRunning(boolean running) {
			this.running = running;
		}
		
		public void setBlock(Block block) {
			this.block = block;
		}

		private byte[] calculateHash(int nonce) {
		   block.setNonce(nonce);
		   this.digest.update(block.getBytes());
		   return this.digest.digest();
		}

		private boolean verifyAndSetHash(int nonce) {
			byte[] hash;
			if(this.block.verifyHash((hash = calculateHash(nonce)))){
		        this.block.setFinalHash(Hex.toHexString(hash));
				return true;
		    }
			return false;
		}
		
		@Override
		public void run() {
        	if(this.block.getSize() > Block.MAX_BLOCK_SIZE) {
        		this.running = false;
        	}
        	int nonce = Integer.MIN_VALUE;
            try {
            	while(this.running && nonce < Integer.MAX_VALUE){
                   	if (verifyAndSetHash(nonce++)) {
                   		this.protocol.sendMinedBlock(block);
                   		this.running = false;
                   	}
        			Thread.sleep(100);
        		}
            	if(this.running)verifyAndSetHash(nonce);
            	this.running = false;
			}
            catch (InterruptedException|IOException e) {
				e.printStackTrace();
			}
		}
	}

}
