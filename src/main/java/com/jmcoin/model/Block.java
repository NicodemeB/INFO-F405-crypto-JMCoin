package com.jmcoin.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import javax.persistence.*;

/**
 * Class bloc
 * Represent the bloc containing transactions
 * @author franckfadeur
 *
 */

@Entity
public class Block implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	private Long id;

	@Transient
	public static final int MAX_BLOCK_SIZE = 1024; //TODO do we need to set this value ?

	@OneToMany(cascade = CascadeType.ALL)
	private List<Transaction> transactions;
	@Basic(optional = false)
	private int difficulty;
	@Basic(optional = false)
	private long timeCreation;
	@Basic(optional = false)
	private int size;
	@Basic(optional = false)
	private String finalHash;
	@Basic(optional = false)
	private String prevHash;
	@Basic(optional = false)
	private int nonce;
	
	public Block() {
		transactions = new ArrayList<>(10); //FIXME do we need to set an arbitrary value ?
	}
	
	public int getNonce() {
		return nonce;
	}
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public long getTimeCreation() {
		return timeCreation;
	}
	public void setTimeCreation(long timeCreation) {
		this.timeCreation = timeCreation;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getFinalHash() {
		return finalHash;
	}
	public void setFinalHash(String finalHash) {
		this.finalHash = finalHash;
	}
	public String getPrevHash() {
		return prevHash;
	}
	public void setPrevHash(String prevHash) {
		this.prevHash = prevHash;
	}
	
	public boolean verifyHash(byte[] bytes) {
		return new BigInteger(bytes).shiftRight(32*8 - difficulty).intValue() == 0;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
