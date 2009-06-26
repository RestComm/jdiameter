package org.jdiameter.server.impl.io.tcp;

import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.transport.tcp.TCPClientConnection;
import org.jdiameter.server.api.io.INetWorkConnectionListener;
import org.jdiameter.server.api.io.INetWorkGuard;
import org.jdiameter.server.impl.helpers.Loggers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetWorkGuard implements INetWorkGuard, Runnable {

    protected IMessageParser parser;
    protected int port;
    protected Logger logger = Logger.getLogger(Loggers.NetWork.fullName());
    protected CopyOnWriteArrayList<INetWorkConnectionListener> listeners = new CopyOnWriteArrayList<INetWorkConnectionListener>();
    protected boolean isWork = false;
    protected Selector selector;
    protected ServerSocket serverSocket;

    private Thread thread = new Thread(this);

    public NetWorkGuard(InetAddress inetAddress, int port, IMessageParser parser) throws Exception {
        this.port = port;
        this.parser = parser;
        //
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            serverSocket = ssc.socket();
            serverSocket.bind( new InetSocketAddress( inetAddress, port ) );
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            isWork = true;
            logger.info("Open server socket: " + serverSocket);
            thread.start();
        } catch(Exception exc) {
            destroy();
            throw new Exception(exc);
        }
    }

    public void run() {
        try {
            while (isWork) {
                int num = selector.select();
                if (num == 0) continue;
                Set keys = selector.selectedKeys();
                for (Object key1 : keys) {
                    SelectionKey key = (SelectionKey) key1;
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        try {
                            Socket s = serverSocket.accept();
                            if(logger.isLoggable(Level.INFO))
                        	{
                        		logger.info("Open incomming connection " + s.toString());
                        	}
                            TCPClientConnection client = new TCPClientConnection(null, s, parser, null);
                            for (INetWorkConnectionListener listener : listeners) {
                                listener.newNetWorkConnection(client);
                            }
                        } catch (Exception e) {
                        	if(logger.isLoggable(Level.SEVERE))
                        	{
                        		logger.log(Level.SEVERE, "Can not cteate incoming connection", e);
                        	}
                        }
                    }
                }
                keys.clear();
            }
        } catch(Exception exc) {
        	if(logger.isLoggable(Level.SEVERE))
        	{
        		logger.log(Level.SEVERE, "Server socket stopped", exc);
        	}
        }
    }

    public void addListener(INetWorkConnectionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void remListener(INetWorkConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String toString() {
        return "NetworkGuard:" + (serverSocket != null ? serverSocket.toString() : "closed");
    }

    public void destroy() {
        try {
            isWork = false;
            try {
                if (thread != null) {
                    thread.join( 1*1000 );
                    if (thread.isAlive())
                        thread.interrupt();
                }
            } catch (InterruptedException e) {
            }
            if (serverSocket!= null)
                serverSocket.close();
            thread = null;
            serverSocket = null;
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }
}
