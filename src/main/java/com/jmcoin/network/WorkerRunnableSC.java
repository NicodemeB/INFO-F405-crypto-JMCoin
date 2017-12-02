package com.jmcoin.network;

import java.io.IOException;
import java.net.Socket;

public class WorkerRunnableSC extends WorkerRunnable {
    public Client getClient() {
        return client;
    }

    public void setClient(ClientSC client) {
        this.client = client;
    }

    ClientSC client;
    public WorkerRunnableSC(Socket clientSocket, JMProtocolImpl<? extends Peer> protocol, ClientSC client) throws IOException {
        super(clientSocket, protocol);
        setClient(client);
        ((RelayNodeJMProtocolImpl) this.protocol).setClient(getClient());
    }

    @Override
    public void run() {
        try {
            new Thread( new ReceiverThread<WorkerRunnableSC>(this)).start();

            do {
                if (getToSend() != null){
                    System.out.println("Thread #"+Thread.currentThread().getId() +" WorkRunnableSC - to send : " + toSend.toString());
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

//    protected void handleMessage(Object msg) {
//		switch (msg.toString()) {
//        case NetConst.CONNECTED :
//            break;
//        case NetConst.CONNECTION_REQUEST:
//            setToSend(NetConst.CONNECTED);
//            break;
//        default:
//            setToSend(this.protocol.processInput(msg));
//            break;
//    }
//}

}
