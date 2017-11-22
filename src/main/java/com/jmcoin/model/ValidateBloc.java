
package com.jmcoin.model;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; 
/**
 * 
 * 
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 */

public class ValidateBloc 
{
    private int nbTransactions=0;
    private FileInputStream verifiedBlock;
    private Mine mine = null;
    
    public ValidateBloc(Mine mine,  FileInputStream verifiedBlock)
    {
        this.mine=mine;
        this.verifiedBlock=verifiedBlock;
    }
    
//public int getNbTransactions(){return nbTransactions;}
    
    
public String mineThisBlock()throws FileNotFoundException, IOException 
{       try{
            InputStreamReader lecture = new InputStreamReader(verifiedBlock);
            BufferedReader buff = new BufferedReader(lecture);
            String ligne;
            while ((ligne=buff.readLine())!=null)
                {
                nbTransactions++;
                }
                 if(nbTransactions == 4)
                    {
                        mine.proofOfWork();
                       
                    }
                else if(nbTransactions>4 || nbTransactions<4)
                    {
                        System.out.println(" This block contain more or less Transactions");
                    }
            buff.close();
            
            }
            
            		
        catch (Exception e){}  
        return mine.getNounce()+"   "+mine.getProof()+"  "+mine.getPreviousHash()+ Integer.toString(nbTransactions);
}
}