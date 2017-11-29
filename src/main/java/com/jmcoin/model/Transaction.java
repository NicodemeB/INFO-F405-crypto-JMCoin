package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

@Entity
public class Transaction implements Serializable {
	
	private static final long serialVersionUID = -1113345289965914322L;
	private ArrayList<Input> inputs;
	private Output outputOut;
	private Output outputBack;
	private byte[] hash;
	private byte[] signature;
	private PublicKey pubKey;
	private Long id;
	
	public Transaction() {
		//TODO do we need to set a max ?
		inputs = new ArrayList<>();
	}
	
	public PublicKey getPubKey() {
		return pubKey;
	}
	public byte[] getSignature() {
		return signature;
	}
	
	
	public byte[] getHash() {
		return hash;
	}
	
	public ArrayList<Input> getInputs() {
		return inputs;
	}
	
	public Output getOutputOut() {
		return outputOut;
	}
	public Output getOutputBack() {
		return outputBack;
	}
	/**
	 * Adds an input and an output in {@link #inputs} and {@link #outputs}
	 * Adds an {@link Input} only once
	 * @param pInput {@link Input}
	 * @param pOutput {@link Output}
	 */
	public void addInput(Input i) {
		if(i == null) {
			throw new IllegalArgumentException("Transaction.addInput: Parameter cannot be null");
		}
		if(!this.inputs.contains(i)) {
			inputs.add(i);
		}
	}
	public void setOutputOut(Output o) {
		if(o == null) {
			throw new IllegalArgumentException("Transaction.addOunput: Parameter cannot be null");
		}
		this.outputOut = o;
	}
	public void setOutputBack(Output o) {
		if(o == null) {
			throw new IllegalArgumentException("Transaction.addOutputBack: Parameter cannot be null");
		}
		this.outputBack = o;

	}
	
	
	public int getSize() {
		int size = 0;
		for(int i = 0; i < this.inputs.size(); i++) {
			size += this.inputs.get(i).getSize();
		}
		size += this.outputOut.getSize();
		size += this.outputBack.getSize();
		return size + this.hash.length + this.signature.length + this.pubKey.getEncoded().length;
	}
	
	public boolean equals(Transaction transaction) {
		if(transaction == null || transaction.inputs == null || transaction.outputOut == null || transaction.outputBack == null || this.inputs.size() != transaction.inputs.size()) return false;
		for(int i = 0; i < this.inputs.size() && i < transaction.inputs.size(); i++) {
			if(!this.inputs.get(i).equals(transaction.inputs.get(i))) return false;
		}
		
		if(!this.outputOut.equals(transaction.outputOut)) return false;
		if(!this.outputBack.equals(transaction.outputBack)) return false;
		
		return Arrays.equals(this.hash, transaction.hash) &&
				Arrays.equals(this.signature, transaction.signature) &&
				Arrays.equals(this.pubKey.getEncoded(), transaction.pubKey.getEncoded());
	}
	public boolean isValid() {
		int total = 0;
		for(Input i : inputs) {

			String unvf = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_UNSPENT_OUTPUTS, null);
			Output[] unspentOutputs = IOFileHandler.getFromJsonString(unvf, Output[].class);
			//if i.output is not in unspent ouputs pool -> false
			//if i.output.address is not this.outputs[0].address -> false
			total += i.getAmount();
		}
		total += outputOut.getAmount();
		total += outputBack.getAmount();
		if(total != 0)return false;
		
		return false;
	}
}
