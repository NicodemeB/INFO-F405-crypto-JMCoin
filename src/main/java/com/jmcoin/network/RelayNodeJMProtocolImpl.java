package com.jmcoin.network;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Transaction;

/**
 * Class RelayNodeJMProtocolImpl
 * Implementation on the J-M protocol from the the {@link RelayNode}'s POV
 * @author enzo
 *
 */
public class RelayNodeJMProtocolImpl extends JMProtocolImpl<RelayNode> {

	private Queue<Transaction> unverifiedTransactions;
	
	public RelayNodeJMProtocolImpl() {
		super(new RelayNode());
		this.unverifiedTransactions = new LinkedList<>();
	}
	

	@Override
	/**
	 * Wallet -> Relay -> Master
	 * ---- Blockchain copy ----
	 * Wallet <- Relay <- Master 
	 */
	protected String giveMeBlockChainCopyImpl() {
		try {
			Client client = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME);
			client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY));
			String response = client.readMessage().toString();
			client.close();
			return response;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	/**
	 * Miner -> Relay -> Master
	 * ------- reward ---------
	 * Miner <- Relay <- Master
	 */
	protected String giveMeRewardAmountImpl() {
		try {
			Client client = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME);
			client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_REWARD_AMOUNT));
			String response = client.readMessage().toString();
			client.close();
			return response;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	/**
	 * Miner -> Relay
	 * --- ntrans ---
	 * Miner <- Relay
	 */
	protected String giveMeUnverifiedTransactionsImpl() {
		Transaction transaction = this.unverifiedTransactions.poll();
		return transaction == null ? null : new Gson().toJson(transaction);
	}

	@Override
	/**
	 * Miner -> Relay -> Miners
	 * 				  -> Master
	 * ----- mined block ------
	 */
	protected boolean takeMyMinedBlockImpl(String payload) {
		if(payload != null) {
			try {
				Client client = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME);
				client.sendMessage(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_MINED_BLOCK, payload));
				client.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	/**
	 * Wallet -> Relay
	 * -- new trans --
	 */
	protected boolean takeMyNewTransactionImpl(String payload) {
		if (payload != null) {
			try {
				Transaction transaction = IOFileHandler.getFromJsonString(payload, Transaction.class);
				this.unverifiedTransactions.add(transaction);
				return true;
			}
			catch(JsonSyntaxException jse) {
				jse.printStackTrace();
			}
		}
		return false;
	}

	@Override
	/**
	 * Master -> Relay -> Miners
	 * ------- new diff. -------
	 */
	protected boolean takeUpdatedDifficultyImpl(String payload) {
		if(payload != null) {
			//TODO broadcast the difficulty
		}
		return false;
	}

}
