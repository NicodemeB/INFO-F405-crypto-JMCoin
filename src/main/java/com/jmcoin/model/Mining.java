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
	
	private MiningThread miningThread;
	
	public Mining(MinerJMProtocolImpl protocol) throws NoSuchAlgorithmException {
		this.miningThread = new MiningThread(protocol);
	}
	
	public void stopMining() {
		System.out.println("**************** !! STOP !! ****************");
		this.miningThread.running = false;
	}
	
	public void restart() {
		this.miningThread.running = false;
	}
	
	public void mine(Block block, MinerJMProtocolImpl protocol) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
		this.miningThread.setRunning(false);
		while(this.miningThread.isRunning()) {
			Thread.sleep(500);
		}
		this.miningThread = new MiningThread(protocol);
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
		
		public void setBlock(Block block) {
			this.block = block;
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public void setRunning(boolean running) {
			this.running = running;
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
        	if(this.block == null || this.block.getSize() > Block.MAX_BLOCK_SIZE) {
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
			}
            catch (InterruptedException|IOException e) {
				e.printStackTrace();
			}
            this.running = false;
		}
	}
}
