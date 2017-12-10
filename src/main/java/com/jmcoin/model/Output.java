package com.jmcoin.model;

import java.io.Serializable;
import java.nio.ByteBuffer;

import javax.persistence.*;

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
	private String address;
	@Basic(optional = false)
	private double amount;
	
    public Output(double amount, String address){
        this.amount = amount;
        this.address = address;
    }
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public Output() {}
	
	public boolean equals(Output pOutput) {
		return this.amount == pOutput.amount && (this.address == null && pOutput.address == null) || (this.address.equals(pOutput.getAddress()));
	}
	
	public int getSize() {
		return Double.BYTES + (this.address == null ? 0 : this.address.length());
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
