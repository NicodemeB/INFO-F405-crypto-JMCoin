package com.jmcoin.network;

import java.io.IOException;
import java.util.Map;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;

public class UserJMProtocolImpl extends JMProtocolImpl<UserNode>{

	private Client client;
	
	public UserJMProtocolImpl(UserNode peer) throws IOException {
		super(peer);
		this.client = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, this);
        new Thread(new ReceiverThread<Client>(this.client)).start();
        new Thread(this.client).start();
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public Client getClient() {
		return client;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void receiveUnspentOutputs(String string) {
		this.peer.setUnspentOutputs(IOFileHandler.getFromJsonString(string, Map.class));
	}

	@Override
	protected void receiveBlockchainCopy(String string) {
		this.peer.setBlockchainCopy(IOFileHandler.getFromJsonString(string, Chain.class));
	}

	@Override
	protected void receiveUnverifiedTransactions(String string) {}

	@Override
	protected void receiveRewardAmount(String string) {}

	@Override
	protected void receiveDifficulty(String string) {}

	@Override
	protected String stopMining() {return null;}

	@Override
	protected String giveMeUnspentOutputs() {return null;}

	@Override
	protected String giveMeBlockChainCopyImpl() {return null;}

	@Override
	protected String giveMeRewardAmountImpl() {return null;}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	protected String takeMyMinedBlockImpl(String payload) throws IOException {return null;}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {return false;}

	@Override
	protected String giveMeDifficulty() {return null;}

	@Override
	protected String giveMeLastBlock() {return null;}

	@Override
	protected void receiveLastBlock(String block) {
		this.peer.setLastBlock(IOFileHandler.getFromJsonString(block, Block.class));
	}
	
}
