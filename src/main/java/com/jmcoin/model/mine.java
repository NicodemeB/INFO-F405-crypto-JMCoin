package crypto;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * 
 * 
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 */
public class mine {
   private int nounce;
   private String previousHash;
   private InputStream bloc;
   private String hash;
   private static String complexity="000";
   private String Proof="";

 



   public mine(String previousHash, InputStream bloc)
   {
      this.bloc=bloc;
      nounce=0;
      this.previousHash=previousHash;
   }
   
   public int getNounce(){return nounce;}
   public String getProof(){return Proof;}
   public String getPreviousHash(){return previousHash;}
   
   public String calculateHash() throws NoSuchAlgorithmException 
   {
	MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((nounce + previousHash + bloc).getBytes());
        byte[] currentHash=md.digest(); 
        StringBuffer sb = new StringBuffer(); 
        for (byte b1 : currentHash) {sb.append(Integer.toHexString(b1 & 0xff)); } 
        String hash=sb.toString(); 
        return hash;
   }
   
   

   
   
   public String proofOfWork()
   {
        try
        {
            boolean done=false;
            while(!(done))
                {
                    hash = calculateHash();  
                    if(hash.substring(0, 3).equals(complexity))
                        {
                            Proof= hash;
                            nounce=nounce;
                            done=true;
                        }
                nounce++;    
                     
                }
        }
        catch (NoSuchAlgorithmException e){return null;}
   
        return Proof;
   }
   
    
   
}
