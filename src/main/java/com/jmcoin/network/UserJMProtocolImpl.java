package com.jmcoin.network;

import java.io.IOException;
import java.util.Map;

import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Transaction;

public class UserJMProtocolImpl extends JMProtocolImpl<UserNode>{

	private Client client;
	
	public UserJMProtocolImpl(UserNode peer) throws IOException {
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

	@Override
	protected void receiveUnspentOutputs(String string) {
		setBundle(string, Map.class);		
	}

	@Override
	protected void receiveBlockchainCopy(String string) {
		setBundle(string, Chain.class);
	}

	@Override
	protected void receiveUnverifiedTransactions(String string) {}

	@Override
	protected void receiveRewardAmount(String string) {}

	@Override
	protected void receiveDifficulty(String string) {}

	@Override
	protected String stopMining() {return null;}

	@Override
	protected String giveMeUnspentOutputs() {return null;}

	@Override
	protected String giveMeBlockChainCopyImpl() {return null;}

	@Override
	protected String giveMeRewardAmountImpl() {return null;}

	@Override
	protected String giveMeUnverifiedTransactionsImpl() {return null;}

	@Override
	protected String takeMyMinedBlockImpl(String payload) throws IOException {return null;}

	@Override
	protected boolean takeMyNewTransactionImpl(String payload) {return false;}

	@Override
	protected String giveMeDifficulty() {return null;}

	@Override
	protected String giveMeLastBlock() {return null;}

	@Override
	protected void receiveLastBlock(String block) {
		setBundle(block, Block.class);
	}

	@Override
	protected void receiveTransactionToThisAddress(String trans) {
		setBundle(trans, Transaction[].class);
	}

	@Override
	protected String giveMeTransactionsToThisAddress(String address) {return null;}
	
	public double getAddressBalance(String ... addresses) throws IOException{
    	Transaction[] transactions = downloadObject(Transaction[].class, NetConst.GIVE_ME_TRANS_TO_THIS_ADDRESS, this.peer.getGson().toJson(addresses), getClient());
        double totalOutputAmount = 0;
        for(int j = 0; j < addresses.length; j++) {
        	for(int i = 0 ; i < transactions.length; i++){
                if(transactions[i].getOutputBack().getAddress().equals(addresses[j])){
                    totalOutputAmount+= transactions[i].getOutputBack().getAmount();
                }
                else if(transactions[i].getOutputOut().getAddress().equals(addresses[j])){
                	totalOutputAmount+= transactions[i].getOutputOut().getAmount();
                }
                else{
                	System.out.println("Wallet : No output belonging to this address");
                }
            }
        }
        return totalOutputAmount;
    }
}
