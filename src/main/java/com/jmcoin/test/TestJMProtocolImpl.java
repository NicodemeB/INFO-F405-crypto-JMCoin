package com.jmcoin.test;


import com.google.gson.Gson;
import com.jmcoin.model.Block;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

public class TestJMProtocolImpl {
	public static void main(String[] args) {
		JMProtocolImpl proto = new JMProtocolImpl();
		Block block = new Block();
		block.setDifficulty(41);
		block.setFinalHash("H");
		block.setSize(0xdead);
		block.setTimeCreation(System.currentTimeMillis());
		Gson gson = new Gson();
		proto.processInput(NetConst.TAKE_MY_MINED_BLOCK+"$"+gson.toJson(block)+"$");
	}
}
