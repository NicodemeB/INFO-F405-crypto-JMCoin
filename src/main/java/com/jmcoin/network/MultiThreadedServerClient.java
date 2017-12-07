package com.jmcoin.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class MultiThreadedServerClient extends MultiThreadedServer{

    private Vector<WorkerRunnableSC> lThreadsSC;
    private Vector<WorkerRunnableSC> awaitingAnswers;
    private ClientSC client;

    public Vector<WorkerRunnableSC> getAwaitingAnswers() {
        return awaitingAnswers;
    }

    public MultiThreadedServerClient(int port, RelayNodeJMProtocolImpl protocol){
        super(port, protocol);
        lThreadsSC = new Vector<WorkerRunnableSC>();
        awaitingAnswers = new Vector<WorkerRunnableSC>();
    }

    public void setClient(ClientSC client) {
        this.client = client;
    }

    public Vector<WorkerRunnableSC> getlThreadsSC() {
        return lThreadsSC;
    }

    @Override
    public void run() {
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
                lThreadsSC.add(new WorkerRunnableSC(clientSocket, protocol, this.client));
                lThreadsSC.lastElement().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server Stopped.");
    }

    @Override
    public synchronized void not(){
        for (WorkerRunnableSC wr: lThreadsSC) {
            wr.not();
        }
    }
}


