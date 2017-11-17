package com.jmcoin.util;

import java.io.UnsupportedEncodingException;

public abstract class StringUtils {
	
	public static byte[] convertToBytesArray(String text) {
		if (text == null) return null;
		try {
			return text.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String convertFromBytesArray(byte[] bytes) {
		return new String(bytes);
	}
}
