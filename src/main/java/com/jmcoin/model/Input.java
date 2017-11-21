package com.jmcoin.model;

import java.io.Serializable;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
public class Input implements Serializable {
	
	private String hashSha256, signature;
        public int amount;
	
	public Input() {}

    public int getAmount() {
        return amount;
    }
	
	public String getHashSha256() {
		return hashSha256;
	}

    public void setAmount(int amount) {
        this.amount = amount;
    }

	public void setHashSha256(String hashSha256) {
		this.hashSha256 = hashSha256;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
        
}
