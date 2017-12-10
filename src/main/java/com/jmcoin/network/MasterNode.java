package com.jmcoin.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jmcoin.crypto.AES;
import org.bouncycastle.util.encoders.Hex;

import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.database.DatabaseFacade;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
//import com.jmcoin.model.Genesis;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class MasterNode extends Peer{

    private static MasterNode instance = new MasterNode();
	public static final int REWARD_START_VALUE = 10;
	public static final int REWARD_RATE = 100;
    private LinkedList<Transaction> unverifiedTransactions;
    
    private Map<String, Output> unspentOutputs;
    private Chain chain;
    private Block lastBlock;
       
    private int difficulty = NetConst.DEFAULT_DIFFICULTY;

    private MasterNode(){
    	super();
    	this.unverifiedTransactions = new LinkedList<>();
    	this.unspentOutputs = new HashMap<>();
    	
    	//TODO uncomment this
    	/*this.chain = DatabaseFacade.getStoredChain();
    	this.lastBlock = DatabaseFacade.getLastBlock();
    	if(this.lastBlock == null) {
    		try {
				this.lastBlock = Genesis.getInstance();
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
					| IOException | InvalidKeyLengthException | StrongEncryptionNotAvailableException e) {
				e.printStackTrace();
			}
    	}*/
    	this.chain = new Chain();
    	try {
			addGenesisToUnverfied();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
				| IOException | InvalidKeyLengthException | StrongEncryptionNotAvailableException e) {
    		e.printStackTrace();
		}
    }
    
    public void debugMasterNode(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException {
    	throw new UnsupportedOperationException("Broken due to the deletion of Genesis class");
    	/*KeyGenerator generator = new KeyGenerator(1024);
    	Map<PrivateKey, PublicKey> keys = new HashMap<>();
    	for(int i = 0; i < 3; i++) {
    		generator.createKeys();
    		keys.put(generator.getPrivateKey(), generator.getPublicKey());
    	}
    	Genesis genesis = null;
    	try {
			genesis = Genesis.getInstance();
		} catch (InvalidKeyException | SignatureException | IOException | InvalidKeyLengthException
				| StrongEncryptionNotAvailableException e1) {
			e1.printStackTrace();
		}
    	Output out1 = new Output();
    	out1.setAddress("A0");
    	out1.setAmount(42);
    	Output out2 = new Output();
    	out2.setAddress("A1");
    	out2.setAmount(24);
    	Output out3 = new Output();
    	out3.setAddress("A2");
    	out3.setAmount(4);
    	unspentOutputs.put("uno!", out1);
    	unspentOutputs.put("dos!", out2);
    	unspentOutputs.put("tre!", out3);
    	Input in1 = new Input();
    	in1.setAmount(out1);
    	in1.setPrevTransactionHash(genesis.getFinalHash().getBytes());
    	Transaction unvfTrans1 = new Transaction();
    	unvfTrans1.setOutputBack(out3);
    	unvfTrans1.setOutputOut(out2);
    	unvfTrans1.addInput(in1);
    	PrivateKey privKey = keys.keySet().iterator().next();
    	unvfTrans1.setPubKey(keys.get(privKey).getEncoded());
    	try {
			unvfTrans1.setSignature(SignaturesVerification.signTransaction(unvfTrans1.getBytes(false), privKey));
		} catch (InvalidKeyException | SignatureException | IOException e) {
			e.printStackTrace();
		}
    	unvfTrans1.computeHash();
    	this.unverifiedTransactions.add(unvfTrans1);
    	Block block = new Block();
    	block.getTransactions().add(unvfTrans1);
    	block.setDifficulty(14);
    	block.setNonce(5);
    	this.chain.getBlocks().put("B1", block);*/
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
    
    public int getRewardAmount() {
    	return REWARD_START_VALUE / ((chain.getSize() / REWARD_RATE) + 1);
    }
    
    /**
     * @param pBlock
     * @throws IOException 
     * @throws SignatureException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    public boolean processMinedBlock(Block pBlock) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException {
    	if(!canBeAdded(pBlock))return false;
		//variable temporaires utilisées pour mettre à jour les pools de manière atomique
		Map<String,Output> tempToRemoveOutputs = new HashMap<String,Output>();
		Map<String,Output> tempToAddOutputs = new HashMap<String,Output>();
		for(final Transaction trans : pBlock.getTransactions()){
			if(verifyBlockTransaction(trans, chain, this.unspentOutputs)){
				String address = SignaturesVerification.DeriveJMAddressFromPubKey(trans.getPubKey());
				//reparcourir les inputs déja validés pour traitement
				for(Input input : trans.getInputs()){
					Transaction prevTrans = chain.findInBlockChain(input.getPrevTransactionHash());
					if(prevTrans != null) {
						if(prevTrans.getOutputOut().getAddress().equals(address)){
							tempToRemoveOutputs.put(Hex.toHexString(prevTrans.getHash()),prevTrans.getOutputOut());
						}
						else if (prevTrans.getOutputBack().getAddress().equals(address)){
							tempToRemoveOutputs.put(Hex.toHexString(prevTrans.getHash()),prevTrans.getOutputBack());
						}
						else {
							return false; //sinon probleme mais normalement impossible
						}
					}
				}
				//adding new outputs to the pool
				tempToAddOutputs.put(Hex.toHexString(trans.getHash())+DELIMITER+trans.getOutputOut().getAddress(),trans.getOutputOut());//delimiter pour avoir une clé unique : concat du hash / adresse
				if(trans.getOutputBack() != null) {
					tempToAddOutputs.put(Hex.toHexString(trans.getHash())+DELIMITER+trans.getOutputBack().getAddress(),trans.getOutputBack());
				}
			}
		}
		for(final Transaction trans : pBlock.getTransactions()){
			if(!this.unverifiedTransactions.removeIf(trans::equals))
				return false; //transaction has to be in unverified transaction pool before being added to the chain
		}
		for (String key : tempToRemoveOutputs.keySet()){
		    unspentOutputs.remove(key);
		}		
		for (Map.Entry<String,Output> entry : tempToAddOutputs.entrySet()){
		    unspentOutputs.put(entry.getKey(),entry.getValue());
		}
		this.chain.getBlocks().put(pBlock.getFinalHash() + pBlock.getTimeCreation(), pBlock);
		this.lastBlock = pBlock;
		return true;
    }
    
     public boolean canBeAdded(Block pBlock){
    	if(pBlock == null)return false;
    	//if(pBlock.getClass().equals(Genesis.class))return true;
		 //TODO check if this is a good replacement for     ^^^^
		if(chain.getSize() == 0 && pBlock.getPrevHash() == null) return true;
    	if(!isFinalHashRight(pBlock))return false;
//FIXME uncomment this    	if (DatabaseFacade.getBlockWithHash(pBlock.getPrevHash()) == null) return false;
    	if (pBlock.getSize() > Block.MAX_BLOCK_SIZE) return false;
    	return true;
    }
     
    private boolean isFinalHashRight(Block pBlock) {
	    BigInteger value = new BigInteger(pBlock.getFinalHash(), 16);
	    return value.shiftRight(32*8 - pBlock.getDifficulty()).intValue() == 0;
    }

	public List<Transaction> getTransactionsToThisAddress(String addresses) {
		String[] tabAddresses = gson.fromJson(addresses, String[].class);
		return debugGetTransactionsToThisAddress(tabAddresses);
		//FIXME uncomment this return DatabaseFacade.getAllTransactionsWithAddress(tabAddresses);
	}

	private ArrayList<Transaction> debugGetTransactionsToThisAddress(String[] addresses){
		ArrayList<Transaction> transactions = new ArrayList<>();
		for(String addr : addresses) {
			for(Transaction trans : this.unverifiedTransactions) {
				if(trans.getOutputBack().getAddress().equals(addr)||trans.getOutputOut().getAddress().equals(addr))
					transactions.add(trans);
			}
		}
		return transactions;
	}

	private void addGenesisToUnverfied() throws NoSuchAlgorithmException, IOException, NoSuchProviderException, StrongEncryptionNotAvailableException, InvalidKeyLengthException, SignatureException, InvalidKeyException {
		Key[] keys = generateGenesisKeys("genesis");
		PrivateKey privKey = (PrivateKey) keys[0];
		PublicKey pubKey = (PublicKey) keys[1];

		Input inGenesis = new Input();
		inGenesis.setPrevTransactionHash(null);
		Output outGenesis = new Output();
		outGenesis.setAmount(42);
		outGenesis.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(pubKey.getEncoded()));
		Transaction transGenesis = new Transaction();
		transGenesis.setOutputBack(null);
		transGenesis.setOutputOut(outGenesis);
		transGenesis.addInput(inGenesis);
		transGenesis.setPubKey(pubKey.getEncoded());
		transGenesis.setSignature(SignaturesVerification.signTransaction(transGenesis.getBytes(false), privKey));
		transGenesis.computeHash();
		unverifiedTransactions.add(transGenesis);
	}

	private Key[] generateGenesisKeys(String pass) throws NoSuchProviderException, NoSuchAlgorithmException, StrongEncryptionNotAvailableException, InvalidKeyLengthException, IOException {
		KeyGenerator keyGen = new KeyGenerator(1024);
		keyGen.createKeys();
		PrivateKey privateKey = keyGen.getPrivateKey();
		PublicKey publicKey = keyGen.getPublicKey();
		char[] AESpw = pass.toCharArray();
		ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
		ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
		AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);
		return new Key[] {privateKey, publicKey};
	}
}
