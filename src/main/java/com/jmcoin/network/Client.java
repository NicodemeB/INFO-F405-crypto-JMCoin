package com.jmcoin.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements  Runnable{
    private Socket requestSocket;
    private ObjectOutputStream out;
    public ObjectInputStream in;
    private boolean sendFlag = false;
    private Object toSend;

    //Client
    public Client (int port, String host) throws IOException {
        requestSocket = new Socket(host, port);
        out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(requestSocket.getInputStream());
    }

    public void close () throws IOException {
        in.close();
        out.close();
        requestSocket.close();
    }

    public synchronized void sendMessage(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
        toSend = null;
        sendFlag = false;
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return  in.readObject();
    }

    public synchronized void setToSend(Object ts){
        toSend = ts;
        sendFlag = true;
    }

    public synchronized Object getToSend(){
        return toSend;
    }

    public void receiveAndTreatMessage() throws InterruptedException {
        try {
            do {
                if (getToSend() != null) {
                    System.out.println("to send : " + getToSend().toString());
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
}
