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
    private boolean canBeAdded(Block b){
         throw new UnsupportedOperationException("Not done yet");
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
