package com.jmcoin.network;

/**
 * The abtract class NetConst
 * Defines some useful constants regarding to the J-M protocol
 * @author enzo
 */
public abstract class NetConst {
	
	public static final int RELAY_NODE_LISTEN_PORT 			= 0xbabe;
	public static final int MASTER_NODE_LISTEN_PORT			= 0xb00b;
	public static final int MINER_NODE_LISTEN_PORT			= 0xc001;
	public static final String MASTER_HOST_NAME				= "localhost"; //TODO remove this
	public static final String RELAY_DEBUG_HOST_NAME		= "localhost"; //TODO remove this
	//public static final String BROADCAST_HOST_NAME		= "255.255.255.255";
	public static final char GIVE_ME_BLOCKCHAIN_COPY		= '0'; //from wallets
	public static final char GIVE_ME_UNVERIFIED_TRANSACTIONS= '1'; //from miners
	public static final char GIVE_ME_REWARD_AMOUNT			= '2'; //from miners
	public static final char TAKE_MY_MINED_BLOCK			= '3'; //from miners
	public static final char TAKE_MY_NEW_TRANSACTION		= '4'; //from wallets
	public static final char TAKE_UPDATED_DIFFICULTY		= '5';
	public static final char DELIMITER						= '$';
	public static final char END							= '#';
	public static final String ERR_NOT_A_REQUEST			= "err_not_req";
	public static final String RES_OKAY						= "res_ok";
	public static final String RES_NOK						= "res_nok";
	public static final String ERR_BAD_REQUEST				= "bad_req";
}
