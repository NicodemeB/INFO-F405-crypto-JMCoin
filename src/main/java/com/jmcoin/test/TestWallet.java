/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmcoin.test;

import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;

import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Famille
 */
public class TestWallet {
    public static void main(String args[]){
        try {
            Wallet w = new Wallet("a","a");
            w.getAddresses();
            //w.createKeys("a");
            Input in = new Input();
            Output out = new Output();
            out.setAmount(10);
            Transaction tr = new Transaction();
            tr.addInput(in);
            tr.setOutputOut(out);
            tr.setOutputBack(null);
            byte[] signature = SignaturesVerification.signTransaction(tr.getBytes(false), w.getKeys().entrySet().iterator().next().getKey());
            for(PrivateKey key : w.getKeys().keySet()) {
                if(SignaturesVerification.verifyTransaction(signature, tr.getBytes(false), w.getKeys().get(key))) {
                	System.out.println("Verified");
                }
            }
            System.out.println("End");
        } catch (Exception ex) {
            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

