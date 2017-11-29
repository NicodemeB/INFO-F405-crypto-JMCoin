package com.jmcoin.model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
public class Input implements Serializable{
	
	private static final long serialVersionUID = -7496600791646424812L;
	public static final int INDEX_REWARD = -1;	
	
	private int amount;	//to delete?? how do we know the value then?
	private byte[] prevTransactionHash;
	private Long id;
	
	public Input() {}
	
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
	
	public boolean equals(Input pInput) {
		return this.amount == pInput.amount  && Arrays.equals(this.prevTransactionHash, pInput.prevTransactionHash);
	}
	
	public int getSize() {
		return  4 + (this.prevTransactionHash == null ? 0 : this.prevTransactionHash.length);
	}
	
	public byte[] getBytes() {
		ByteBuffer bf = ByteBuffer.allocate(getSize());
		bf.putInt(this.amount);
		if(this.prevTransactionHash != null)bf.put(this.prevTransactionHash);
		return bf.array();
	}

	public byte[] getPrevTransactionHash() {
		return prevTransactionHash;
	}

	public void setPrevTransactionHash(byte[] prevTransactionHash) {
		this.prevTransactionHash = prevTransactionHash;
	}
}
