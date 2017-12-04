package com.jmcoin.network;

import java.io.IOException;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.io.IOFileHandler;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class MinerJMProtocolImpl extends JMProtocolImpl<MinerNode>{

	private Client client;
	
	public MinerJMProtocolImpl(MinerNode peer) throws IOException {
		super(peer);
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

	/*@Override
	protected String AskDebug(Object payload) {
		return null;
	}
	

	@Override
	protected String AnswerDebug(Object payload) {
		return null;
	}



//	@Override
//	protected String BroacastDebug() {
//		return null;
//	}

	@Override
	protected String SendBroacastDebug() {
		return null;
	}*/

	@Override
	protected String StopMining() {
		this.peer.getMining().stopMining();
		return NetConst.RES_OKAY;
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

	public static String sendRequest(int relayNodeListenPort, String relayDebugHostName, char takeMyMinedBlock, String s) {
		//FIXME
		return null;
	}

	@Override
	protected void receiveDifficulty(String string) {
		try{
			this.peer.getMining().setDifficulty(Integer.parseInt(string));
		}
		catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	@Override
	protected void receiveUnverifiedTransactions(String string) {
		this.peer.getMining().setUnverifiedTransaction(IOFileHandler.getFromJsonString(string, Transaction[].class));
	}

	@Override
	protected void receiveRewardAmount(String string) {
		try{
			this.peer.getMining().setRewardAmount(Integer.parseInt(string));
		}
		catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	@Override
	protected void receiveBlockchainCopy(String nextToken) {}

	@Override
	protected void receiveUnspentOutputs(String string) {
		this.peer.getMining().setUnspentOutputs(IOFileHandler.getFromJsonString(string, Output[].class));
	}
	public Chain getChain() throws IOException {
		this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_BLOCKCHAIN_COPY, null));
		Chain c = this.peer.getMining().getChain();
		while(c == null) {
			try {
				Thread.sleep(500);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			c = this.peer.getMining().getChain();
		}
		return c;
	}
	public boolean validateTransaction(Transaction trans, Chain chain) throws IOException {
		int total = 0;
		for(Input i : trans.getInputs()) {
			Transaction t  = chain.findInBlockChain(i.getPrevTransactionHash());
			Output output = null;
			if(t.getOutputOut().getAddress() == SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey()) && t.getOutputOut().getAmount() == i.getAmount()) {
				output = t.getOutputOut();
			}
			else if(t.getOutputBack().getAddress() == SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey()) && t.getOutputBack().getAmount() == i.getAmount()) {
				output = t.getOutputBack();
			}
			if(output == null) {
				return false;
			}
			this.client.sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_UNSPENT_OUTPUTS, null));
			//String unvf = "";//JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_UNSPENT_OUTPUTS, null);
			//Output[] unspentOutputs = IOFileHandler.getFromJsonString(unvf, Output[].class);
			Output[] unspentOutputs = this.peer.getMining().getUnspentOutputs(); 
			while(unspentOutputs == null) {
				try {
					Thread.sleep(500);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
				unspentOutputs = this.peer.getMining().getUnspentOutputs();
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
}
