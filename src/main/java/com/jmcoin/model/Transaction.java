package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

@Entity
public class Transaction implements Serializable {
	
        
	private static final long serialVersionUID = -1113345289965914322L;
    @Id
	private ArrayList<Input> inputs;
	private Output outputOut;
	private Output outputBack; 	//can be null
	private byte[] hash;
	private byte[] signature;
	private PublicKey pubKey;
	private Long id;
	
	public Transaction() {
		inputs = new ArrayList<>();
	}

	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}
	
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
/*	public void setInputs(ArrayList<Input> inputs) {
		this.inputs = inputs;
	}
*/
	
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
		for(Input input : this.inputs)
			size+=input.getSize();
		size+= (this.outputBack == null ? 0 : this.outputBack.getSize());
		size+= (this.outputOut == null ? 0 : this.outputOut.getSize());
		size+= (this.hash == null ? 0 : this.hash.length);
		size+= (this.signature == null ? 0 : this.signature.length);
		size+= (this.pubKey == null ? 0 : this.pubKey.getEncoded().length);
		return size;
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
	
	
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(getSize());
		for(Input input : this.inputs) {
			buf.put(input.getBytes());
		}
		if(this.signature != null)buf.put(this.signature);
		if(this.pubKey != null)buf.put(pubKey.getEncoded());
		if(this.outputBack != null)buf.put(outputBack.getBytes());
		if(this.outputOut != null)buf.put(outputOut.getBytes());
		return buf.array();
		
	}
	
	/**
	 * Computes the hash based on all properties but the {@link #hash}
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public void computeHash() throws NoSuchAlgorithmException {
		MessageDigest dig = MessageDigest.getInstance("SHA-256");
        dig.update(getBytes());
		this.hash = dig.digest();
	}
}
