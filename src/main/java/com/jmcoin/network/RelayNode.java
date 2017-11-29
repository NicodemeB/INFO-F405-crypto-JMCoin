package com.jmcoin.network;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Chain;

/**
 * Class RelayNode
 * Represents a peer allowing communication over the network
 * @author enzo
 */

public class RelayNode extends Peer{

	private Chain localChainCopy;

	public RelayNode() {
		super();
		String localChainCopy = JMProtocolImpl.sendRequest(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, NetConst.GIVE_ME_BLOCKCHAIN_COPY, null);
		this.localChainCopy = IOFileHandler.getFromJsonString(localChainCopy, Chain.class);
	}
	
	public Chain getLocalChainCopy() {
		return localChainCopy;
	}
	
	public void updateBlockChain(String bc) {
		this.localChainCopy = IOFileHandler.getFromJsonString(bc, Chain.class);
	}
}
