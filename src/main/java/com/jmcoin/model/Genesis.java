package com.jmcoin.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.network.NetConst;

public class Genesis extends Block {
	
	private static final long serialVersionUID = -3874611565736597876L;
	private static Genesis genesis;
	
	private static Key[] createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException{
		KeyGenerator keyGen = new KeyGenerator(1024);
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);
        return new Key[] {privateKey, publicKey};
     }
	
	private Genesis() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		super();
		Key[] keys = createKeys("genesis");
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
		this.transactions.add(transGenesis);
		this.difficulty = NetConst.DEFAULT_DIFFICULTY;
	}
	
	public static Genesis getInstance() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		return genesis == null ? (genesis = new Genesis()):genesis;
	}

	public Output getOuputOut() {
		return this.transactions.get(0).getOutputOut();
	}
}
