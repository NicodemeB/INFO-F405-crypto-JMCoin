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

    public WorkerRunnable(Socket clientSocket, String serverText) throws  IOException{
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        in  = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return  in.readObject();
    }

    public void close () throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void run() {
        try {
            //**************************************
            // Client server interaction
            // TODO - POROTOCOL IMPLEMENTATION
            System.out.println(readMessage());
//            Thread.sleep(10 * 1000);
            sendMessage("ok ok");
            close();
            //**************************************

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}

