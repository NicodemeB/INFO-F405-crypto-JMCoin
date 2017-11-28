package com.jmcoin.network;

import java.io.*;
import java.net.Socket;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    
    protected JMProtocolImpl jmProtocol;

    Object toSend;
    boolean sendFlag = false;

    public WorkerRunnable(Socket clientSocket, JMProtocolImpl protocol, String serverText) throws  IOException{
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        in  = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        jmProtocol = protocol;
    }

    synchronized void sendMessage(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
        toSend = null;
        sendFlag = false;
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return  in.readObject();
    }

    public void close () throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }


    synchronized void setToSend(Object ts){
        toSend = ts;
        sendFlag = true;
    }

    synchronized Object getToSend(){
        return toSend;
    }

    public void run() {
        try {
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
            new Thread( new ReceiverThread(this, in)).start();
            boolean loop = true;
            do {
                if (getToSend() != null){
                    System.out.println("to send : " + toSend.toString());
                    sendMessage(toSend);
                }
                Thread.sleep(100);
            } while (true);
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
}

