package com.jmcoin.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Class CipherAES128
 * comes from https://codereview.stackexchange.com/questions/25548/aes-128-encryption-class
 * @author enzo
 *
 */
public class CipherAES128 {
	
	private SecretKey secretKey;
	private IvParameterSpec initVector;
	private final String algo = "AES/CBC/PKCS5Padding";
	
	public CipherAES128(SecretKey secretKey) throws NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
		this.secretKey = secretKey;
		SecureRandom secRandom = new SecureRandom();
		byte[] iv = new byte[16];
		secRandom.nextBytes(iv);
		this.initVector = new IvParameterSpec(iv);
	}
	
	
	public byte[] decrypt(byte[] pPlainText) throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		try {
			Cipher cipher = Cipher.getInstance(this.algo);
			cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.initVector);
			return cipher.doFinal(pPlainText);
		}
		catch(NoSuchAlgorithmException nsae) {
			throw nsae;
		}
		catch(NoSuchPaddingException nspe) {
			throw nspe;
		}
		catch(InvalidKeyException ike) {
			throw ike;
		}
		catch(InvalidAlgorithmParameterException iape) {
			throw iape;
		}
		catch(IllegalBlockSizeException ibse) {
			throw ibse;
		}
		catch(BadPaddingException bpe) {
			throw bpe;
		}
	}
	
	public byte[] encrypt(byte[] pPlainText) throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		try {
			Cipher cipher = Cipher.getInstance(this.algo);
			cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.initVector);
			return cipher.doFinal(pPlainText);
		}
		catch(NoSuchAlgorithmException nsae) {
			throw nsae;
		}
		catch(NoSuchPaddingException nspe) {
			throw nspe;
		}
		catch(InvalidKeyException ike) {
			throw ike;
		}
		catch(InvalidAlgorithmParameterException iape) {
			throw iape;
		}
		catch(IllegalBlockSizeException ibse) {
			throw ibse;
		}
		catch(BadPaddingException bpe) {
			throw bpe;
		}
	}
}
