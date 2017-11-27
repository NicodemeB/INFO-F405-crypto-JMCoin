package com.jmcoin.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Reader implements Runnable{

    ObjectInputStream input;
    WorkerRunnable parent;
    public Reader(WorkerRunnable workerRunnable, ObjectInputStream in) {
        parent = workerRunnable;
        input = in;
    }

    @Override
    public void run() {
        boolean loop = true;
        try {
            do {
                Object read = input.readObject();
                if (read != null) {
                    System.out.println("read : " + read.toString());
                    // TODO - response about protocol
                    parent.toSend = "test";
                    parent.sendFlag = true;
                }
                Thread.sleep(10);
            }while (loop);
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                parent.close();
                parent.loop=false;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
