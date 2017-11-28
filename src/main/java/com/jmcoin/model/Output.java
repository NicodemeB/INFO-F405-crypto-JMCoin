package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class Output.
 * Represents an output in a {@link Transaction}
 * @author enzo
 *
 */
@Entity
public class Output implements Serializable {
	private static final long serialVersionUID = -1699190505094955025L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	private Long id;
	@Basic(optional = false)
	private int amount;
	@Basic(optional = false)
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
	
	public boolean equals(Output pOutput) {
		return this.amount == pOutput.amount && this.inputIndex == pOutput.inputIndex;
	}
	
	public int getSize() {
		return 8 + this.publicKey.getBytes().length;
	}
}
