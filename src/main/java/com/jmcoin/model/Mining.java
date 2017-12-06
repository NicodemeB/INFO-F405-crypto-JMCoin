package com.jmcoin.model;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import org.bouncycastle.util.encoders.Hex;

import com.jmcoin.network.MinerJMProtocolImpl;
/**
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 * @author Enzo Borel
 */
public class Mining{
	
	/*private Integer difficulty;
	private Integer rewardAmount;
	private Transaction[] unverifiedTransaction;
	private Output[] unspentOutputs;
	private Chain chain;*/
	private MiningThread miningThread;
	
	
	public Mining(MinerJMProtocolImpl protocol) throws NoSuchAlgorithmException {
		this.miningThread = new MiningThread(protocol);
	}
	
	public void stopMining() {
		System.out.println("**************** !! STOP !! ****************");
		this.miningThread.setRunning(false);
	}
	
	public void mine(Block block) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
		System.err.println("Start mining");
        this.miningThread.setBlock(block);
        this.miningThread.setRunning(true);
        this.miningThread.start();
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
