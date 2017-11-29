package com.jmcoin.network;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Transaction;

public class MasterJMProtocolImpl extends JMProtocolImpl<MasterNode>{

	public MasterJMProtocolImpl() {
		super(MasterNode.getInstance());
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		return peer.getBlockChain();
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		return Integer.toString(peer.getRewardAmount());
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {
		List<Transaction> transactions;
		if(this.peer.getUnverifiedTransactions().size() > NetConst.MAX_SENT_TRANSACTIONS) {
			transactions = this.peer.getUnverifiedTransactions().subList(0, NetConst.MAX_SENT_TRANSACTIONS);
		}
		else {
			transactions = this.peer.getUnverifiedTransactions();
		}
		return new Gson().toJson(transactions);
	}

	@Override
	protected boolean takeMyMinedBlockImpl(String payload) {
		if (payload != null) {
			try {
				peer.processBlock(IOFileHandler.getFromJsonString(payload, Block.class));
				return true;
			}
			catch(JsonSyntaxException jse) {
				jse.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		try {
			Transaction transaction = IOFileHandler.getFromJsonString(payload, Transaction.class);
			this.peer.getUnverifiedTransactions().add(transaction);
			return true;
		}
		catch(JsonSyntaxException jse) {
			jse.printStackTrace();
		}
		return false;
	}

	@Override
	protected String giveMeDifficulty() {
		return Integer.toString(this.peer.getDifficulty());
	}
}
