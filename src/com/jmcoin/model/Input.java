package com.jmcoin.model;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
public class Input {
	
	private String hashSha256, signature;
	
	public Input() {}
	
	public String getHashSha256() {
		return hashSha256;
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
