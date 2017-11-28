package com.jmcoin.network;

import java.io.IOException;
import java.util.List;

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

	
	public RelayNodeJMProtocolImpl() {
		super(new RelayNode());
	}

	@Override
	/**
	 * Wallet -> Relay -> Master
	 * ---- Blockchain copy ----
	 * Wallet <- Relay <- Master 
	 */
	protected String giveMeBlockChainCopyImpl() {
		return sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_BLOCKCHAIN_COPY, null);
	}

	@Override
	/**
	 * Miner -> Relay -> Master
	 * ------- reward ---------
	 * Miner <- Relay <- Master
	 */
	protected String giveMeRewardAmountImpl() {
		return sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_REWARD_AMOUNT, null);
	}

	@Override
	/**
	 * Miner -> Relay
	 * --- ntrans ---
	 * Miner <- Relay
	 */
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
				for(Transaction tr : this.peer.getUnverifiedTransactions()) {
					System.out.println(tr);
				}
				System.out.println("----------------------");
				Block b = IOFileHandler.getFromJsonString(payload, Block.class);
				for(final Transaction t : b.getTransactions()){
					this.peer.getUnverifiedTransactions().removeIf(t::equals);
				}
				for(Transaction tr : this.peer.getUnverifiedTransactions()) {
					System.out.println(tr);
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
				this.peer.getUnverifiedTransactions().add(transaction);
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
			try {
				int difficulty = Integer.parseInt(payload);
				peer.setDifficulty(difficulty);
				return true;
			}
			catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected int giveMeDifficulty() {
		return peer.getDifficulty();
	}

}
