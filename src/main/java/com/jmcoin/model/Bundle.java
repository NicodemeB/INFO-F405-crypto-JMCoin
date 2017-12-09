package com.jmcoin.model;

public class Bundle<X>{
	
	private X object;

	public X getObject() {
		X x = object;
		this.object = null;
		return x;
	}

	public void setObject(X object) {
		this.object = object;
	}
}
