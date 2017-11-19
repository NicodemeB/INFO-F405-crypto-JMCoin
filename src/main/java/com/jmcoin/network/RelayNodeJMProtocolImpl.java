package com.jmcoin.network;

import java.io.IOException;

public class RelayNodeJMProtocolImpl extends JMProtocolImpl {

	public RelayNodeJMProtocolImpl() {
		super(new RelayNode());
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		System.out.println("Relay: you request has been forwarded to MasterNode");
		try {
			Client client = new Client(NetConst.MASTER_NODE_LISTEN_PORT, "localhost");
			client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY));
			String response = client.readMessage().toString();
			System.out.println("Master gave me its answer");
			client.close();
			return response;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected String giveMeRewardAmountImpl() {return null;}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	protected void takeMyMinedBlockImpl(String payload) {}

	@Override
	protected void takeMyNewTransactionImpl(String payload) {}

	@Override
	protected void takeUpdatedDifficultyImpl(String payload) {}

}
