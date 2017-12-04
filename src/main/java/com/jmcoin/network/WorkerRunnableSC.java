package com.jmcoin.network;

import java.io.IOException;
import java.net.Socket;

public class WorkerRunnableSC extends WorkerRunnable {

    private ClientSC client;
    private Thread thread;

    public WorkerRunnableSC(Socket clientSocket, JMProtocolImpl<? extends Peer> protocol, ClientSC client) throws IOException {
        super(clientSocket, protocol);
        this.client = client;
        ((RelayNodeJMProtocolImpl) this.protocol).setClient(this.client);
    }

    @Override
    public void run() {
        try {
            thread = new Thread(new ReceiverThread<WorkerRunnableSC>(this));
            thread.start();
            do {
                if (getToSend() != null){
                    System.out.println("WorkRunnable Thread #"+Thread.currentThread().getId() +" WorkRunnableSC - to send : " + toSend.toString());
                    sendMessage(toSend);
                }
                Thread.sleep(100);
            } while (true);
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void handleMessage(Object msg) {
        switch (msg.toString()) {
            case NetConst.CONNECTED :
                break;
            case NetConst.CONNECTION_REQUEST:
                setToSend(NetConst.CONNECTED);
                break;
            default:
                this.client.getServer().getAwaitingAnswers().add(this);
                setToSend(this.protocol.processInput(msg));
                break;
        }
    }

}
