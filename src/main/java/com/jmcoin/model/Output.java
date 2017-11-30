package com.jmcoin.model;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Class Output.
 * Represents an output in a {@link Transaction}
 * @author enzo
 *
 */
public class Output implements Serializable {
	private static final long serialVersionUID = -1699190505094955025L;
	private String address;
	private double amount;
//	private int inputIndex;
//	private PublicKey pubKey;
	private Long id;
        
	/*public int getInputIndex() {
		return inputIndex;
	}
	public void setInputIndex(int inputIndex) {
		this.inputIndex = inputIndex;
	}*/
    public Output(double amount, String address){
        this.amount = amount;
        this.address = address;
    }
	public double getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public Output() {}
	
	public boolean equals(Output pOutput) {
		return this.amount == pOutput.amount && this.address.equals(pOutput.getAddress());
	}
	
	public int getSize() {
		return 4 + (this.address == null ? 0 : this.address.length());
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Returns obj an an array of bytes
	 * @return array of bytes
	 */
	public byte[] getBytes() {
		ByteBuffer bf = ByteBuffer.allocate(getSize());
		bf.putDouble(this.amount);
		if(this.address!=null)bf.put(this.address.getBytes());
		return bf.array();
	}
}
