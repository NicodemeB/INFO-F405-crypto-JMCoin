package com.jmcoin.network;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import com.jmcoin.crypto.SignaturesVerification;
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
	
	public Client getClient() {
		return client;
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
		setBundle(string , Integer.class);
	}

	@Override
	protected void receiveUnverifiedTransactions(String string) {
		setBundle(string , Transaction[].class);
	}

	@Override
	protected void receiveRewardAmount(String string) {
		setBundle(string , Integer.class);
	}

	@Override
	protected void receiveBlockchainCopy(String string) {
		setBundle(string , Chain.class);
	}

	@Override
	protected void receiveUnspentOutputs(String string) {
		setBundle(string , Output[].class);
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
			Output[] unspentOutputs = downloadObject(Output[].class, NetConst.GIVE_ME_UNSPENT_OUTPUTS, null, client);
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
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.TAKE_MY_MINED_BLOCK, this.peer.getGson().toJson(block)));
	}

	@Override
	protected String giveMeLastBlock() {return null;}

	@Override
	protected void receiveLastBlock(String block) {
		setBundle(block, Block.class);
	}

	@Override
	protected void receiveTransactionToThisAddress(String trans) {}

	@Override
	protected String giveMeTransactionsToThisAddress(String address) {return null;}
}
