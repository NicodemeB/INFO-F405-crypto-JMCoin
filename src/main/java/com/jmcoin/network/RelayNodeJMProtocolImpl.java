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
	
	public RelayNodeJMProtocolImpl() throws IOException {
		super(new RelayNode());
		new BroadcastingEchoServer(this).start();
		try {
			broadcastingClient = new BroadcastingClient(NetConst.DEFAULT_BROADCAST_SERVER_COUNT, NetConst.MINER_BROADCAST_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
