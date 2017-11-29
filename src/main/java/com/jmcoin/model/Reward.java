package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Reward extends Transaction implements Serializable{

	private static final long serialVersionUID = 1L;
	@Transient
	public static final int REWARD_START_VALUE = 10;
	@Transient
	public static final int REWARD_RATE = 100;
	@Basic(optional = false)
	private int amount;

	public Reward(){
		//DO NOT DELETE : Used by EclipseLink internal cooking
	}

	public int getAmount() {
		return amount;
	}
}
