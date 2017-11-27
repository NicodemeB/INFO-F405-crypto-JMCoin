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
		for(int i = 0 ;i < 5; i++) {
			Input in = new Input();
			in.setAmount(i);
			in.setHashSha256("H"+i);
			in.setSignature("S"+i);
			Output out = new Output();
			out.setAmount(i+42);
			out.setPublicKey("Pk"+i);
			Transaction trans = new Transaction();
			trans.addInputOutput(in, out);
			this.unverifiedTransactions.add(trans);
		}
	}	
	
	public LinkedList<Transaction> getUnverifiedTransactions() {
		return unverifiedTransactions;
	}

	public int getDifficulty() {
		return this.difficulty;
	}
}
