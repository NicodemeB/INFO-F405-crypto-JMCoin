package com.jmcoin.network;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Mining;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class MinerJMProtocolImpl extends JMProtocolImpl<MinerNode>{
	
	private Client client;
	private Mining mining;
	
	public MinerJMProtocolImpl(MinerNode peer) throws IOException, NoSuchAlgorithmException {
		super(peer);
		this.mining = new Mining(this);
		this.client = new Client(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, this);
        new Thread(new ReceiverThread<Client>(this.client)).start();
        new Thread(this.client).start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public Mining getMiningInfos() throws IOException {
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_DIFFICULTY, null));
		while(this.mining.getDifficulty()== null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_REWARD_AMOUNT, null));
		while(this.mining.getRewardAmount() == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNVERIFIED_TRANSACTIONS, null));
		while(this.mining.getUnverifiedTransaction() == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return mining;
	}

	@Override
	protected String stopMining() {
		this.mining.stopMining();
		return null;
	}

	@Override
	//unused
	protected String giveMeBlockChainCopyImpl() {return null;}

	@Override
	//unused
	protected String giveMeRewardAmountImpl() {return null;}

	@Override
	//unused
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	//unused
	protected String takeMyMinedBlockImpl(String payload) {return null;}

	@Override
	//unused
	protected boolean takeMyNewTransactionImpl(String payload) {return false;}
	
	@Override
	protected String giveMeDifficulty() {return null;}

	@Override
	protected String giveMeUnspentOutputs() {return null;}

	@Override
	protected void receiveDifficulty(String string) {
		try{
			this.mining.setDifficulty(Integer.parseInt(string));
		}
		catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	@Override
	protected void receiveUnverifiedTransactions(String string) {
		this.mining.setUnverifiedTransaction(IOFileHandler.getFromJsonString(string, Transaction[].class));
	}

	@Override
	protected void receiveRewardAmount(String string) {
		try{
			this.mining.setRewardAmount(Integer.parseInt(string));
		}
		catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	@Override
	protected void receiveBlockchainCopy(String nextToken) {}

	@Override
	protected void receiveUnspentOutputs(String string) {
		this.mining.setUnspentOutputs(IOFileHandler.getFromJsonString(string, Output[].class));
	}
	
	public Chain getChain() throws IOException {
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null));
		Chain c = this.mining.getChain();
		while(c == null) {
			try {
				Thread.sleep(500);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			c = this.mining.getChain();
		}
		return c;
	}
	
	public boolean validateTransaction(Transaction trans, Chain chain) throws IOException {
		int total = 0;
		for(Input i : trans.getInputs()) {
			Transaction t  = chain.findInBlockChain(i.getPrevTransactionHash());
			Output output = null;
			if(t.getOutputOut().getAddress().equals(SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey())) && t.getOutputOut().getAmount() == i.getAmount()) {
				output = t.getOutputOut();
			}
			else if(t.getOutputBack().getAddress().equals(SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey())) && t.getOutputBack().getAmount() == i.getAmount()) {
				output = t.getOutputBack();
			}
			if(output == null) {
				return false;
			}
			this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNSPENT_OUTPUTS, null));
			Output[] unspentOutputs = this.mining.getUnspentOutputs(); 
			while(unspentOutputs == null) {
				try {
					Thread.sleep(500);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
				unspentOutputs = this.mining.getUnspentOutputs();
			}
			boolean unspent = false;
			for(Output uo : unspentOutputs) {
				if(uo.equals(output)) {
					unspent = true;
				}
			}
			if(!unspent) {
				return false;
			}
			//if i.output is not in unspent ouputs pool -> false
			//if i.output.address is not this.outputs[0].address -> false
			total += i.getAmount();
		}
		total -= trans.getOutputOut().getAmount();
		total -= trans.getOutputBack().getAmount();
		System.out.println("total = " + total);
		if(total != 0)
			return false;
		
		return true	;
	}
	
	public void sendMinedBlock(Block block) throws IOException {
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_MINED_BLOCK, IOFileHandler.toJson(block)));
	}

	@Override
	protected String giveMeLastBlock() {return null;}

	@Override
	protected void receiveLastBlock(String block) {}

	@Override
	protected void receiveTransactionToThisAddress(String trans) {}

	@Override
	protected String giveMeTransactionsToThisAddress(String address) {return null;}
}
