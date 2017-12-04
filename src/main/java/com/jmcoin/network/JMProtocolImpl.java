package com.jmcoin.network;

import java.io.IOException;
import java.util.StringTokenizer;

import com.jmcoin.model.Block;
import com.jmcoin.model.Output;
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
	
	public int getPortBroadcast() {
		return this.peer.getPortBroadcast();
	}

	/**
	 * Will assume that the payload is built as follows:
	 * x$yyyyyyyyyyyyy$#
	 * where x is the type
	 * and y the payload itself (can be empty, like 0$$#)
	 * @throws IOException 
	 */
	public String processInput(Object message) {
	    System.out.println(this.peer.getClass().getSimpleName() + " is processing input: " +message.toString());
		String content = (String)message;
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
				try {
					return takeMyMinedBlockImpl(tokenizer.nextToken());
				} catch (IOException e) {
					e.printStackTrace();
				}
				return NetConst.RES_NOK;
			case NetConst.TAKE_MY_NEW_TRANSACTION:
				return takeMyNewTransactionImpl(tokenizer.nextToken()) ? NetConst.RES_OKAY : NetConst.RES_NOK;
			case NetConst.GIVE_ME_UNSPENT_OUTPUTS:
				return giveMeUnspentOutputs();
			case NetConst.GIVE_ME_DIFFICULTY:
				return giveMeDifficulty();
            /*case NetConst.SEND_BROADCAST_DEBUG :
                System.out.println("INTO JMPROTO SEND_BROADCAST_DEBUG");
                return SendBroacastDebug();*/
            case NetConst.RECEIVE_DIFFICULTY:
            	receiveDifficulty(tokenizer.nextToken());
            	return NetConst.RES_OKAY;
            case NetConst.RECEIVE_REWARD_AMOUNT:
            	receiveRewardAmount(tokenizer.nextToken());
            	return NetConst.RES_OKAY;
            case NetConst.RECEIVE_UNVERIFIED_TRANS:
            	receiveUnverifiedTransactions(tokenizer.nextToken());
            	return NetConst.RES_OKAY;
            case NetConst.RECEIVE_BLOCKCHAIN_COPY:
            	receiveBlockchainCopy(tokenizer.nextToken());
            	return NetConst.RES_OKAY;
            case NetConst.RECEIVE_UNSPENT_OUTPUTS:
            	receiveUnspentOutputs(tokenizer.nextToken());
            	return NetConst.RES_OKAY;
            /*case NetConst.ASK_DEBUG:
                return AskDebug(tokenizer.nextToken());
            case NetConst.ANSWER_DEBUG:
                return AnswerDebug(tokenizer.nextToken());*/
            case NetConst.STOP_MINING:
                return StopMining();
			default:
				return NetConst.ERR_NOT_A_REQUEST;
			}
		}
		return NetConst.ERR_BAD_REQUEST;
	}

    protected abstract void receiveUnspentOutputs(String string);
	protected abstract void receiveBlockchainCopy(String nextToken);
	protected abstract void receiveUnverifiedTransactions(String string);
	protected abstract void receiveRewardAmount(String string);
	protected abstract void receiveDifficulty(String string);

	/*protected abstract String AskDebug(Object payload);
    protected abstract String AnswerDebug(Object payload);
    protected abstract String SendBroacastDebug();*/
    protected abstract String StopMining();
	
	/**
	 * Returns unspent {@link Output} as a list
	 * @return
	 */
	protected abstract String giveMeUnspentOutputs();

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
	 * @throws IOException 
	 */
	protected abstract String takeMyMinedBlockImpl(String payload) throws IOException;
	
	/**
	 * Gets a new {@link Transaction}, and returns false only if the body cannot be parsed
	 * Getting a "true" doesn't mean that the {@link Transaction} is valid regarding to the protocol
	 * @param payload {@link Transaction} to parse
	 * @return true if the {@link Transaction} has been received properly
	 */
	protected abstract boolean takeMyNewTransactionImpl(String payload);	
	
	/**
	 * Returns difficulty on demand
	 * @return
	 */
	
	protected abstract String giveMeDifficulty();
	/**
	 * Builds a message to send over the network, compliant with the protocol
	 * @param request {@link NetConst}.xxxxx
	 * @param body JSON object if needed
	 * @return message
	 */
	public static String craftMessage(int request, String body) {
		return request + "$" + body + "$#";
	}
	
	public static String craftMessage(int request, char body) {
		return request + "$" + body + "$#"+'\0';
	}

    /*public static String sendRequest(int relayNodeListenPort, String relayDebugHostName, char takeMyMinedBlock, String s) {
        //FIXME
        return null;
    }*/

//	public static String sendRequest(int port, String host, int req, String payload) {
//		try {
//			RequestSender client = new RequestSender(port, host);
//			client.sendMessage(JMProtocolImpl.craftMessage(req, payload == null ? "" : payload));
//			String response = client.readMessage().toString();
//			client.close();
//			return response;
//		} catch (IOException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
}
