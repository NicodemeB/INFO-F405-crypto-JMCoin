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

    Object receivedMessage = "";
    boolean sendFlag = false;
    Object toSend;
    boolean loop = true;

    public WorkerRunnable(Socket clientSocket, JMProtocolImpl protocol, String serverText) throws  IOException{
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        in  = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        jmProtocol = protocol;
    }

    public void sendMessage(Object msg) throws IOException {
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
    public Object getMessage() {
        return receivedMessage;
    }

    public void run() {
        try {
            //**************************************
            // Client server interaction
            // TODO - PROTOCOL IMPLEMENTATION
            // TODO - Implement abstract class and return a correct value
            new Thread( new Reader(this, in)).start();
            boolean loop = true;
            do {
                if (toSend != null){
                    System.out.println("to send : " + toSend.toString());
                    sendMessage(toSend);
                }
                Thread.sleep(100);
            } while (loop);

            System.out.println(readMessage());
            sendMessage("TODO - Implement abstract class and return a correct value TEST8");

            close();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
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

