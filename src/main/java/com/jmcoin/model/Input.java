package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PublicKey;

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
	private int amount;
	private int outputPrevTrans;
	
	/**
	 * @{@link #INDEX_REWARD} is Reward
	 */
	private Long id;
	private String hashSha256;
	private String signature;
	
	public Input() {}
	
	public void setPrevTransHash(String prevTransHash) {
		this.prevTransHash = prevTransHash;
	}
	
	public int getOutputPrevTrans() {
		return outputPrevTrans;
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
		return this.amount == pInput.amount  && this.prevTransHash.equals(pInput.prevTransHash);
	}
	
	public int getSize() {
		return 4 + this.prevTransHash.getBytes().length;
	}
}
