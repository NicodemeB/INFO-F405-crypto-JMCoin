package com.jmcoin.model;

import java.util.*;
import java.util.function.Consumer;

public class Chain implements Iterable<Block> {

    private List<Block> blocks;

    public Chain() {
        this.blocks = new LinkedList<>();
    }

    public void addBlock(Block block){
        blocks.add(Objects.requireNonNull(block));
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
    	for(Block block : this.blocks) {
    		if (block.getFinalHash().equals(pBlock.getPrevHash())) {
    			return true;
			}
    	}
    	return false;
    }

    @Override
    public Iterator<Block> iterator() {
        return blocks.iterator();
    }

    @Override
    public void forEach(Consumer<? super Block> action) {
        blocks.forEach(action);
    }

    @Override
    public Spliterator<Block> spliterator() {
        return blocks.spliterator();
    }

}
