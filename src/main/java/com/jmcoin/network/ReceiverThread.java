package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiverThread<X extends TemplateThread> implements Runnable{

    protected ObjectInputStream input;
    protected JMProtocolImpl<? extends Peer> jmProtocol;
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
                    // TODO - response about protocol - switch on netconst
                    switch (read.toString()) {
                        case NetConst.CONNECTED :
                        	if (this.runnable.getClass().equals(Client.class)) {
                            }
                            break;
                        case NetConst.CONNECTION_REQUEST:
                            if (this.runnable.getClass().equals(WorkerRunnable.class)) {
                                this.runnable.setToSend(NetConst.CONNECTED);
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
