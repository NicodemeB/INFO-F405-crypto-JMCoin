/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmcoin.test;

import com.jmcoin.crypto.AES;
import com.jmcoin.model.Wallet;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
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
//
//        try {
//        Wallet w = new Wallet("a","a");
//            w.createKeys("yo");
//            w.getAddresses();
//            System.out.println("end");
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeySpecException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (AES.InvalidPasswordException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (AES.InvalidAESStreamException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (AES.StrongEncryptionNotAvailableException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (AES.InvalidKeyLengthException ex) {
//            Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}

