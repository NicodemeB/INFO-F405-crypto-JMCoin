package com.jmcoin.model;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Class Output.
 * Represents an output in a {@link Transaction}
 * @author enzo
 *
 */
public class Output implements Serializable {
	private static final long serialVersionUID = -1699190505094955025L;
	private int address;
	private int amount;
	private int inputIndex;
	private PublicKey pubKey;
	private Long id;
	
	public int getInputIndex() {
		return inputIndex;
	}
	
	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
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

	public PublicKey getPubKey() {
		return pubKey;
	}
	
	public Output() {}
	
	public boolean equals(Output pOutput) {
		return this.amount == pOutput.amount && this.inputIndex == pOutput.inputIndex && Arrays.equals(this.pubKey.getEncoded(), pOutput.getPubKey().getEncoded());
	}
	
	public int getSize() {
		return 8 + this.pubKey.getEncoded().length;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}
}
