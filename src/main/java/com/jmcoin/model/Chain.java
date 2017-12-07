package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "Chain.findAll", query = "SELECT c FROM Chain c")
})
public class Chain implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyColumn(name="BLOCKS_KEY", table="CHAIN_BLOCK")
    private Map<String, Block> blocks;

    public Chain() {
        this.blocks = new HashMap<>();
    }
    
    public Map<String, Block> getBlocks() {
		return blocks;
	}
    
    public int getSize(){
        return blocks.size();
    }
    
    public Transaction findInBlockChain(byte[] hashTrans) {
		for(String s : this.blocks.keySet()) {
			Block b = this.blocks.get(s);
			for(Transaction trans : b.getTransactions()) {
				if(Arrays.equals(trans.getHash(), hashTrans))return trans;
			}
		}
		return null;
	}

}
