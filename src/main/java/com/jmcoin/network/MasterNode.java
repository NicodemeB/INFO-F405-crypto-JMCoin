package com.jmcoin.network;

import java.util.LinkedList;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Output;
import com.jmcoin.model.Reward;
import com.jmcoin.model.Transaction;

public class MasterNode extends Peer{

    private static MasterNode instance = new MasterNode();
    private LinkedList<Transaction> unverifiedTransactions;
    private LinkedList<Output> unspentOutputs;
    private Chain chain;
    
    private int difficulty = NetConst.DEFAULT_DIFFICULTY;

    private MasterNode(){
    	super();
    	chain = new Chain();
    	this.unverifiedTransactions = new LinkedList<>();
    	this.unspentOutputs = new LinkedList<>();
    }
    
    public LinkedList<Output> getUnspentOutputs() {
		return unspentOutputs;
	}
	
	public LinkedList<Transaction> getUnverifiedTransactions() {
		return unverifiedTransactions;
	}

    public static MasterNode getInstance(){
        return instance;
    }
    
    public int getDifficulty() {
		return difficulty;
	}
    
    /**
     * TODO read blockchain in the file and send it in JSON format
     * @return the blockchain data in JSON
     */
    public String getBlockChain() {
        return IOFileHandler.toJson(chain);
    }
    
    //TODO compute this reard according to the the size of the transaction
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
			this.unverifiedTransactions.removeIf(trans::equals);
			/*Output outputOut = trans.getOutputOut();
			Output outputBack = trans.getOutputBack();*/
		}
    	if(chain.canBeAdded(pBlock))
    		chain.addBlock(pBlock);
    }
}
