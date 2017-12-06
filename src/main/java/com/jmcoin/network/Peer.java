package com.jmcoin.network;

import com.jmcoin.model.Bundle;

public abstract class Peer {
		
	protected Bundle<? extends Object> bundle;
	
	public Peer() {
		this.bundle = new Bundle<>();
	}
	
	public Bundle<? extends Object> getBundle() {
		return bundle;
	}
	
	protected <T> Bundle<T> createBundle(Class<T> type) {
		Bundle<T> bundle = new Bundle<>();
		setBundle(bundle);
		return bundle;
	}
	
	protected void setBundle(Bundle<? extends Object> bundle) {
		this.bundle = bundle;
	}
}
