package com.jmcoin.network;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Transaction;

public class MasterJMProtocolImpl extends JMProtocolImpl<MasterNode>{
	
	public MasterJMProtocolImpl() throws IOException {
		super(MasterNode.getInstance());
	}

//	@Override
//	protected String BroacastDebug(){
//		System.out.println("Thread #"+Thread.currentThread().getId() +" BROADCAST DEBUG MASTER NODE");
//		System.out.println("OK MASTER NODE KNOW THAT He need to say to everyone to stop mining");
//		return (craftMessage(NetConst.STOP_MINING, null));
//	}

	/*@Override
	protected String AskDebug(Object payload) {
		return craftMessage(NetConst.ANSWER_DEBUG, "PREJEN!");
	}

	@Override
	protected String AnswerDebug(Object payload) {
//		System.out.println("payload = "+ payload);
		return null;
	}

	@Override
	protected String SendBroacastDebug() { return null; }*/

	@Override
	protected String StopMining (){
		// TODO - extand answer to all RELAYS
//		System.out.println("Thread #"+Thread.currentThread().getId() +" BROADCAST DEBUG MASTER NODE");
//		System.out.println("OK MASTER NODE KNOWS THAT He needs to say to everyone to stop mining");
		return craftMessage(NetConst.STOP_MINING, null);
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
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_UNVERIFIED_TRANS, new Gson().toJson(transactions));
	}

	@Override
	protected String takeMyMinedBlockImpl(String payload) throws IOException {
		if (payload != null) {
			try {
				peer.processBlock(IOFileHandler.getFromJsonString(payload, Block.class));
				return StopMining();
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
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_DIFFICULTY, Integer.toString(this.peer.getDifficulty()));
	}
	
	@Override
	protected String giveMeRewardAmountImpl() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_REWARD_AMOUNT, Integer.toString(peer.getRewardAmount()));
	}
	
	@Override
	protected String giveMeBlockChainCopyImpl() {
		return JMProtocolImpl.craftMessage(NetConst.RECEIVE_BLOCKCHAIN_COPY, peer.getBlockChain());
	}

	@Override
	//TODO May be too big!
	protected String giveMeUnspentOutputs() {
		return new Gson().toJson(this.peer.getUnspentOutputs());
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
	protected void receiveUnspentOutputs(String string) {
		// TODO Auto-generated method stub
		
	}
}
