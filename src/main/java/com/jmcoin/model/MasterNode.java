package com.jmcoin.model;

public class MasterNode implements Node {

    private static MasterNode instance = new MasterNode();



    private MasterNode(){

    }

    public static MasterNode getInstance(){
        return instance;
    }

}
