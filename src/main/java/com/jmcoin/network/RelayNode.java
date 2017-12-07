package com.jmcoin.network;

import java.io.IOException;

import com.jmcoin.model.Chain;

/**
 * Class RelayNode
 * Represents a peer allowing communication over the network
 * @author enzo
 */

public class RelayNode extends Peer{

	private Chain localChainCopy;

	public RelayNode() throws IOException {
		super();
	}
	
	public Chain getLocalChainCopy() {
		return this.localChainCopy;
	}
	
	public void updateBlockChain(String bc) {
		this.localChainCopy = this.gson.fromJson(bc, Chain.class);
	}
}
