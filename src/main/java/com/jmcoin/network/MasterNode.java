package com.jmcoin.network;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Reward;

public class MasterNode extends Peer{

    private static MasterNode instance = new MasterNode();

    private Chain chain;

    private MasterNode(){
    	super();
    	chain = new Chain();
    }

    public static MasterNode getInstance(){
        return instance;
    }
    
    /**
     * TODO read blockchain in the file and send it in JSON format
     * @return the blockchain data in JSON
     */
    public String getBlockChain() {
        return IOFileHandler.toJson(chain);
    	//return "Here is last version of the blockchain";
    }
    
    //FIXME computes the value of the next reward ?
    public int getRewardAmount() {
    	return Reward.REWARD_START_VALUE / ((IOFileHandler.getFromJsonString(getBlockChain(), Chain.class).getSize() / Reward.REWARD_RATE) + 1);
    }
    
    public void processBlock(Block pBlock) {
    	Chain chain = IOFileHandler.getFromJsonString(getBlockChain(), Chain.class);
    	if(chain.canBeAdded(pBlock))
    		chain.addBlock(pBlock);
    }
}
