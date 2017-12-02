package com.jmcoin.network;

import java.io.IOException;

public class ClientSC extends Client{
    //INFO - THIS IS THE CLIENT PART OF THE RELAY

    private MultiThreadedServerClient server;

    private Thread t;

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }

    public MultiThreadedServerClient getServer() {
        return server;
    }

    public void setServer(MultiThreadedServerClient server) {
        this.server = server;
    }



    public ClientSC(int port, String host, JMProtocolImpl<? extends Peer> protocol, MultiThreadedServerClient srv) throws IOException {
        super(port, host, protocol);
        setServer(srv);
        t = new Thread(new ReceiverThread<ClientSC>(this));
        t.start();
    }

    @Override
    public void receiveAndTreatMessage() throws InterruptedException {
        try {
            do {
                if (getToSend() != null) {
                    System.out.println("Thread #"+Thread.currentThread().getId() +" ClientSC - to send : " + getToSend().toString());
                    sendMessage(getToSend());

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
                setToSend(this.protocol.processInput(msg));
                break;
        }
    }
}
