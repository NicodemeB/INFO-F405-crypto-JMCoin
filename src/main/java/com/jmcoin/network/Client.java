package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends TemplateThread{
	
	/*private String response;
	
	public String getResponse() {
		String res = response;
		this.response = null;
		return res;
	}*/
	
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

    public void receiveAndTreatMessage() throws InterruptedException {
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
            receiveAndTreatMessage();
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
        	this.protocol.processInput(msg);
            break;
		}
	}
}
