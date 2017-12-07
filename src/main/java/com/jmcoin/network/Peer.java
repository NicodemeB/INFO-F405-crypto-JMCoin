package com.jmcoin.network;

import com.google.gson.Gson;
import com.jmcoin.model.Bundle;

public abstract class Peer {
		
	protected Bundle<? extends Object> bundle;
	protected Gson gson;
	
	public Peer() {
		this.bundle = new Bundle<>();
		this.gson = new Gson();
	}
	
	public Gson getGson() {
		return gson;
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
