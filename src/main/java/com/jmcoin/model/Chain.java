package com.jmcoin.model;

import java.math.BigInteger;
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
    
    public Map<String, Block> getBlocks() {
		return blocks;
	}

    //TODO check if block is valid and can be added
    public boolean canBeAdded(Block pBlock){
    	if(!pBlock.verifyHash(pBlock.getFinalHash().getBytes())) return false;
    	if (!doesPrevBlocKExists(pBlock)) return false;
    	if (pBlock.getSize() > Block.MAX_BLOCK_SIZE) return false;
    	for(Transaction transaction : pBlock.getTransactions()) {
    		
    	}
    	return true;
    }
    
    public boolean isFinalHashRight(Block pBlock) {
    	BigInteger value = new BigInteger(pBlock.getFinalHash(), 16);
    	return value.shiftRight(32*8 - pBlock.getDifficulty()).intValue() == 0;
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

    public int getSize(){
        return blocks.size();
    }

}
