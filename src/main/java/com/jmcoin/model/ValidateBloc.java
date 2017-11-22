
package com.jmcoin.model;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * 
 * 
 * @author Trifi Mohamed Nabil
 * @author Arbib Mohamed
 */

public class ValidateBloc 
{
    private int nbTransactions=0;
    private Mine mine = null;
    
    public ValidateBloc(Mine mine)
    {
        this.mine=mine;
    }    
    
	
}