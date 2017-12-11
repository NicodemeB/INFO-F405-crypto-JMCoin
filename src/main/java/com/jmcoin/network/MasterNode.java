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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jmcoin.crypto.AES;
import org.bouncycastle.util.encoders.Hex;

import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
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
    	/*
    	this.chain = DatabaseFacade.getStoredChain();
    	if(chain == null){
    		chain = new Chain();
    		DatabaseFacade.storeBlockChain(chain);
		}
    	this.lastBlock = DatabaseFacade.getLastBlock();
		if(chain.getSize() == 0) {
			try {
				addGenesisToUnverfied();
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
					| IOException | InvalidKeyLengthException | StrongEncryptionNotAvailableException e) {
				e.printStackTrace();
			}
		}*/
    	//TODO remove this to use database
		chain = new Chain();
		if(chain.getSize() == 0) {
			try {
				addGenesisToUnverified();
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
					| IOException | InvalidKeyLengthException | StrongEncryptionNotAvailableException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void debugMasterNode(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException, StrongEncryptionNotAvailableException, InvalidKeyLengthException {
    	/*KeyGenerator generator = new KeyGenerator(1024);
        Map<PrivateKey, PublicKey> keys = new HashMap<>();
        PrivateKey[] keyskeys = new PrivateKey[4];
        for(int i = 0; i < 3; i++) {
            generator.createKeys();
            keyskeys[i] = generator.getPrivateKey();
            keys.put(keyskeys[i], generator.getPublicKey());
        }
        keys.put(privateKey, publicKey);
        keyskeys[3] = privateKey;
        
        //create one random block
        /*Block inChain = new Block();
        inChain.setDifficulty(12);
        inChain.setFinalHash("00001");
        inChain.setNonce(0);
        inChain.setPrevHash(null);
        inChain.setTimeCreation(System.currentTimeMillis());
        
        Transaction tr1 = new Transaction();
        Input input1 = new Input();
        input1.setAmount(this.unverifiedTransactions.get(0).getOutputOut());
        input1.setPrevTransactionHash(this.unverifiedTransactions.get(0).getHash());
        
        Output outputOutToMe = new Output();
        outputOutToMe.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(publicKey.getEncoded()));
        outputOutToMe.setAmount(12);
        Output outputBack = new Output();
        outputBack.setAddress(this.unverifiedTransactions.get(0).getOutputOut().getAddress());
        outputBack.setAmount(this.unverifiedTransactions.get(0).getOutputOut().getAmount() -12);
        
        tr1.addInput(input1);
        tr1.setOutputBack(outputBack);
        tr1.setOutputOut(outputOutToMe);
        tr1.setPubKey(publicKey.getEncoded());
        tr1.setSignature(SignaturesVerification.signTransaction(tr1.getBytes(false), privateKey));
        tr1.computeHash();
        
        Transaction tr2 = new Transaction();
        Output outputOut2 = new Output();
        outputOut2.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(keyskeys[0]).getEncoded()));
        outputOut2.setAmount(10);
        Output outputBack2 = new Output();
        outputBack2.setAddress(this.unverifiedTransactions.get(0).getOutputOut().getAddress());
        outputBack2.setAmount(20);
        Input input2 = new Input();
        input2.setAmount(this.unverifiedTransactions.get(0).getOutputOut());
        input2.setPrevTransactionHash(this.unverifiedTransactions.get(0).getHash());
        
        tr2.addInput(input2);
        tr2.setOutputBack(outputBack2);
        tr2.setOutputOut(outputOut2);
        tr2.setPubKey(keys.get(keyskeys[1]).getEncoded()); //should fail
        tr2.setSignature(SignaturesVerification.signTransaction(tr2.getBytes(false), keyskeys[1]));
        tr2.computeHash();
        this.unverifiedTransactions.add(tr1);
        this.unverifiedTransactions.add(tr2);*/
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
							tempToRemoveOutputs.put(Hex.toHexString(prevTrans.getHash())+DELIMITER+prevTrans.getOutputOut().getAddress(),prevTrans.getOutputOut());
						}
						else if (prevTrans.getOutputBack().getAddress().equals(address)){
							tempToRemoveOutputs.put(Hex.toHexString(prevTrans.getHash())+DELIMITER+prevTrans.getOutputBack().getAddress(),prevTrans.getOutputBack());
						}
						else {
							return false; //sinon probleme mais normalement impossible
						}
					}
				}
				//adding new outputs to the pool
				System.out.println("Trans: "+this.gson.toJson(trans));
				System.out.println("Hash "+Hex.toHexString(trans.getHash()));
				tempToAddOutputs.put(Hex.toHexString(trans.getHash())+DELIMITER+trans.getOutputOut().getAddress(),trans.getOutputOut());//delimiter pour avoir une clé unique : concat du hash / adresse
				if(trans.getOutputBack().getAddress() != null) {
					tempToAddOutputs.put(Hex.toHexString(trans.getHash())+DELIMITER+trans.getOutputBack().getAddress(),trans.getOutputBack());
				}
			}
			else {
				return false;
			}
		}
		for(final Transaction trans : pBlock.getTransactions()){
			if(!this.unverifiedTransactions.removeIf(trans::equals) && trans.getOutputBack().getAddress() != null) {
				return false;
			}
			//transaction has to be in unverified transaction pool before being added to the chain,
			//except when it's a reward!!
		}
		System.out.println("--------------------------------------------");
		System.out.println(this.gson.toJson(this.unspentOutputs));
		for (String key : tempToRemoveOutputs.keySet()){
		    unspentOutputs.remove(key);
		}		
		for (Map.Entry<String,Output> entry : tempToAddOutputs.entrySet()){
		    unspentOutputs.put(entry.getKey(),entry.getValue());
		}
		System.out.println(this.gson.toJson(this.unspentOutputs));
		System.out.println("--------------------------------------------");
		this.chain.getBlocks().put(pBlock.getFinalHash() + pBlock.getTimeCreation(), pBlock);
		this.lastBlock = pBlock;
		return true;
    }
    
     public boolean canBeAdded(Block pBlock){
    	if(pBlock == null)return false;
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
				if(addr.equals(trans.getOutputBack().getAddress())||addr.equals(trans.getOutputOut().getAddress()))
					transactions.add(trans);
			}
		}
		return transactions;
	}

	private void addGenesisToUnverified() throws NoSuchAlgorithmException, IOException, NoSuchProviderException, StrongEncryptionNotAvailableException, InvalidKeyLengthException, SignatureException, InvalidKeyException {
		Key[] keys = generateGenesisKeys(NetConst.GENESIS);
		PrivateKey privKey = (PrivateKey) keys[0];
		PublicKey pubKey = (PublicKey) keys[1];
		Input inGenesis = new Input();
		inGenesis.setPrevTransactionHash(null);
		Output outGenesis = new Output();
		outGenesis.setAmount(42);
		outGenesis.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(pubKey.getEncoded()));
		Transaction transGenesis = new Transaction();
		Output outputBack = new Output();
		outputBack.setAddress(null);
		outputBack.setAmount(0);
		transGenesis.setOutputBack(outputBack);
		transGenesis.setOutputOut(outGenesis);
		transGenesis.addInput(inGenesis);
		transGenesis.setPubKey(pubKey.getEncoded());
		transGenesis.setSignature(SignaturesVerification.signTransaction(transGenesis.getBytes(false), privKey));
		transGenesis.computeHash();
		unverifiedTransactions.add(transGenesis);
		this.lastBlock = new Block();
		this.lastBlock.setFinalHash(NetConst.GENESIS);
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
