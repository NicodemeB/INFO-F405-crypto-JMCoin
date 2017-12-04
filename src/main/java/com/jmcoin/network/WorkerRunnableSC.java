package com.jmcoin.network;

import java.io.IOException;
import java.net.Socket;

public class WorkerRunnableSC extends WorkerRunnable {

    private ClientSC client;
    private Thread rt;


    public Thread getRt() {
        return rt;
    }

    public void setRt(Thread rt) {
        this.rt = rt;
    }

    public ClientSC getClient() {
        return client;
    }

    public void setClient(ClientSC client) {
        this.client = client;
    }


    public WorkerRunnableSC(Socket clientSocket, JMProtocolImpl<? extends Peer> protocol, ClientSC client) throws IOException {
        super(clientSocket, protocol);
        setClient(client);
        ((RelayNodeJMProtocolImpl) this.protocol).setClient(getClient());
    }

    @Override
    public void run() {
        try {
            rt = new Thread(new ReceiverThread<WorkerRunnableSC>(this));
            rt.start();
//            ((ReceiverThread) rt).
            do {
                if (getToSend() != null){
                    System.out.println("WorkRunnable Thread #"+Thread.currentThread().getId() +" WorkRunnableSC - to send : " + toSend.toString());
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

    @Override
    protected void handleMessage(Object msg) {
        switch (msg.toString()) {
            case NetConst.CONNECTED :
                break;
            case NetConst.CONNECTION_REQUEST:
                setToSend(NetConst.CONNECTED);
                break;
            default:
                getClient().getServer().getAwaitingAnswers().add(this);
                setToSend(this.protocol.processInput(msg));
                break;
        }
    }

}
