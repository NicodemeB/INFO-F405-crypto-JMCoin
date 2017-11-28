package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Reward extends Transaction implements Serializable{

	private static final long serialVersionUID = 1L;
	@Transient
	public static final int REWARD_START_VALUE = 10;
	@Transient
	public static final int REWARD_RATE = 100;
	@Basic(optional = false)
	private String miner;
	@Basic(optional = false)
	private int amount;


	public Reward(String miner) {
		this.miner = Objects.requireNonNull(miner);
		this.amount = 0; //TODO remove this. Should ask via relay node MasterNode.getInstance().getRewardAmount();
	}

	private Reward(){
		//DO NOT DELETE : Used by EclipseLink internal cooking
	}

	public String getMiner() {
		return miner;
	}

	public int getAmount() {
		return amount;
	}
}
