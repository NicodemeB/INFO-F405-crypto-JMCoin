package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestSender {
    private Socket requestSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public RequestSender (int port, String host) throws IOException {
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

    public void sendMessage(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return  in.readObject();
    }
}