package com.jmcoin.network;

import java.io.IOException;

import com.jmcoin.io.IOFileHandler;
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
		this.localChainCopy = IOFileHandler.getFromJsonString(bc, Chain.class);
	}
}
