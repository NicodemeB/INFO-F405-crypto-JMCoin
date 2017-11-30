package com.jmcoin.network;

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
	protected String giveMeBlockChainCopyImpl() {
		String blockchain = sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_BLOCKCHAIN_COPY, null);
		this.peer.updateBlockChain(blockchain);
		return blockchain;
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		return sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_REWARD_AMOUNT, null);
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {
		return JMProtocolImpl.sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null);
	}

	@Override
	protected boolean takeMyMinedBlockImpl(String payload) {
		if(payload != null) {
			JMProtocolImpl.sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.TAKE_MY_MINED_BLOCK, payload);
			return true;
		}
		return false;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		if (payload != null) {
			JMProtocolImpl.sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.TAKE_MY_NEW_TRANSACTION, payload);
			return true;
		}
		return false;
	}

	@Override
	protected String giveMeDifficulty() {
		return JMProtocolImpl.sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_DIFFICULTY, null);
	}

	@Override
	protected String giveMeUnspentOutputs() {
		return JMProtocolImpl.sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_UNSPENT_OUTPUTS, null);
	}

}
