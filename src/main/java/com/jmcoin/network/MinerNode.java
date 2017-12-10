package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import org.bouncycastle.util.encoders.Hex;
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
	private SuperThread superThread;
	
	public MinerNode(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		super();
		this.wallet = new Wallet(password);
	}
	
	/**
	 * Starts the mining process with the POV of the nodes
	 * @param protocol
	 * @throws NoSuchAlgorithmException 
	 */
	public void startMining(MinerJMProtocolImpl protocol) throws NoSuchAlgorithmException {
		this.superThread = new SuperThread(protocol);
		this.superThread.start();
	}
	
	public void stopMining() {
		this.superThread.mining = false;
	}
	
	public void stopMiningThread() {
		if(this.superThread.miningThread != null)
			this.superThread.miningThread.running = false;
	}
	
	public Block buildBlock(MinerJMProtocolImpl protocol) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		int difficulty = protocol.downloadObject(NetConst.GIVE_ME_DIFFICULTY, null, protocol.getClient());
		Transaction[] transactions = protocol.downloadObject(NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null, protocol.getClient());
		Block lastBlock = protocol.downloadObject(NetConst.GIVE_ME_LAST_BLOCK, null, protocol.getClient());
		Map<String, Output> unspentOutputs = protocol.downloadObject(NetConst.GIVE_ME_UNSPENT_OUTPUTS, null, protocol.getClient());
		Chain chain = protocol.downloadObject(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null, protocol.getClient());
		Block block = new Block();
		if(transactions != null) {
			for(int i = 0; i < transactions.length; i++) {
				//FIXME uncomment if(verifyBlockTransaction(transactions[i], chain, unspentOutputs))
					block.getTransactions().add(transactions[i]);
			}
		}
		int value = protocol.downloadObject(NetConst.GIVE_ME_REWARD_AMOUNT, null, protocol.getClient());
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
		if(!lastBlock.getFinalHash().equals(NetConst.GENESIS))
			block.setPrevHash(lastBlock.getFinalHash());
		return block;
	}
	
	private class SuperThread extends Thread{
		
		private boolean mining;
		private MinerJMProtocolImpl protocol;
		private MiningThread miningThread;
		
		public SuperThread(MinerJMProtocolImpl protocol) throws NoSuchAlgorithmException {
			this.protocol = protocol;
		}

		private void mine(Block block) throws NoSuchAlgorithmException {
			this.miningThread = new MiningThread(this.protocol);
			this.miningThread.block = block;
			this.miningThread.start();
		}
		
		@Override
		public void run() {
			this.mining = true;
			while(this.mining) {
				try {
					Block block = buildBlock(protocol);
					mine(block);
					while(this.miningThread.running)
						continue;//Thread.sleep(500);
					Thread.sleep(5000);
				} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchProviderException
						| SignatureException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.miningThread.running = false;
		}
	}
	
	private class MiningThread extends Thread{
		
		private Block block;
		private MessageDigest digest;
		private boolean running;
		private MinerJMProtocolImpl protocol;
		
		public MiningThread(MinerJMProtocolImpl protocolImpl) throws NoSuchAlgorithmException {
			this.digest = MessageDigest.getInstance("SHA-256");
			this.protocol = protocolImpl;
			this.running = true;
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
	               		System.err.println(gson.toJson(block));
	               		this.protocol.sendMinedBlock(block);
	               		this.running = false;
	               	}
	    		}
	        	if(this.running)verifyAndSetHash(nonce);
			}
	        catch (IOException e) {
				e.printStackTrace();
			}
	        this.running = false;
	        getBundle().getObject();
		}
	}
}
