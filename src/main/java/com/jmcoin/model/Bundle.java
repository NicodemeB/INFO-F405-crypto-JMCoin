package com.jmcoin.model;

import java.lang.reflect.Type;

public class Bundle<X>{
	
	private X object;

	public X getObject(Type type) {
		X x = object;
		this.object = null;
		return x;
	}

	public void setObject(X object) {
		this.object = object;
	}
}
