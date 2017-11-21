/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmcoin.test;

import com.jmcoin.model.Input;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;
import com.jmcoin.model.Wallet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Famille
 */
public class TestWallet {
    public static void main(String args[]){
        String rep = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        rep += sep + "Documents" + sep + "PublicKeys";
        System.out.println(rep);

        try {
            Wallet w = new Wallet("a","a");
            w.getAddresses();
            w.createKeys("a");
            Input in = new Input();
            in.setAmount(10);
            Output out = new Output();
            out.setAmount(10);
            Transaction tr = new Transaction();
            tr.addInputOutput(in, out);
            byte[] signature = w.signTransaction(tr, w.getKeys().entrySet().iterator().next().getKey());
            boolean b = w.verifyTransaction(signature, tr, w.getKeys().entrySet().iterator().next().getValue());
            System.out.println(b);
            System.out.println("end");
        } catch (Exception ex) {
            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

