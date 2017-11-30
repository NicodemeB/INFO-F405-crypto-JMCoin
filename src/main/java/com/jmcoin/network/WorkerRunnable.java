package com.jmcoin.network;

import java.io.*;
import java.net.Socket;

/**

 */
public class WorkerRunnable extends TemplateThread{

    public WorkerRunnable(Socket clientSocket, JMProtocolImpl<? extends Peer> protocol) throws  IOException{
    	super(protocol);
        this.socket = clientSocket;
        in  = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void run() {
        try {
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
            new Thread( new ReceiverThread<WorkerRunnable>(this)).start();
            boolean loop = true;
            do {
                if (getToSend() != null){
                    System.out.println("to send : " + toSend.toString());
                    sendMessage(toSend);
                }
                Thread.sleep(100);
            } while (loop);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	@Override
	protected void handleMessage(Object msg) {
		switch (msg.toString()) {
        case NetConst.CONNECTED :
            break;
        case NetConst.CONNECTION_REQUEST:
        	setToSend(NetConst.CONNECTED);
            break;
        default:
        	setToSend(this.protocol.processInput(msg));
            break;
    }
	}
}

