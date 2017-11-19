package com.jmcoin.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class bloc
 * Represent the bloc containing transactions
 * @author franckfadeur
 *
 */
public class Block {
	
	public static final int MAX_BLOCK_SIZE = 1024; //TODO do we need to set this value ?
	private List<Transaction> transactions;
	private int difficulty;
	private long timeCreation;
	private int size;
	private String finalHash;
	private String prevHash;
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
	
	public String hashMe() {
		return null;
	}
	
	public boolean verifyHash() {
		BigInteger value = new BigInteger(finalHash, 16);
		return value.shiftRight(32*8 - difficulty).intValue() == 0;
	}
}
