package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Transaction implements Serializable {
	
	private static final long serialVersionUID = -1113345289965914322L;
	private ArrayList<Input> inputs;
	private ArrayList<Output> outputs;
	private String hash;
	private byte[] signature;
	private PublicKey pubKey;
	private Long id;
	
	public Transaction() {
		//TODO do we need to set a max ?
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
	}
	
	public PublicKey getPubKey() {
		return pubKey;
	}
	public byte[] getSignature() {
		return signature;
	}
	
	
	public String getHash() {
		return hash;
	}
	
	public ArrayList<Input> getInputs() {
		return inputs;
	}
	
	public List<Output> getOutputs() {
		return outputs;
	}
	/**
	 * Adds an input and an output in {@link #inputs} and {@link #outputs}
	 * Adds an {@link Input} only once
	 * @param pInput {@link Input}
	 * @param pOutput {@link Output}
	 */
	public void addInputOutput(Input pInput, Output pOutput) {
		if (pInput == null || pOutput == null){
			throw new IllegalArgumentException("Transaction.addInputOutput: Parameters cannot be null");
		}
		if (pOutput.getInputIndex() != 0) {
			return;
		}
		if(!doesListContain(pInput)) {
			this.inputs.add(pInput);
		}
		pOutput.setInputIndex(pInput.hashCode());
		this.outputs.add(pOutput);
	}
	
	private boolean doesListContain(Input pIn) {
		for(Input input : this.inputs) {
			if (input.equals(pIn)) return true;
		}
		return false;
	}
	
	public int getSize() {
		int size = 0;
		for(int i = 0; i < this.inputs.size(); i++) {
			size += this.inputs.get(i).getSize();
		}
		for(int i = 0; i < this.outputs.size(); i++) {
			size += this.outputs.get(i).getSize();
		}
		return size + this.hash.getBytes().length + this.signature.length + this.pubKey.getEncoded().length;
	}
	
	public boolean equals(Transaction transaction) {
		if(transaction == null || transaction.inputs == null || transaction.outputs == null || this.inputs.size() != transaction.inputs.size() || this.outputs.size() != transaction.outputs.size()) return false;
		for(int i = 0; i < this.inputs.size() && i < transaction.inputs.size(); i++) {
			if(!this.inputs.get(i).equals(transaction.inputs.get(i))) return false;
		}
		for(int i = 0; i < this.outputs.size() && i < transaction.outputs.size(); i++) {
			if(!this.outputs.get(i).equals(transaction.outputs.get(i))) return false;
		}
		return Arrays.equals(this.hash.getBytes(), transaction.hash.getBytes()) &&
				Arrays.equals(this.signature, transaction.signature) &&
				Arrays.equals(this.pubKey.getEncoded(), transaction.pubKey.getEncoded());
	}
}
