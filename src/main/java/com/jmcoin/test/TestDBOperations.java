package com.jmcoin.test;

import com.google.gson.Gson;
import com.jmcoin.database.DatabaseFacade;
import com.jmcoin.model.Chain;

public class TestDBOperations {

    public static void main(String[] args) {
        Chain c = new Chain();
        //DatabaseFacade.storeBlockChain(c);
        c = DatabaseFacade.getStoredChain();
        System.out.println(new Gson().toJson(DatabaseFacade.getStoredChain()));
    }

}
