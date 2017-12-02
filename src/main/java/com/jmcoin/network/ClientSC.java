package com.jmcoin.network;

import java.io.IOException;

public class ClientSC extends Client{


    private MultiThreadedServerClient server;

    public MultiThreadedServerClient getServer() {
        return server;
    }

    public void setServer(MultiThreadedServerClient server) {
        this.server = server;
    }



    public ClientSC(int port, String host, JMProtocolImpl<? extends Peer> protocol, MultiThreadedServerClient srv) throws IOException {
        super(port, host, protocol);
        setServer(srv);
    }

    @Override
    public void receiveAndTreatMessage() throws InterruptedException {
        try {
            do {
                if (getToSend() != null) {
                    System.out.println("Thread #"+Thread.currentThread().getId() +" ClientSC - to send : " + getToSend().toString());
                    if (getToSend().toString().equals(protocol.craftMessage(NetConst.STOP_MINING, null))){
                        server.not();
                    }else {
                        sendMessage(getToSend());
                    }
                }
                Thread.sleep(100);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
                System.out.println("close");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void handleMessage(Object msg) {
        System.out.println("SUCE MA GROSSE QUEUE");
        switch (msg.toString()) {
            case NetConst.CONNECTED :
                //TODO do something
                break;
            case NetConst.CONNECTION_REQUEST:
                break;
            case "54$null$#" :
                System.out.println("server.not()");
                server.not();
                break;

            default:

                break;
        }
    }
}
