package com.jmcoin.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

import javax.persistence.Transient;

public class MasterNode extends Peer{

    private static MasterNode instance = new MasterNode();
	@Transient
	public static final int REWARD_START_VALUE = 10;
	@Transient
	public static final int REWARD_RATE = 100;
    private LinkedList<Transaction> unverifiedTransactions;
    
    private Map<String, Output> unspentOutputs; //key = hash of the transaction containing the output
    private Chain chain;
    private Block lastBlock;
       
    private int difficulty = NetConst.DEFAULT_DIFFICULTY;

    private MasterNode(){
    	super();
    	this.chain = new Chain();
    	this.unverifiedTransactions = new LinkedList<>();
    	this.unspentOutputs = new HashMap<>();
    	this.lastBlock = new Block();
    	this.lastBlock.setDifficulty(32);
    	this.lastBlock.setFinalHash("h0");
    }
    
    public Block getLastBlock() {
		return lastBlock;
	}
    
    public Map<String, Output> getUnspentOutputs() {
		return unspentOutputs;
	}
	
	protected LinkedList<Transaction> getUnverifiedTransactions() {
		return unverifiedTransactions;
	}

    public static MasterNode getInstance(){
        return instance;
    }
    
    public int getDifficulty() {
		return difficulty;
	}
    
    public Chain getChain() {
		return chain;
	}
    
    //TODO compute this reward according to the the size of the transaction
    //almost empty -> low reward
    public int getRewardAmount() {
    	return REWARD_START_VALUE / ((chain.getSize() / REWARD_RATE) + 1);
    }
    
    /**
     * TODO Fork -> some verified transactions could set as "unverified". Same thing with spent outputs
     * @param pBlock
     * @throws IOException 
     * @throws SignatureException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    public void processMinedBlock(Block pBlock) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException {
    		//TODO verifier le hash du bloc 
    		//variable temporaires utilisées pour mettre à jour les pools de manière atomique
    		Map<String,Output> tempToRemoveOutputs = new HashMap<String,Output>();
    		Map<String,Output> tempToAddOutputs = new HashMap<String,Output>();
    	
    		for(final Transaction trans : pBlock.getTransactions())
    		{
			if(super.verifyBlockTransaction(trans, chain, (Output[])this.unspentOutputs.values().toArray()))
			{
				String address = SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey());
				//reparcourir les inputs déja validés pour traitement
				for(Input input : trans.getInputs())
				{
					Transaction prevTrans = chain.findInBlockChain(input.getPrevTransactionHash());
					if(prevTrans.getOutputOut().getAddress().equals(address))
					{
						tempToRemoveOutputs.put(Hex.toHexString(prevTrans.getHash()),prevTrans.getOutputOut());
					}
					else if (prevTrans.getOutputBack().getAddress().equals(address))
					{
						tempToRemoveOutputs.put(Hex.toHexString(prevTrans.getHash()),prevTrans.getOutputBack());
					}
					//sinon probleme mais normalement impossible
				}
				//adding new outputs to the pool
				tempToAddOutputs.put(Hex.toHexString(trans.getHash())+"$"+trans.getOutputOut().getAddress(),trans.getOutputOut());//delimiter pour avoir une clé unique : concat du hash / adresse
				if(trans.getOutputBack() != null) 
				{
					tempToAddOutputs.put(Hex.toHexString(trans.getHash())+"$"+trans.getOutputBack().getAddress(),trans.getOutputBack());
				}
			}
		}
    		for(final Transaction trans : pBlock.getTransactions()){
    			if(!this.unverifiedTransactions.removeIf(trans::equals))return; //transaction has to be in unverified transaction pool before being added to the chain
    			//Bouger le reste aussi ?
    		}
    		for (Map.Entry<String,Output> entry : tempToRemoveOutputs.entrySet())
    		{
    		    unspentOutputs.remove(entry.getKey());
    		}
    		for (Map.Entry<String,Output> entry : tempToAddOutputs.entrySet())
    		{
    		    unspentOutputs.put(entry.getKey(),entry.getValue());
    		}
    		chain.addBlock(pBlock);
    		this.lastBlock = pBlock;
    }
    
    /* To delete ?
     public boolean canBeAdded(Block pBlock){
	    	if(pBlock.getClass() == Genesis.class)return true;
	    	if(!isFinalHashRight(pBlock))return false;
	    	if (!doesPrevBlocKExists(pBlock)) return false;
	    	if (pBlock.getSize() > Block.MAX_BLOCK_SIZE) return false;
	    	return true;
    }
    public boolean isFinalHashRight(Block pBlock) {
	    	BigInteger value = new BigInteger(pBlock.getFinalHash(), 16);
	    	return value.shiftRight(32*8 - pBlock.getDifficulty()).intValue() == 0;
    }
    /**
     * Checks if the previous block exists in the chain, based on the hash
     * @param pBlock
     * @return
     */
    /*private boolean doesPrevBlocKExists(Block pBlock) {
	    	for(String key : chain.getBlocks().keySet()) {
	    		if (chain.getBlocks().get(key).getFinalHash().equals(pBlock.getPrevHash())) {
	    			return true;
				}
	    	}
	    	return false;
    }
      */

	public List<Transaction> getTransactionsToThisAddress(String address) {
		List<Transaction> transactions = new ArrayList<>();
		//FIXME get all transactions related to this address
		return transactions;
	}
}
