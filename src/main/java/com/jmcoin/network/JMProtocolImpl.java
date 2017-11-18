package com.jmcoin.network;

import java.util.StringTokenizer;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Transaction;

public class JMProtocolImpl {

	public JMProtocolImpl() {
		
	}
	
	public String processInput(Object message) {
		//FIXME the content of this method depends on the way we exchange data. Please update
		// the following carefully.
		//At this time, we will assume that $message is a String
		String content = (String)message;
		/**
		 * Will assume that the payload is built as follows:
		 * x$yyyyyyyyyyyyy$
		 * where x is the type
		 * and y the payload itself (can be empty, like 0$$
		 */
		StringTokenizer tokenizer = new StringTokenizer(content, String.valueOf(NetConst.DELIMITER));
		if(!tokenizer.hasMoreTokens()) return null;
		String type = tokenizer.nextToken();
		if(type != null && type.length() == 1) {
			if(!tokenizer.hasMoreTokens()) return null;
			String payload = tokenizer.nextToken();
			System.out.println(payload);
			switch (type.charAt(0)) {
			case NetConst.GIVE_ME_BLOCKCHAIN_COPY:
				//no meaningful payload
				break;
			case NetConst.GIVE_ME_REWARD_AMOUNT:
				//no meaningful payload
				break;
			case NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS:
				//no meaningful payload
				break;
			case NetConst.TAKE_MY_MINED_BLOCK:
				Block block = IOFileHandler.getFromJsonString(payload, Block.class);
				//TODO deal with this shit
				break;
			case NetConst.TAKE_MY_NEW_TRANSACTION:
				Transaction transaction = IOFileHandler.getFromJsonString(payload, Transaction.class);
				//TODO deal with this shit
				break;
			default:
				break;
			}
		}
		return null;
	}
}
