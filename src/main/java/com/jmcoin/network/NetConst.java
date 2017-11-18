package com.jmcoin.network;

public abstract class NetConst {
	
	public static final int RELAY_NODE_LISTEN_PORT 					= 0xbabe;
	public static final char GIVE_ME_BLOCKCHAIN_COPY				= '0'; //from wallets
	public static final char GIVE_ME_UNVERIFIED_TRANSACTIONS		= '1'; //from miners
	public static final char GIVE_ME_REWARD_AMOUNT					= '2'; //from miners
	public static final char TAKE_MY_MINED_BLOCK					= '3'; //from miners
	public static final char TAKE_MY_NEW_TRANSACTION				= '4'; //from wallets
	public static final char DELIMITER								= '$';
	public static final char END									= '#';

}
