package com.jmcoin.network;

import java.util.LinkedList;

import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

/**
 * Class RelayNode
 * Represents a peer allowing communication over the network
 * @author enzo
 */

public class RelayNode extends Peer{

	private LinkedList<Transaction> unverifiedTransactions;
	private int difficulty;

	public RelayNode() {
		super();
		this.difficulty = NetConst.DEFAULT_DIFFICULTY;
		this.unverifiedTransactions = new LinkedList<>();
	}	
	
	public LinkedList<Transaction> getUnverifiedTransactions() {
		return unverifiedTransactions;
	}

	public int getDifficulty() {
		return this.difficulty;
	}
	
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
}
