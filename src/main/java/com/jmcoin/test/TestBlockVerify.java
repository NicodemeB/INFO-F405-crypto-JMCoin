package com.jmcoin.test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;

import com.google.gson.Gson;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Mine;
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
		Block block = new Block();
		block.setDifficulty(12);
		block.getTransactions().add(transaction);
		try {
			block.setFinalHash("11118d9c2e115fe68e978bedf114d92dad7cb35fbbcc85bae20c7e38ecc5860f");
			Chain chain = new Chain();
			System.out.println("Can be added to chain: "+chain.isFinalHashRight(block));
			Mine mine = new Mine(block);
			System.out.println(mine.proofOfWork());
			System.out.println("Can be added to chain: "+chain.isFinalHashRight(block));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
