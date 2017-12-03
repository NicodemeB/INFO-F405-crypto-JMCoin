package com.jmcoin.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bouncycastle.util.encoders.Hex;


/**
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 * @author Enzo Borel
 */
public class Mining{
	
	private Integer difficulty;
	private Integer rewardAmount;
	private Transaction[] unverifiedTransaction;
	
	public Mining() {
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
	
	public String mine(Block block) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
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
