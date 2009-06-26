package org.jdiameter.client.impl.transport.tcp;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.io.NotInitializedException;
import org.jdiameter.client.impl.helpers.Loggers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

class TCPTransportClient implements Runnable {

    private TCPClientConnection parentConnection;

    public static final int DEFAULT_BUFFER_SIZE  = 1024;
    public static final int DEFAULT_STORAGE_SIZE = 2048;

    protected boolean stop = false;
    protected Thread selfThread;

    protected int bufferSize = DEFAULT_BUFFER_SIZE;
    protected ByteBuffer buffer = ByteBuffer.allocate(this.bufferSize);

    protected InetSocketAddress destAddress;
    protected InetSocketAddress origAddress;

    protected SocketChannel socketChannel;
    protected Lock lock = new ReentrantLock();

    protected int storageSize = DEFAULT_STORAGE_SIZE;
    protected ByteBuffer storage = ByteBuffer.allocate(storageSize);
    protected Logger logger = Logger.getLogger(Loggers.Transport.fullName());


    TCPTransportClient() {
    }

    /**
     * Default constructor
     *
     * @param parenConnection connection created this transport
     */
    TCPTransportClient(TCPClientConnection parenConnection) {
        this.parentConnection = parenConnection;
    }

    public void initialize() throws IOException, NotInitializedException {
        if (destAddress == null) {
            throw new NotInitializedException("Destination address is not set");
        }
        socketChannel = SelectorProvider.provider().openSocketChannel();
        if (origAddress != null) {
            socketChannel.socket().bind(origAddress);
        }
        socketChannel.connect(destAddress);
        socketChannel.configureBlocking(true);
        getParent().onConnected();
    }

    public TCPClientConnection getParent() {
        return parentConnection;
    }

    public void initialize(Socket socket) throws IOException, NotInitializedException  {
        socketChannel = socket.getChannel();
        socketChannel.configureBlocking(true);
        destAddress = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
    }

    public void start() throws Exception {
     if(logger.isLoggable(Level.FINEST))
     {
        logger.log(Level.FINEST, "Starting transport");
     }
        if (socketChannel == null)
            throw new NotInitializedException("Transport is not initialized");
        if (!socketChannel.isConnected())
            throw new NotInitializedException("Socket channel is not connected");
        if (getParent() == null)
            throw new NotInitializedException("No parent connection is set is set");
        if (selfThread == null || !selfThread.isAlive()) {
            selfThread = new Thread(this);
        }
        selfThread.start();
    }    

    public void run() {
        logger.log(Level.FINEST, "Transport is started");
        try {
            while (!stop) {
                int dataLength = socketChannel.read(buffer);
                if (dataLength == -1) {
                    if (socketChannel.isConnected()) {
                        Thread.sleep(10);
                        continue;
                    } else {
                        break;
                    }
                }
                buffer.flip();
                byte[] data = new byte[buffer.limit()];
                buffer.get(data);
                append(data);
                buffer.clear();
            }
        } catch (ClosedByInterruptException e) {
        	if(logger.isLoggable(Level.SEVERE))
     		{
            	logger.log(Level.SEVERE, "Transport exception ", e);
            }
        } catch (InterruptedException e) {
	        if(logger.isLoggable(Level.SEVERE))
    	 	{
        	  	logger.log(Level.SEVERE, "Transport exception ", e);
            }
        } catch (AsynchronousCloseException e) {
	        if(logger.isLoggable(Level.SEVERE))
    		 {
        	    logger.log(Level.SEVERE, "Transport exception ", e);
        	 }
        } catch (Throwable e) {
        	if(logger.isLoggable(Level.SEVERE))
    	 	{
            	logger.log(Level.SEVERE, "Transport exception ", e);
            }
        } finally {
            //
            if (!stop) {
                try {
                    clearBuffer();
                    if (socketChannel != null && socketChannel.isOpen())
                        socketChannel.close();                     
                    getParent().onDisconnect();
                } catch (Exception e) {
                	if(logger.isLoggable(Level.SEVERE))
		    	 	{
        	            logger.log(Level.SEVERE, "Error", e);                    
        	        }
                }
            }
            stop = false;
            //
            logger.log(Level.INFO, "Read thread is stopped");
        }
    }

