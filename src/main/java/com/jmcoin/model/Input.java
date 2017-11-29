package com.jmcoin.model;

import java.io.Serializable;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
public class Input implements Serializable{
	
	private static final long serialVersionUID = -7496600791646424812L;
	public static final int INDEX_REWARD = -1;
	private String prevTransHash;
	
	
	private int amount;	//to delete?? how do we know the value then?
	private byte[] prevTransactionHash;
	private Long id;
	
	public Input() {}
	
	public void setPrevTransHash(String prevTransHash) {
		this.prevTransHash = prevTransHash;
	}
	
	
	public String getPrevTransHash() {
		return prevTransHash;
	}
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
	
	//TODO recompute this
	public boolean equals(Input pInput) {
		return this.amount == pInput.amount  && this.prevTransHash.equals(pInput.prevTransHash) && this.prevTransactionHash == pInput.prevTransactionHash;
	}
	
	public int getSize() {
		return 8 + this.prevTransHash.getBytes().length;
	}

	public byte[] getPrevTransactionHash() {
		return prevTransactionHash;
	}

	public void setPrevTransactionHash(byte[] prevTransactionHash) {
		this.prevTransactionHash = prevTransactionHash;
	}
}
