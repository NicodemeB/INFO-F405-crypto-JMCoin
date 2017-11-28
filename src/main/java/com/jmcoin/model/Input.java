package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
@Entity
public class Input implements Serializable{
	
	private static final long serialVersionUID = -7496600791646424812L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	private Long id;
	@Basic(optional = false)
	private String hashSha256;
	@Basic(optional = false)
	private String signature;
	@Basic(optional = false)
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
	
	public boolean equals(Input pInput) {
		return this.amount == pInput.amount && this.signature.equals(pInput.signature)  && this.hashSha256.equals(pInput.hashSha256);
	}
	
	public int getSize() {
		return 4 + this.hashSha256.getBytes().length + this.signature.getBytes().length;
	}
}
