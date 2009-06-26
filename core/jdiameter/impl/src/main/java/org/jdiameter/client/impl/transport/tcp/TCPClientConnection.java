package org.jdiameter.client.impl.transport.tcp;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TCPClientConnection implements IConnection {

    private final long createdTime;
    private TCPTransportClient client;
    protected IMessageParser parser;
    protected final Object zero = new Object();
    protected ConcurrentLinkedQueue<IConnectionListener> listeners = new ConcurrentLinkedQueue<IConnectionListener>();

    protected TCPClientConnection (IMessageParser parser) {
        this.createdTime = System.currentTimeMillis();
        this.parser = parser;
        client = new TCPTransportClient(this);
    }

    public TCPClientConnection(Configuration config, Socket socket, IMessageParser parser, String ref) throws Exception {
        this(parser);
        client = new TCPTransportClient(this);
        client.initialize(socket);
        client.start();
    }

    public TCPClientConnection(Configuration config, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref) {
        this(parser);
        client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
        client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
        this.parser = parser;
    }

    public TCPClientConnection(Configuration config, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, IMessageParser parser, String ref) {
        this(parser);
        client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
        client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
        listeners.add(listener);
        this.parser = parser;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void connect() throws TransportException {
        try {
            getClient().initialize();
            getClient().start();
        } catch(IOException e) {
            throw new TransportException("Cannot init transport: ", TransportError.NetWorkError, e);            
        } catch (Exception e) {
            throw new TransportException("Cannot init transport: ", TransportError.Internal, e);
        }
    }

    public void disconnect() throws InternalError {
        try {
          if(getClient() != null) {
            getClient().stop();
          }
        } catch (Exception e) {
            throw new InternalError("Error while stopping transport: " + e.getMessage());
        }
    }

    public void release() throws IOException {
        try {
          if (getClient() != null) {
            getClient().release();
          }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        finally {
          parser = null;
          listeners = null;
        }
    }

    public void sendMessage(IMessage message) throws TransportException, OverloadException {
        try {
          if (getClient() != null) {
            getClient().sendMessage(parser.encodeMessage(message));
          }
        } catch (Exception e) {
            throw new TransportException("Cannot send message: ", TransportError.FailedSendMessage, e);
        }
    }

    protected TCPTransportClient getClient() {
        return client;
    }

    public boolean isNetworkInitiated() {
        return false;
    }

    public boolean isConnected() {
        return getClient() != null && getClient().isConnected();
    }

    public InetAddress getRemoteAddress() {
        return getClient().getDestAddress().getAddress();
    }

    public int getRemotePort() {
        return getClient().getDestAddress().getPort();
    }

    public void addConnectionListener(IConnectionListener connectionListener) {
        if (listeners != null) {
            listeners.add(connectionListener);
            notifyListeners();
        }
    }

    public void remConnectionListener(IConnectionListener connectionListener) {
        if (listeners != null) {
            listeners.remove(connectionListener);
        }
    }

    public void remAllConnectionListener() {
        listeners.clear();
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null; 
    }

    public String getKey() {
        return "aaa://"+ getRemoteAddress().getHostName() + ":" + getRemotePort();
    }

    void onDisconnect() {
        if (listeners != null) {
            waitListeners();
            for (IConnectionListener listener : this.listeners) {
                listener.connectionClosed(getKey(), null);
            }
        }
    }

    void onMessageReveived(ByteBuffer message) throws AvpDataException {
        if (listeners != null) {
            waitListeners();
            for (IConnectionListener listener : listeners) {
                listener.messageReceived(getKey(), parser.createMessage(message));
            }
        }
    }


    void onAvpDataException(AvpDataException e) {
        if (listeners != null) {
            waitListeners();
            for (IConnectionListener listener : listeners) {
                listener.internalError(getKey(), null, new TransportException("Avp Data Exception:", TransportError.ReceivedBrokenMessage, e));
            }
        }
    }

    void onConnected() {
        if (listeners != null) {
            waitListeners();
            for (IConnectionListener listener : this.listeners)
                listener.connectionOpened(getKey());
        }
    }

    void notifyListeners() {
        if (listeners != null && listeners.size() == 0)
            synchronized(zero) {
                zero.notifyAll();
            }
    }

    void waitListeners() {        
        if (listeners != null && listeners.size() == 0)
            synchronized(zero) {
                try {
                    zero.wait(500);
                } catch (InterruptedException e) {}
            }
    }
}
