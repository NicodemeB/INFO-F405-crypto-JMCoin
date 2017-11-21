package com.jmcoin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Transaction implements Serializable {
	
	private HashSet<Input> inputs;
	private ArrayList<Output> outputs;
	
	public Transaction() {
		//TODO do we need to set a max ?
		inputs = new HashSet<>();
		outputs = new ArrayList<>();
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
		if(!this.inputs.contains(pInput)) {
			this.inputs.add(pInput);
		}
		pOutput.setInputIndex(pInput.hashCode());
		this.outputs.add(pOutput);
	}
}
