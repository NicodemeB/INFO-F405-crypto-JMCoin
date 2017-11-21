package com.jmcoin.model;

import java.io.Serializable;

/**
 * Class Output.
 * Represents an output in a {@link Transaction}
 * @author enzo
 *
 */
public class Output implements Serializable {
	
	private int amount;
	private int inputIndex;
	
	public int getInputIndex() {
		return inputIndex;
	}

	public void setInputIndex(int inputIndex) {
		this.inputIndex = inputIndex;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	private String publicKey;
	
	public Output() {}
}
