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
	private Map<String, Output> unspentOutputs;
	private Chain blockchainCopy;
	private Block lastBlock;

	public UserNode(String email) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		this.wallet = new Wallet(email);
	}

	public Chain getBlockchainCopy() {
		return blockchainCopy;
	}
	
	public Map<String, Output> getUnspentOutputs() {
		return unspentOutputs;
	}
	
	public void setUnspentOutputs(Map<String, Output> unspentOutputs) {
		this.unspentOutputs = unspentOutputs;
	}

	public void setBlockchainCopy(Chain blockchainCopy) {
		this.blockchainCopy = blockchainCopy;
	}
	
    public Transaction createTransaction(String fromAddress, String toAddress, double amountToSend, PrivateKey privKey, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, FileNotFoundException, SignatureException{
        //Recupérer la liste de transacction avec des outputs disponibles pour cette adresse TO DO from network
        ArrayList<Transaction> addressTransactions = new ArrayList<Transaction>();
        ArrayList<Input> addressInputs = new ArrayList<Input>();
        double totalOutputAmount = 0;
        for(int i = 0 ; i < addressTransactions.size(); i++){
            if(addressTransactions.get(i).getOutputBack().getAddress().equals(fromAddress)){
                totalOutputAmount+= addressTransactions.get(i).getOutputBack().getAmount();
                addressInputs.add(new Input(fromAddress,addressTransactions.get(i).getOutputBack().getAmount(),addressTransactions.get(i).getHash()));
            }
            else{  
                if((addressTransactions.get(i).getOutputOut().getAddress().equals(fromAddress))){
                    totalOutputAmount+= addressTransactions.get(i).getOutputOut().getAmount();
                    addressInputs.add(new Input(fromAddress,addressTransactions.get(i).getOutputOut().getAmount(),addressTransactions.get(i).getHash()));
                }
                else{
                    System.out.println("Wallet : No output belonging to this address");
                }
            }           
        }
        if(amountToSend <= totalOutputAmount){
            Output oOut = new Output(amountToSend, toAddress);
            Output oBack = new Output(totalOutputAmount-amountToSend, fromAddress);
            Transaction tr = new Transaction(addressInputs,oOut, oBack,pubKey);
            tr.setSignature(SignaturesVerification.signTransaction(tr.getBytes(false), privKey));
            tr.computeHash();
            return tr;
        }
        else{
            System.out.println("Wallet: Insuficient amount for that address");
        }
        //this.wallet.addTransaction(new Transaction());
		return null;
    }

	public Block getLastBlock() {
		return lastBlock;
	}

	public void setLastBlock(Block lastBlock) {
		this.lastBlock = lastBlock;
	}
}
