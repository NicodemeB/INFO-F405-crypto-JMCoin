package com.jmcoin.network;

import com.jmcoin.model.Block;

public class MasterNode extends Peer{

    private static MasterNode instance = new MasterNode();

    private MasterNode(){
    	super();		
    }

    public static MasterNode getInstance(){
        return instance;
    }
    
    /**
     * TODO read blockchain in the file and send it in JSON format
     * @return the blockchain data in JSON
     */
    public String getBlockChain() {
    	return "Here is last version of the blockchain";
    }
    
    //FIXME remove this
    public int getRewardAmount() {
    	return 42;
    }
    
    //TODO process block
    public void processBlock(Block pBlock) {
    }
}
