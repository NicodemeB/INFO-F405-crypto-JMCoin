package com.jmcoin.network;

import java.io.IOException;
import java.util.List;
import com.google.gson.JsonSyntaxException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Transaction;

public class MasterJMProtocolImpl extends JMProtocolImpl<MasterNode>{
	
	public MasterJMProtocolImpl() throws IOException {
		super(MasterNode.getInstance());
	}

	@Override
	protected String stopMining (){
		return craftMessage(NetConst.STOP_MINING, null);
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {
		List<Transaction> transactions = this.peer.getUnverifiedTransactions().size() > NetConst.MAX_SENT_TRANSACTIONS ?
				this.peer.getUnverifiedTransactions().subList(0, NetConst.MAX_SENT_TRANSACTIONS):
					this.peer.getUnverifiedTransactions();
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_UNVERIFIED_TRANS, IOFileHandler.toJson(transactions));
	}

	@Override
	protected String takeMyMinedBlockImpl(String payload) throws IOException {
		if (payload != null) {
			try {
				peer.processBlock(IOFileHandler.getFromJsonString(payload, Block.class));
				return stopMining();
			}
			catch(JsonSyntaxException jse) {
				jse.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		try {
			this.peer.getUnverifiedTransactions().add(IOFileHandler.getFromJsonString(payload, Transaction.class));
			return true;
		}
		catch(JsonSyntaxException jse) {
			jse.printStackTrace();
		}
		return false;
	}

	@Override
	protected String giveMeDifficulty() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_DIFFICULTY, Integer.toString(this.peer.getDifficulty()));
	}
	
	@Override
	protected String giveMeRewardAmountImpl() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_REWARD_AMOUNT, Integer.toString(peer.getRewardAmount()));
	}
	
	@Override
	protected String giveMeBlockChainCopyImpl() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_BLOCKCHAIN_COPY, IOFileHandler.toJson(peer.getChain()));
	}

	@Override
	//FIXME May be too big!
	protected String giveMeUnspentOutputs() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_UNSPENT_OUTPUTS, IOFileHandler.toJson(this.peer.getUnspentOutputs()));
	}

	@Override
	protected void receiveDifficulty(String string) {}

	@Override
	protected void receiveUnverifiedTransactions(String string) {}

	@Override
	protected void receiveRewardAmount(String string) {}

	@Override
	protected void receiveBlockchainCopy(String nextToken) {}

	@Override
	protected void receiveUnspentOutputs(String string) {}
}
