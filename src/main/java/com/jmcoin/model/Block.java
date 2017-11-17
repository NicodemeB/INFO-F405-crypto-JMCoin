package com.jmcoin.model;

import java.util.List;

/**
 * Class bloc
 * Represent the bloc containing transactions
 * @author franckfadeur
 *
 */
public class Block {
	private List<Transaction> transactions;
	private int difficulty;
	private long timeCreation;
	private int size;
	private String finalHash;
	private String prevHash;
	
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
}
