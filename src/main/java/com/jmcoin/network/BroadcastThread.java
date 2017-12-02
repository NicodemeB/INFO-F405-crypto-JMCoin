package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;

public class BroadcastThread <X extends TemplateThread> extends Thread {

    protected ObjectInputStream input;
    protected X runnable;

    public BroadcastThread(X workerRunnable) {
        runnable = workerRunnable;
        input = workerRunnable.getIn();
    }

    @Override
    public void run() {
        sendBroadcast();
    }
    public synchronized void sendBroadcast(){
        do {
            try {
                System.out.println("waiting to broadcast stop mining");
                wait();
                System.out.println("STOP MINING");
                runnable.sendMessage(runnable.protocol.craftMessage(NetConst.STOP_MINING, null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }
    public synchronized void not(){
        System.out.println("notifyAll()");
        notifyAll();
    }
}