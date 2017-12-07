package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.List;
import com.google.gson.JsonSyntaxException;
import com.jmcoin.model.Block;
import com.jmcoin.model.Transaction;

public class MasterJMProtocolImpl extends JMProtocolImpl<MasterNode>{
	
	public MasterJMProtocolImpl(MasterNode masterNode) throws IOException {
		super(masterNode);
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
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_UNVERIFIED_TRANS, this.peer.getGson().toJson(transactions));
	}

	@Override
	protected String takeMyMinedBlockImpl(String payload) throws IOException {
		if (payload != null) {
			try {
				this.peer.processMinedBlock(this.peer.getGson().fromJson(payload, Block.class));
				return stopMining();
			}
			catch(JsonSyntaxException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException jse) {
				jse.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		try {
			Transaction transaction = this.peer.getGson().fromJson(payload, Transaction.class);
			System.err.println("Hi, I just received a new Transaction!");
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
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_DIFFICULTY, Integer.toString(this.peer.getDifficulty()));
	}
	
	@Override
	protected String giveMeRewardAmountImpl() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_REWARD_AMOUNT, Integer.toString(this.peer.getRewardAmount()));
	}
	
	@Override
	protected String giveMeBlockChainCopyImpl() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_BLOCKCHAIN_COPY, this.peer.getGson().toJson(this.peer.getChain()));
	}

	@Override
	protected String giveMeUnspentOutputs() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_UNSPENT_OUTPUTS, this.peer.getGson().toJson(this.peer.getUnspentOutputs()));
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

	@Override
	protected String giveMeLastBlock() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_LAST_BLOCK, this.peer.getGson().toJson(this.peer.getLastBlock()));
	}

	@Override
	protected void receiveLastBlock(String block) {}

	@Override
	protected void receiveTransactionToThisAddress(String trans) {}

	@Override
	protected String giveMeTransactionsToThisAddress(String address) {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_TRANS_TO_THIS_ADDRESS, this.peer.getGson().toJson(this.peer.getTransactionsToThisAddress(address)));
	}
}
