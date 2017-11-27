package com.jmcoin.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
//    private boolean iHaveSomethingToReceive = false;
//    private boolean iHaveSomethingToSend = false;
    String buffer = null;
    Object receivedMessage = "";
    boolean sendFlag = false;

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

    public void sendMessage(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return  in.readObject();
    }

    public Object getMessage() {
        return receivedMessage;
    }

    public boolean iHaveSomethingToReceive () throws ClassNotFoundException, IOException {
        try {
            Object temp = readMessage();
            receivedMessage = temp;
            return true;
        } catch (EOFException e){
            receivedMessage = null;
            return false;
        }
    }
    public boolean doIHaveSomethingToSend (){
        if (sendFlag == true ) {
            sendFlag = false;
            return true;
        }
        return false;
    }
    public void iHaveSomethingToSend (){
        sendFlag = true;
    }
}
