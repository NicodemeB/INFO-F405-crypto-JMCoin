package com.jmcoin.model;

public class MasterNode {

    private static MasterNode instance = new MasterNode();

    private MasterNode(){
    }

    public static MasterNode getInstance(){
        return instance;
    }

}
