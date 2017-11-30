package com.jmcoin.model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import com.jmcoin.io.IOFileHandler;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

/**
 * Class Input.
 * Represents an input in a {@link Transaction}
 * @author enzo
 *
 */
public class Input implements Serializable{
	
    private static final long serialVersionUID = -7496600791646424812L;
    public static final int INDEX_REWARD = -1;	
    public String address;
    private double amount;	//to delete?? how do we know the value then?
    private byte[] prevTransactionHash;
    private Long id;
    
    public Input(){};
    public Input(String adr, double amount, byte[] prevTrans)
    {
        this.address = adr;
        this.amount = amount;
        this.prevTransactionHash = prevTrans;
    }
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
	
	public boolean equals(Input pInput) {
		return this.amount == pInput.amount  && Arrays.equals(this.prevTransactionHash, pInput.prevTransactionHash);
	}
	
	public int getSize() {
		return  4 + (this.prevTransactionHash == null ? 0 : this.prevTransactionHash.length);
	}
	
	public Transaction getTransaction() {
		String chainJson = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_BLOCKCHAIN_COPY, null);
		Chain chain = IOFileHandler.getFromJsonString(chainJson, Chain.class);
		this.getPrevTransactionHash();
		Map<String, Block> blocks = chain.getBlocks();
		for(String key : blocks.keySet()) {
			Block b = blocks.get(key);
			for(Transaction t : b.getTransactions()) {
				if(Arrays.equals(t.getHash(), this.getPrevTransactionHash())) {
					return t;
				}
			}
		}
		return null;
	}
	
	public byte[] getBytes() {
		ByteBuffer bf = ByteBuffer.allocate(getSize());
		bf.putDouble(this.amount);
		if(this.prevTransactionHash != null)bf.put(this.prevTransactionHash);
		return bf.array();
	}

	public byte[] getPrevTransactionHash() {
		return prevTransactionHash;
	}

	public void setPrevTransactionHash(byte[] prevTransactionHash) {
		this.prevTransactionHash = prevTransactionHash;
	}
}
