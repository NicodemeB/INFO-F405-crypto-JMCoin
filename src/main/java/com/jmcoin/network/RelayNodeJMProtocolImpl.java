package com.jmcoin.network;

import java.io.IOException;

/**
 * Class RelayNodeJMProtocolImpl
 * Implementation on the J-M protocol from the the {@link RelayNode}'s POV
 * @author enzo
 *
 */
public class RelayNodeJMProtocolImpl extends JMProtocolImpl<RelayNode> {
	
	private BroadcastingClient broadcastingClient;

    public ClientSC getClient() {
        return client;
    }

    public void setClient(ClientSC client) {
        this.client = client;
    }

    private ClientSC client;
	
	public RelayNodeJMProtocolImpl() throws IOException {
		super(new RelayNode());

	}


    @Override
    protected String AskDebug(Object payload) {
//        return (String) sendRequestAndGetAnswer(NetConst.ASK_DEBUG, payload.toString());

        sendRequest(NetConst.ASK_DEBUG, payload.toString());
        return null;

    }

    @Override
    protected String AnswerDebug(Object payload) {
        System.out.println("payload = " + payload);
        try {
            getClient().getServer().getAwaitingAnswers().firstElement().sendMessage(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getClient().getServer().getAwaitingAnswers().remove(getClient().getServer().getAwaitingAnswers().firstElement());
		return null;
    }

    @Override
    protected String SendBroacastDebug(){
	    sendRequest(NetConst.STOP_MINING,null);
	    return null;
    }


	@Override
	protected String StopMining(){
		// TODO SEND TO ALL CLIENT STOP MINING
		System.out.println("Thread #"+Thread.currentThread().getId() +"RECEIVE STOP MINING FROM MASTER -> SEND STOP MINING TO MINER");
//		notifyAll();
		return "LALA";
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		String blockchain = (String) sendRequestAndGetAnswer( NetConst.GIVE_ME_BLOCKCHAIN_COPY, null);
		this.peer.updateBlockChain(blockchain);
		return blockchain;
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		return (String) sendRequestAndGetAnswer( NetConst.GIVE_ME_REWARD_AMOUNT, null);
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {
		return (String) sendRequestAndGetAnswer( NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null);
	}

	@Override
	protected boolean takeMyMinedBlockImpl(String payload) {
		if(payload != null) {
            sendRequestAndGetAnswer( NetConst.TAKE_MY_MINED_BLOCK, payload);
			return true;
		}
		return false;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		if (payload != null) {
            sendRequestAndGetAnswer( NetConst.TAKE_MY_NEW_TRANSACTION, payload);
			return true;
		}
		return false;
	}

	@Override
	protected String giveMeDifficulty() {
		return (String) sendRequestAndGetAnswer( NetConst.GIVE_ME_DIFFICULTY, null);
	}

	@Override
	protected String giveMeUnspentOutputs() {
		return (String) sendRequestAndGetAnswer( NetConst.GIVE_ME_UNSPENT_OUTPUTS, null);
	}

	@Override
	protected void receiveByBroadcast(String received) {
		System.out.println("Relay node - received request type "+received+" by broadcast");
		//FIXME cannot retransmit to miners
		try {
			broadcastingClient.discoverServers(JMProtocolImpl.craftMessage(NetConst.SEND_BROADCAST, received));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void sendRequest(int req, String payload) {
        getClient().setToSend(JMProtocolImpl.craftMessage(req, payload == null ? "" : payload));
    }

    public synchronized Object sendRequestAndGetAnswer(int req, String payload) {
	    // FIXME - DO NOT USE : BULLSHIT
		    getClient().setToSend(JMProtocolImpl.craftMessage(req, payload == null ? "" : payload));
//			try {
//				getClient().getServer().getAwaitingAnswers().firstElement().sendMessage("TEST");
//				getClient().getServer().getAwaitingAnswers().remove(getClient().getServer().getAwaitingAnswers().firstElement());
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		return null;

	}
}
