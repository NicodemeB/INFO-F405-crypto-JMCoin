package com.jmcoin.test;

import com.google.gson.GsonBuilder;
import com.jmcoin.network.MasterNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestGenesisCreation {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MasterNode mn = MasterNode.getInstance();
        Method method = MasterNode.class.getDeclaredMethod("getUnverifiedTransactions");
        method.setAccessible(true);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(method.invoke(mn)));
    }
}
