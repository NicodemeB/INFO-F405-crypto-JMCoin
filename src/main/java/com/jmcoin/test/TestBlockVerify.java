package com.jmcoin.test;

import com.google.gson.Gson;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class TestBlockVerify {
	
	public static void main(String[] args) {
		Input in = new Input();
		in.setHashSha256("h1");
		in.setSignature("sig1");
		Output out = new Output();
		out.setAmount(42);
		out.setInputIndex(0);
		out.setPublicKey("Pubkey");
		Transaction transaction = new Transaction();
		transaction.addInputOutput(in, out);
		System.out.println(new Gson().toJson(transaction));
	}
}
