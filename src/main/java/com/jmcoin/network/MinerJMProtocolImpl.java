package com.jmcoin.network;

public class MinerJMProtocolImpl extends JMProtocolImpl<MinerNode>{

	public MinerJMProtocolImpl(MinerNode peer) {
		super(peer);
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		return null;
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		return null;
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {
		return null;
	}

	@Override
	protected boolean takeMyMinedBlockImpl(String payload) {
		return false;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {
		return false;
	}

	@Override
	protected boolean takeUpdatedDifficultyImpl(String payload) {
		return false;
	}
	//TODO Reward Transaction

}
