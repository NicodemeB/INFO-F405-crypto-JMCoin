package com.jmcoin.network;

import com.google.gson.JsonSyntaxException;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;

public class MasterJMProtocolImpl extends JMProtocolImpl<MasterNode>{

	public MasterJMProtocolImpl() {
		super(MasterNode.getInstance());
	}

	@Override
	protected String giveMeBlockChainCopyImpl() {
		return peer.getBlockChain();
	}

	@Override
	protected String giveMeRewardAmountImpl() {
		return Integer.toString(peer.getRewardAmount());
	}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	protected boolean takeMyMinedBlockImpl(String payload) {
		if (payload != null) {
			try {
				Block block = IOFileHandler.getFromJsonString(payload, Block.class);
				peer.processBlock(block);
			}
			catch(JsonSyntaxException jse) {
				jse.printStackTrace();
				return false;
			}
			return true;
			
		}
		return false;
	}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {return false;}

	@Override
	protected boolean takeUpdatedDifficultyImpl(String payload) {return false;}
}
