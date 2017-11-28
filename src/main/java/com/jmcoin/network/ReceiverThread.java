package com.jmcoin.network;

import com.jmcoin.test.TestNetworkClientXServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiverThread implements Runnable{

    ObjectInputStream input;
    Object parent;
    public ReceiverThread(WorkerRunnable workerRunnable, ObjectInputStream in) {
        parent = workerRunnable;
        input = in;
    }
    public ReceiverThread(Client workerRunnable, ObjectInputStream in) {
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
                    // TODO - response about protocol - switch on netconst
                    switch (read.toString()) {
                        case "Connected" :
                            if (read.toString().contains("Connected")){
                                if (parent.getClass().getSimpleName().equals("Client")) {
                                }
                            }
                            break;
                        case "ConnectionRequest":
                            if (parent.getClass().getSimpleName().equals("WorkerRunnable")) {
                                ((WorkerRunnable)parent).setToSend("Connected");
                            }
                            break;
                        default:
                            System.out.println("Default case; case not defined; drop packet");
                            break;
                    }

                }
                Thread.sleep(10);
            }while (loop);
        } catch (IOException e) {
            try {
                if (parent.getClass().getSimpleName().equals("WorkerRunnable")) {
                    ((WorkerRunnable)parent).close();
                } else if (parent.getClass().getSimpleName().equals("Client")) {
                    ((Client)parent).close();
                }
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
