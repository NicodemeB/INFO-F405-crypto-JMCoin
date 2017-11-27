package com.jmcoin.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import com.jmcoin.model.Block;
import com.jmcoin.model.Input;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class TestMining {

	public static void main(String[] args) {
		/*Input in = new Input();
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
		}*/
		Mining mining = new Mining();
		try {
			mining.buildBlock();
			System.out.println(mining.mine());
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
