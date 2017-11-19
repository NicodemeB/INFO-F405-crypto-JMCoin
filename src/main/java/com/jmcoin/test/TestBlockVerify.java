package com.jmcoin.test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

import com.jmcoin.model.Block;

public class TestBlockVerify {
	
	public static void main(String[] args) {
		Block bl = new Block();
		bl.setDifficulty(20);
		MessageDigest dig = null;
		try {
			dig = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if(dig == null)return;
		for(int i = 0; i < 10000000; i++) {
			bl.setFinalHash(Hex.toHexString(dig.digest(Integer.toString(i).getBytes())));
			if(bl.verifyHash()) {
				System.out.println(bl.getFinalHash());
				break;
			}
		}
	}
}
