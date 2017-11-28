package com.jmcoin.model;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Class Output.
 * Represents an output in a {@link Transaction}
 * @author enzo
 *
 */
public class Output implements Serializable {
	private static final long serialVersionUID = -1699190505094955025L;
	private int amount;
	private int inputIndex;
	private PublicKey pubKey;
	
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

	public PublicKey getPubKey() {
		return pubKey;
	}
	
	public Output() {}
	
	public boolean equals(Output pOutput) {
		return this.amount == pOutput.amount && this.inputIndex == pOutput.inputIndex;
	}
	
	public int getSize() {
		return 8 + this.pubKey.getEncoded().length;
	}
}
