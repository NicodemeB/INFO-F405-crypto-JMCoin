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

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Block;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class MinerNode extends Peer{

	private Wallet wallet;
	
	public MinerNode(String email) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		super();
		this.wallet = new Wallet(email);
	}
	
	public void mine(MinerJMProtocolImpl protocol) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, InterruptedException, ExecutionException {
		Mining mining = protocol.getMiningInfos();
		Block block = buildBlock(mining);
		mining.mine(block);
	}
	
	/**
	 * Debug prupos only
	 * @param protocol
	 * @param diff
	 * @throws InvalidKeyException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void mine(MinerJMProtocolImpl protocol, int diff) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, InterruptedException, ExecutionException {
		Mining mining = protocol.getMiningInfos();
		Block block = buildBlock(mining);
		block.setDifficulty(diff);
		mining.mine(block);
	}
	
	private Block buildBlock(Mining mining) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		Block block = new Block();
		int difficulty = mining.getDifficulty();
		Transaction trans[] = mining.getUnverifiedTransaction();
		if(trans != null) {
			for(int i = 0; i < trans.length; i++) {
				//TODO verify transaction
				block.getTransactions().add(trans[i]);
			}
		}
		int intRewardAmount = 0;
		int tmp = mining.getRewardAmount();
    	intRewardAmount = block.getSize() >= Block.MAX_BLOCK_SIZE ? tmp : intRewardAmount * ((tmp / Block.MAX_BLOCK_SIZE)+1);
        PrivateKey privKey = this.wallet.getKeys().keySet().iterator().next();
        PublicKey pubKey = this.wallet.getKeys().get(privKey);
		Transaction reward = new Transaction();
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
}
