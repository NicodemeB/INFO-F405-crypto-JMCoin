package com.jmcoin.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

public class UserNode extends Peer{

	private Wallet wallet;
	private Chain chain;
	private Map<String, Output> unspentOutputs;
	private Block lastBlock;
	private ArrayList<Transaction> transToMe;
	
	public UserNode(String email) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		this.wallet = new Wallet(email);
	}
	
    public Transaction createTransaction(UserJMProtocolImpl protocol, String fromAddress, String toAddress, double amountToSend, PrivateKey privKey, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, FileNotFoundException, SignatureException{
        protocol.getClient().sendMessage(JMProtocolImpl.craftMessage(NetConst.GIVE_ME_TRANS_TO_THIS_ADDRESS, fromAddress));
        while(this.transToMe == null) {
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        ArrayList<Transaction> addressTransactions = this.transToMe;
        Transaction tr = new Transaction();
        double totalOutputAmount = 0;
        for(int i = 0 ; i < addressTransactions.size(); i++){
            if(addressTransactions.get(i).getOutputBack().getAddress().equals(fromAddress)){
                totalOutputAmount+= addressTransactions.get(i).getOutputBack().getAmount();
                tr.addInput(new Input(fromAddress,addressTransactions.get(i).getOutputBack().getAmount(),addressTransactions.get(i).getHash()));
            }
            else if((addressTransactions.get(i).getOutputOut().getAddress().equals(fromAddress))){
            	totalOutputAmount+= addressTransactions.get(i).getOutputOut().getAmount();
            	tr.addInput(new Input(fromAddress,addressTransactions.get(i).getOutputOut().getAmount(),addressTransactions.get(i).getHash()));
            }
            else{
            	System.out.println("Wallet : No output belonging to this address");
            }           
        }
        if(amountToSend <= totalOutputAmount){
            Output oOut = new Output(amountToSend, toAddress);
            Output oBack = new Output(totalOutputAmount-amountToSend, fromAddress);
            tr.setSignature(SignaturesVerification.signTransaction(tr.getBytes(false), privKey));
            tr.computeHash();
            tr.setPubKey(pubKey);
            tr.setOutputBack(oBack);
            tr.setOutputOut(oOut);
            return tr;
        }
        else{
            System.out.println("Wallet: Insuficient amount for that address");
        }
		return null;
    }

    
    public Map<String, Output> getUnspentOutputs() {
		return unspentOutputs;
	}
    
	public void setUnspentOutputs(Map<String, Output> unspentOutputs) {
		this.unspentOutputs  =unspentOutputs;
		
	}

	public void setBlockchainCopy(Chain chain) {
		this.setChain(chain);
	}

	public void setLastBlock(Block lastBlock) {
		this.lastBlock = lastBlock;
	}
	
	public Block getLastBlock() {
		return lastBlock;
	}

	public Chain getChain() {
		return chain;
	}

	public void setChain(Chain chain) {
		this.chain = chain;
	}
	
	public List<Transaction> getTransToMe() {
		return transToMe;
	}
	
	public void setTransToMe(ArrayList<Transaction> transToMe) {
		this.transToMe = transToMe;
	}
}
