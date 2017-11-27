package com.jmcoin.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class MultiThreadedServer implements Runnable{

    protected int          serverPort   = -1;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected JMProtocolImpl<? extends Peer> protocol	= null;

    public MultiThreadedServer(int port, JMProtocolImpl<? extends Peer> protocol){
        this.serverPort = port;
        this.protocol = protocol;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            try {
                new Thread(
                        new WorkerRunnable(clientSocket, protocol,  "Multithreaded Server")
                ).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            System.out.println("Launching server on port : " + this.serverPort);
            this.serverSocket = new ServerSocket(this.serverPort);
            System.out.println("Running server on port : " + this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+ this.serverPort, e);
        }
    }

}