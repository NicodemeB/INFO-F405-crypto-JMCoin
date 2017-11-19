package com.jmcoin.network;

public class MasterJMProtocolImpl extends JMProtocolImpl{

	public MasterJMProtocolImpl() {
		super(MasterNode.getInstance());
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		return ((MasterNode)peer).getBlockChain();
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		return Integer.toString(50); //FIXME remove arbitrary value
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	protected void takeMyMinedBlockImpl(String payload) {}

	@Override
	protected void takeMyNewTransactionImpl(String payload) {}

	@Override
	protected void takeUpdatedDifficultyImpl(String payload) {}
	
	

}
