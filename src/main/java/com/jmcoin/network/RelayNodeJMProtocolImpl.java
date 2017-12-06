package com.jmcoin.network;

import java.io.IOException;

/**
 * Class RelayNodeJMProtocolImpl
 * Implementation on the J-M protocol from the the {@link RelayNode}'s POV
 * @author enzo
 *
 */
public class RelayNodeJMProtocolImpl extends JMProtocolImpl<RelayNode> {

    public void setClient(ClientSC client) {
        this.client = client;
    }
    
    private ClientSC client;
	
	public RelayNodeJMProtocolImpl(RelayNode relay) throws IOException {
		super(relay);
	}
    
    private void receiveData(String payload) {
        try {
            this.client.getServer().getAwaitingAnswers().firstElement().sendMessage(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.client.getServer().getAwaitingAnswers().remove(this.client.getServer().getAwaitingAnswers().firstElement());
    }
    
    @Override
	protected void receiveUnspentOutputs(String string) {
		receiveData(string);
	}
    
	@Override
	protected void receiveUnverifiedTransactions(String string) {
		receiveData(string);
	}

	@Override
	protected void receiveRewardAmount(String string) {
		receiveData(string);
	}

	@Override
	protected void receiveDifficulty(String payload) {
		receiveData(payload);
	}
	
	@Override
	protected void receiveBlockchainCopy(String string) {
		this.peer.updateBlockChain(string);
		receiveData(string);
	}

	@Override
	protected String stopMining(){
		return JMProtocolImpl.craftMessage(NetConst.STOP_MINING, null);
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null));
		return null;
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_REWARD_AMOUNT, null));
		return null;
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null));
		return null;
	}

	@Override
	protected String takeMyMinedBlockImpl(String payload) {
		if(payload != null) {
			this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_MINED_BLOCK, payload));
		}
		return null;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		if (payload != null) {
			this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_NEW_TRANSACTION, payload));
			return true;
		}
		return false;
	}

	@Override
	protected String giveMeDifficulty() {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_DIFFICULTY, null));
		return null;
	}

	@Override
	protected String giveMeUnspentOutputs() {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNSPENT_OUTPUTS, null));
		return null;
	}

	@Override
	protected String giveMeLastBlock() {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_LAST_BLOCK, null));
		return null;
	}

	@Override
	protected void receiveLastBlock(String block) {
		receiveData(block);
	}

	@Override
	protected void receiveTransactionToThisAddress(String trans) {
		receiveData(trans);
	}

	@Override
	protected String giveMeTransactionsToThisAddress(String address) {
		this.client.setToSend(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_TRANS_TO_THIS_ADDRESS, address));
		return null;
	}
}
