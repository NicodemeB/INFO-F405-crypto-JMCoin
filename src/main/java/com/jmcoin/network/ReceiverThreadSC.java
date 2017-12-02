package com.jmcoin.network;

import java.io.IOException;

public class ReceiverThreadSC extends ReceiverThread {

    public ReceiverThreadSC(TemplateThread workerRunnable) {
        super(workerRunnable);
    }

    @Override
    public void run() {
        boolean loop = true;
        try {
            do {
                Object read = input.readObject();
                if (read != null) {
                    System.out.println("Thread #"+Thread.currentThread().getId() + " read : " + read.toString());
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
