package com.jmcoin.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;
/**
 * 
 * 
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 */
public class Mine {
	
   private Block block;
   private MessageDigest digest;

   public Mine(Block pBlock) throws NoSuchAlgorithmException{
	   this.block = pBlock;
	   this.digest = MessageDigest.getInstance("SHA-256");
   }
      
   private byte[] calculateHash(int nonce) {
	   block.setNonce(nonce);
	   this.digest.update(block.toString().getBytes());
	   return this.digest.digest();
   }
   
   public String proofOfWork() {
	   if(block.getSize() > Block.MAX_BLOCK_SIZE) return null;
	   int nonce = Integer.MIN_VALUE;
       while(nonce < Integer.MAX_VALUE){
		   if (verifyAndSetHash(nonce++)) return block.getFinalHash();
       }
	   if (verifyAndSetHash(nonce)) return block.getFinalHash();
	   return null;
    }

	private boolean verifyAndSetHash(int nonce) {
		byte[] hash;
		if(this.block.verifyHash((hash = calculateHash(nonce)))){
            this.block.setFinalHash(Hex.toHexString(hash));
			return true;
        }
		return false;
	}
}
