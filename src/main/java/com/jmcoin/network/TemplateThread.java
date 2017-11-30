package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class TemplateThread implements Runnable{
	
	protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected Socket socket;
    protected Object toSend;
    protected boolean sendFlag;
    protected JMProtocolImpl<? extends Peer> protocol;
    
    protected abstract void handleMessage(Object msg);
    
    public TemplateThread(JMProtocolImpl<? extends Peer> protocol) {
    	this.protocol = protocol;
	}
    
    public void close () throws IOException {
        in.close();
        out.close();
        socket.close();
    }
    
    public Object readMessage() throws IOException, ClassNotFoundException {
        return  in.readObject();
    }
    
    public ObjectInputStream getIn() {
		return in;
	}
    
    synchronized void sendMessage(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
        toSend = null;
        sendFlag = false;
    }

    public synchronized Object getToSend(){
        return toSend;
    }
    
    public synchronized void setToSend(Object ts){
        toSend = ts;
        sendFlag = true;
    }
}
