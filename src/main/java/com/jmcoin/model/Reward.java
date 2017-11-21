package com.jmcoin.model;

import java.util.Objects;

public class Reward extends Transaction{

	private String miner;
	//TODO computed value ?
	private String amount = "10";


	public Reward(String miner, String amount) {
		this.miner = Objects.requireNonNull(miner);
		this.amount = Objects.requireNonNull(amount);
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
