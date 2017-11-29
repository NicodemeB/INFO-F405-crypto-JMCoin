package com.jmcoin.network;

public class MinerJMProtocolImpl extends JMProtocolImpl<MinerNode>{

	public MinerJMProtocolImpl(MinerNode peer) {
		super(peer);
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
	protected String giveMeUnspentOutputs() {
		// TODO Auto-generated method stub
		return null;
	}
}
