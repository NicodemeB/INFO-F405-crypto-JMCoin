package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.jmcoin.model.Mining;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

public class TestMining {

	public static void main(String[] args) {
		Mining mining = new Mining();
		try {
			mining.buildBlock();
			System.out.println(mining.mine());
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.TAKE_MY_MINED_BLOCK, new Gson().toJson(mining.getBlock()));
	}
}