    public void stop() throws Exception {
        logger.log(Level.FINEST, "Stopping transport");
        stop = true;
        if (socketChannel != null && socketChannel.isOpen())
            socketChannel.close();
        if (selfThread != null)
            selfThread.join(100);
        clearBuffer();
        logger.log(Level.FINEST, "Transport is stopped");
    }

    public void release() throws Exception {
        stop();
        destAddress = null;
    }

    public void setBufferSize(int size) {
        bufferSize = size;
        buffer = ByteBuffer.allocate(this.bufferSize);
        logger.log(Level.FINEST, "Buffer size is set to " + this.bufferSize);
    }

    private void clearBuffer() throws IOException {
        bufferSize = DEFAULT_BUFFER_SIZE;
        buffer = ByteBuffer.allocate(bufferSize);
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public InetSocketAddress getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(InetSocketAddress address) {
        destAddress = address;
        if(logger.isLoggable(Level.FINEST))
       	{
        	logger.log(Level.FINEST, "Destination address is set to " + destAddress.getHostName() + ":" + destAddress.getPort());
        }
    }

    public void setOrigAddress(InetSocketAddress address) {
        origAddress = address;
    }

    public void sendMessage(ByteBuffer bytes) throws IOException {
        int rc;
        lock.lock();   
        try {
            rc = socketChannel.write(bytes);
        } catch (Exception e) {
            logger.log(Level.INFO, "Can not send message", e);
            throw new IOException("Error while sending message: " + e);
        } finally {
            lock.unlock();
        }
        if (rc == -1)
            throw new IOException("Connection closed");
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Transport to ");
        if (this.destAddress != null) {
            buffer.append(this.destAddress.getHostName());
            buffer.append(":");
            buffer.append(this.destAddress.getPort());
        } else {
            buffer.append("null");
        }
        buffer.append("@");
        buffer.append(super.toString());
        return buffer.toString();
    }

    boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

    void append(byte[] data) {
        if (storage.position() + data.length >= storage.capacity()) {
            ByteBuffer tmp = ByteBuffer.allocate(storage.limit() + data.length * 2);
            byte[] tmpData = new byte[storage.position()];
            storage.flip();
            storage.get(tmpData);
            tmp.put(tmpData);
            storage = tmp;
            if(logger.isLoggable(Level.FINE))
            {
            	logger.log(Level.FINE, "Increase storage size. Current size is", storage.array().length);
            }
        }

        try {
          storage.put(data);
        }
        catch (BufferOverflowException boe) {
          logger.log(Level.WARNING, "Buffer overflow occured", boe);
        }
        boolean messageReseived;
        do {
            messageReseived = seekMessage(storage);
        } while (messageReseived);
    }

    private boolean seekMessage(ByteBuffer localStorage) {
        if (storage.position() == 0)
            return false;

        storage.flip();
        int tmp = localStorage.getInt();
        localStorage.position(0);

        byte vers = (byte) (tmp >> 24);
        if (vers != 1)
            return false;
        int dataLength = (tmp & 0xFFFFFF);

        if (localStorage.limit() < dataLength) {
            localStorage.position(localStorage.limit());
            localStorage.limit(localStorage.capacity());
            return false;
        }

        byte[] data = new byte[dataLength];
        localStorage.get(data);
        localStorage.position(dataLength);
        localStorage.compact();

        try {
            getParent().onMessageReveived(ByteBuffer.wrap(data));
        } catch (AvpDataException e) {
            logger.log(Level.INFO, "Garbage was received from server");
            storage.clear();
            getParent().onAvpDataException(e);
        }
        return true;
    }

    public int getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(int size) {
        storageSize = size;
    }

}
