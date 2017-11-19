package com.jmcoin.network;

import java.util.StringTokenizer;

/**
 * 
 * @author enzo
 */
public abstract class JMProtocolImpl {
	
	protected Peer peer;
	
	public JMProtocolImpl(Peer peer) {
		this.peer = peer;
	}
	
	public String processInput(Object message) {
		//FIXME the content of this method depends on the way we exchange data. Please update
		//the following carefully.
		//At this time, we will assume that $message is a String
		String content = (String)message;
		/**
		 * Will assume that the payload is built as follows:
		 * x$yyyyyyyyyyyyy$
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
			default:
				break;
			}
		}
		return null;
	}
	
	protected abstract String giveMeBlockChainCopyImpl();
	protected abstract String giveMeRewardAmountImpl();
	protected abstract String giveMeUnverifiedTransactionsImpl();
	protected abstract void takeMyMinedBlockImpl(String payload);
	protected abstract void takeMyNewTransactionImpl(String payload);
	protected abstract void takeUpdatedDifficultyImpl(String payload);
	
	public static String craftMessage(int request, String body) {
		return request + "$" + body + "$#";
	}
	
	public static String craftMessage(int request) {
		return JMProtocolImpl.craftMessage(request, "");
	}
}
