package com.jmcoin.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Mining;
import com.jmcoin.network.JMProtocolImpl;
import com.jmcoin.network.NetConst;

public class TestMining {
	
	public static Key[] createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException{
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

	public static void main(String[] args) throws InvalidKeyException, NoSuchProviderException, SignatureException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		Mining mining = new Mining();
		try {
			Key[] keys= createKeys("coucoucocu");
			mining.buildBlock((PublicKey)keys[1], (PrivateKey)keys[0]);
			System.out.println(mining.mine());
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.TAKE_MY_MINED_BLOCK, new Gson().toJson(mining.getBlock()));
	}
}
