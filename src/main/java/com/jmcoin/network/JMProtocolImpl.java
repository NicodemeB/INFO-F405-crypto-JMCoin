package com.jmcoin.network;

import java.io.IOException;
import java.util.StringTokenizer;

import com.jmcoin.model.Block;
import com.jmcoin.model.Transaction;

/**
 * Global protocol. Is redefined is sub-classes in order to fit requirements for each node
 * @author enzo
 */
public abstract class JMProtocolImpl<X extends Peer> {
	
	protected X peer;
	
	public JMProtocolImpl(X peer) {
		this.peer = peer;
	}
	
	public X getPeer() {
		return peer;
	}
	
	public String processInput(Object message) {
		//FIXME the content of this method depends on the way we exchange data. Please update
		//the following carefully.
		//At this time, we will assume that $message is a String
		String content = (String)message;
		/**
		 * Will assume that the payload is built as follows:
		 * x$yyyyyyyyyyyyy$#
		 * where x is the type
		 * and y the payload itself (can be empty, like 0$$#)
		 */
		StringTokenizer tokenizer = new StringTokenizer(content, String.valueOf(NetConst.DELIMITER));
		if(!tokenizer.hasMoreTokens()) return null;
		String type = tokenizer.nextToken();
		if(type != null && type.length() != 0) {
			if(!tokenizer.hasMoreTokens()) return null;
			int code = 0;
			try {
			    code = Integer.parseInt(type);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			switch ((char)code) {
			case NetConst.GIVE_ME_BLOCKCHAIN_COPY:
				return giveMeBlockChainCopyImpl();
			case NetConst.GIVE_ME_REWARD_AMOUNT:
				return giveMeRewardAmountImpl();
			case NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS:
				return giveMeUnverifiedTransactionsImpl();
			case NetConst.TAKE_MY_MINED_BLOCK:
				takeMyMinedBlockImpl(tokenizer.nextToken());
				break;
			case NetConst.TAKE_MY_NEW_TRANSACTION:
				takeMyNewTransactionImpl(tokenizer.nextToken());
				break;
			case NetConst.TAKE_UPDATED_DIFFICULTY:
				takeUpdatedDifficultyImpl(tokenizer.nextToken());
				break;
			case NetConst.GIVE_ME_DIFFICULTY:
				return Integer.toString(giveMeDifficulty());
			default:
				return NetConst.ERR_NOT_A_REQUEST;
			}
		}
		return NetConst.ERR_BAD_REQUEST;
	}
	
	/**
	 * Returns the last version of the blockchain
	 * @return blockchain as a string
	 */
	protected abstract String giveMeBlockChainCopyImpl();
	
	/**
	 * Returns the last amount of the reward, computed according to the time
	 * (or arbitrarily set)
	 * @return amount of the reward
	 */
	protected abstract String giveMeRewardAmountImpl();
	
	/**
	 * Returns all non-verified pending transactions
	 * @return set of transactions
	 */
	protected abstract String giveMeUnverifiedTransactionsImpl();
	
	/**
	 * Gets a new mined {@link Block}, and returns false only if the body cannot be parsed
	 * Getting a "true" doesn't mean that the {@link Block} is valid regarding to the protocol
	 * @param payload {@link Block} to parse
	 * @return true if the {@link Block} has been received properly
	 */
	protected abstract boolean takeMyMinedBlockImpl(String payload);
	
	/**
	 * Gets a new {@link Transaction}, and returns false only if the body cannot be parsed
	 * Getting a "true" doesn't mean that the {@link Transaction} is valid regarding to the protocol
	 * @param payload {@link Transaction} to parse
	 * @return true if the {@link Transaction} has been received properly
	 */
	protected abstract boolean takeMyNewTransactionImpl(String payload);
	
	/**
	 * Gets the new diffiulty from the master, computed according to the power of the network
	 * (or arbitrarily set)
	 * @param payload the difficulty, expessed as a number of bits
	 * @return true if value has been received properly
	 */
	protected abstract boolean takeUpdatedDifficultyImpl(String payload);
	
	
	protected abstract int giveMeDifficulty();
	/**
	 * Builds a message to send over the network, compliant with the protocol
	 * @param request {@link NetConst}.xxxxx
	 * @param body JSON object if needed
	 * @return message
	 */
	public static String craftMessage(int request, String body) {
		return request + "$" + body + "$#";
	}
	
	public static String sendRequest(int port, String host, int req, String payload) {
		try {
			Client client = new Client(port, host);
			client.sendMessage(JMProtocolImpl.craftMessage(req, payload == null ? "" : payload));
			String response = client.readMessage().toString();
			client.close();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
