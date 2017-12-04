package com.jmcoin.network;

import java.io.IOException;

import com.jmcoin.io.IOFileHandler;
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
	protected void receiveUnspentOutputs(String string) {}
}
