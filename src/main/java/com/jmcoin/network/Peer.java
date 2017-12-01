package com.jmcoin.network;

public abstract class Peer {
	
	protected int portBroadcast;
	
	public Peer() {}
	
	public int getPortBroadcast() {
		return portBroadcast;
	}	
}
