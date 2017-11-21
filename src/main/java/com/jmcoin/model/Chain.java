package com.jmcoin.model;

import java.util.*;

public class Chain {

    private Map<String, Block> blocks;

    public Chain() {
        this.blocks = new HashMap<>();
    }

    public boolean addBlock(Block block){
        Objects.requireNonNull(block);
        if(canBeAdded(block)) {
            blocks.put(block.getFinalHash() + block.getTimeCreation(), block);
            return true;
        }
        return false;
    }

    //TODO check if block is valid and can be added
    private boolean canBeAdded(Block pBlock){
    	if(!pBlock.verifyHash()) return false;
    	if (!doesPrevBlocKExists(pBlock)) return false;
    	if (pBlock.getSize() > Block.MAX_BLOCK_SIZE) return false;
    	for(Transaction transaction : pBlock.getTransactions()) {
    		
    	}
    	return true;
    }
    
    /**
     * Checks if the previous block exists in the chain, based on the hash
     * @param pBlock
     * @return
     */
    private boolean doesPrevBlocKExists(Block pBlock) {
    	for(String key : this.blocks.keySet()) {
    		if (blocks.get(key).getFinalHash().equals(pBlock.getPrevHash())) {
    			return true;
			}
    	}
    	return false;
    }

}
