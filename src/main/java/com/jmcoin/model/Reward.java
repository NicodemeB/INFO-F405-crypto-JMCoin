package com.jmcoin.model;

import com.jmcoin.network.MasterNode;

import java.util.Objects;

public class Reward extends Transaction{

	private static final long serialVersionUID = 1L;
	public static final int REWARD_START_VALUE = 10;
	public static final int REWARD_RATE = 100;
	private String miner;
	private int amount;


	public Reward(String miner) {
		this.miner = Objects.requireNonNull(miner);
		this.amount = 0; //TODO remove this. Should ask via relay node MasterNode.getInstance().getRewardAmount();
	}

	public String getMiner() {
		return miner;
	}

	public int getAmount() {
		return amount;
	}
}
