package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends TemplateThread{
	
	public Client (int port, String host, JMProtocolImpl<? extends Peer> protocol) throws IOException {
		super(protocol);
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
	
    public synchronized void sendMessage(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
        toSend = null;
        sendFlag = false;
    }

    public void receiveAndHandleMessage() throws InterruptedException {
        try {
            do {
                if (getToSend() != null) {
                    System.out.println("Thread #"+Thread.currentThread().getId() +" Client - to send : " + getToSend().toString());
                    sendMessage(getToSend());
                }
                Thread.sleep(100);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
                System.out.println("close");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            receiveAndHandleMessage();
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
            break;
        default:
        	System.out.println(this.protocol.processInput(msg));
            break;
		}
	}
}
