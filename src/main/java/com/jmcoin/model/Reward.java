package com.jmcoin.model;

import com.jmcoin.network.MasterNode;

import java.util.Objects;

public class Reward extends Transaction{

	public static final int REWARD_START_VALUE = 10;
	public static final int REWARD_RATE = 100;
	private String miner;
	//TODO computed value ?
	private String amount = "10";


	public Reward(String miner) {
		this.miner = Objects.requireNonNull(miner);
		this.amount = MasterNode.getInstance().getRewardAmount() + "";
	}

	//TODO whot iz dis ?
	@Override
	public void addInputOutput(Input pInput, Output pOutput) {
		super.addInputOutput(pInput, pOutput);
	}

	public String getMiner() {
		return miner;
	}

	public String getAmount() {
		return amount;
	}
}
