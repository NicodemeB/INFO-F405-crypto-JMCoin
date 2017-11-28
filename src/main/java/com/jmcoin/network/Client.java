package com.jmcoin.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    Socket requestSocket;
    ObjectOutputStream out;
    public ObjectInputStream in;
    String buffer = null;
    Object receivedMessage = "";
    private boolean sendFlag = false;
    Object toSend;

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
}
