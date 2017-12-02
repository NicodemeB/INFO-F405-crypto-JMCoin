package com.jmcoin.network;

import java.io.IOException;

public class MinerJMProtocolImpl extends JMProtocolImpl<MinerNode>{

	private BroadcastingEchoServer broadcastingEchoServer;
	
	public MinerJMProtocolImpl(MinerNode peer) {
		super(peer);
		try {
			this.broadcastingEchoServer = new BroadcastingEchoServer(this);
			this.broadcastingEchoServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String BroacastDebug() {
		return null;
	}

	@Override
	protected String SendBroacastDebug() {
		return null;
	}

	@Override
	protected String StopMining() {
		return null;
	}

	@Override
	//unused
	protected String giveMeBlockChainCopyImpl() {return null;}

	@Override
	//unused
	protected String giveMeRewardAmountImpl() {return null;}

	@Override
	//unused
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	//unused
	protected boolean takeMyMinedBlockImpl(String payload) {return false;}

	@Override
	//unused
	protected boolean takeMyNewTransactionImpl(String payload) {return false;}
	
	@Override
	protected String giveMeDifficulty() {return null;}

	@Override
	protected String giveMeUnspentOutputs() {return null;}

	@Override
	protected void receiveByBroadcast(String received) {
		System.out.println("Miner node - received something by broadcast");
		if(received.equals(Character.toString(NetConst.STOP_MINING))){
			this.peer.stopMining();
		}
	}
	public static String sendRequest(int relayNodeListenPort, String relayDebugHostName, char takeMyMinedBlock, String s) {
		//FIXME
		return null;
	}
}
