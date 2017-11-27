package com.jmcoin.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
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
		return sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_BLOCKCHAIN_COPY);
	}

	@Override
	/**
	 * Miner -> Relay -> Master
	 * ------- reward ---------
	 * Miner <- Relay <- Master
	 */
	protected String giveMeRewardAmountImpl() {
		return sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_REWARD_AMOUNT);
	}

	@Override
	/**
	 * Miner -> Relay
	 * --- ntrans ---
	 * Miner <- Relay
	 */
	protected String giveMeUnverifiedTransactionsImpl() {
		Transaction transactions = this.unverifiedTransactions.poll();
		return transactions == null ? null : new Gson().toJson(transactions);
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
				Block block = IOFileHandler.getFromJsonString(payload, Block.class);
				if(block == null || block.getTransactions() != null)return false;
				ArrayList<Integer> indexes = new ArrayList<>(block.getTransactions().size());
				for(int i = 0; i < block.getTransactions().size(); i++) {
					int k = 0;
					for(Transaction unvfTransaction : this.unverifiedTransactions) {
						if(block.getTransactions().get(i).equals(unvfTransaction)) {
							indexes.add(k);
						}
					}
				}
				Client client = new Client(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME);
				client.sendMessage(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_MINED_BLOCK, payload));
				client.close();
				Block b = IOFileHandler.getFromJsonString(payload, Block.class);
				for(final Transaction t : b.getTransactions()){
					unverifiedTransactions.removeIf(t::equals);
				}
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
