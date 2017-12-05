package com.jmcoin.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Reward;
import com.jmcoin.model.Transaction;

public class MasterNode extends Peer{

    private static MasterNode instance = new MasterNode();
    private LinkedList<Transaction> unverifiedTransactions;
    
    private Map<String, Output> unspentOutputs; //key = hash of the transaction containing the output
    private Chain chain;
    private Block lastBlock;
    
    private int difficulty = NetConst.DEFAULT_DIFFICULTY;

    private MasterNode(){
    	super();
    	chain = new Chain();
    	this.unverifiedTransactions = new LinkedList<>();
    	this.unspentOutputs = new HashMap<>();
    	this.lastBlock = new Block();
    	this.lastBlock.setDifficulty(32);
    	this.lastBlock.setFinalHash("h0");
    }
    
    public Block getLastBlock() {
		return lastBlock;
	}
    
    public Map<String, Output> getUnspentOutputs() {
		return unspentOutputs;
	}
	
	protected LinkedList<Transaction> getUnverifiedTransactions() {
		return unverifiedTransactions;
	}

    public static MasterNode getInstance(){
        return instance;
    }
    
    public int getDifficulty() {
		return difficulty;
	}
    
    public Chain getChain() {
		return chain;
	}
    
    //TODO compute this reward according to the the size of the transaction
    //almost empty -> low reward
    public int getRewardAmount() {
    	return Reward.REWARD_START_VALUE / ((chain.getSize() / Reward.REWARD_RATE) + 1);
    }
    
    /**
     * TODO Fork -> some verified transactions could set as "unverified". Same thing with spent outputs
     * @param pBlock
     */
    public void processBlock(Block pBlock) {
		for(final Transaction trans : pBlock.getTransactions()){
			//remove from unverified transaction
			this.unverifiedTransactions.removeIf(trans::equals);
			//check unspent outputs
			String address = SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey());
			for(Input input : trans.getInputs()) {
				Transaction prevTrans = chain.findInBlockChain(input.getPrevTransactionHash());
				if(prevTrans == null) {
					//Reward
				}
				else {
					Output outToMe = null; //in previous transaction, find the output which was for me
					if(prevTrans.getOutputBack().getAddress().equals(address))
						outToMe = prevTrans.getOutputBack();
					else if (prevTrans.getOutputOut().getAddress().equals(address))
						outToMe = prevTrans.getOutputOut();
					else
						return; //not normal
					double diff = outToMe.getAmount() - input.getAmount();
					if(diff != 0)
						return; //not normal
					else if(diff == 0)
						this.unspentOutputs.remove(Hex.toHexString(prevTrans.getHash()));//remove from unspent outputs
				}
			}
			this.unspentOutputs.put(Hex.toHexString(trans.getHash()), trans.getOutputOut());
			if(trans.getOutputBack() != null) {
				this.unspentOutputs.put(Hex.toHexString(trans.getHash()), trans.getOutputBack());
			}
		}
    	chain.addBlock(pBlock);
    	this.lastBlock = pBlock;
    }
}
