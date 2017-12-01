package com.jmcoin.model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.persistence.Basic;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
public class Input implements Serializable{
	
    private static final long serialVersionUID = -7496600791646424812L;
    public static final int INDEX_REWARD = -1;	
    public String address;
    private double amount;
    private byte[] prevTransactionHash;
    @Basic(optional = false)
    private Long id;
    
    public Input(){};
    public Input(String adr, double amount, byte[] prevTrans)
    {
        this.address = adr;
        this.amount = amount;
        this.prevTransactionHash = prevTrans;
    }
    public double getAmount() {
        return amount;
    }

    /**
     * We don't want a number here, since this should always be equal to the amount of the output generating the input
     * @param o
     */
    public void setAmount(Output o) {
        this.amount = o.getAmount();
    }
	
	public boolean equals(Input pInput) {
		return this.amount == pInput.amount  && Arrays.equals(this.prevTransactionHash, pInput.prevTransactionHash);
	}
	
	public int getSize() {
		return  Double.BYTES + (this.prevTransactionHash == null ? 0 : this.prevTransactionHash.length);
	}
	
	public byte[] getBytes() {
		ByteBuffer bf = ByteBuffer.allocate(getSize());
		bf.putDouble(this.amount);
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
