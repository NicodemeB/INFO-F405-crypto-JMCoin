package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiverThread<X extends TemplateThread> implements Runnable{

    protected ObjectInputStream input;
    protected X runnable; 

    public ReceiverThread(X workerRunnable) {
        runnable = workerRunnable;
        input = workerRunnable.getIn();
    }

    @Override
    public void run() {
        boolean loop = true;
        try {
            do {
                Object read = input.readObject();
                if (read != null) {
                    System.out.println("read : " + read.toString());
                    this.runnable.handleMessage(read);
                }
                Thread.sleep(10);
            }while (loop);
        } catch (IOException e) {
            try {
                this.runnable.close();
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
