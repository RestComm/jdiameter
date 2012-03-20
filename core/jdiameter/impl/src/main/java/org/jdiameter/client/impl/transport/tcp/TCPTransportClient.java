/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl.transport.tcp;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.io.NotInitializedException;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TCPTransportClient implements Runnable {

  private TCPClientConnection parentConnection;
  private IConcurrentFactory concurrentFactory;

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

  private String socketDescription = null;

  private static final Logger logger = LoggerFactory.getLogger(TCPTransportClient.class);

  public TCPTransportClient() {
  }

  /**
   * Default constructor
   *
   * @param concurrentFactory factory for create threads
   * @param parenConnection connection created this transport
   */
  TCPTransportClient(IConcurrentFactory concurrentFactory, TCPClientConnection parenConnection) {
    this.parentConnection = parenConnection;
    this.concurrentFactory = concurrentFactory;
  }

  /**
   *  Network init socket 
   */
  public void initialize() throws IOException, NotInitializedException {
    logger.debug("Initialising TCPTransportClient. Origin address is [{}] and destination address is [{}]", origAddress, destAddress);
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
    logger.debug("Initialising TCPTransportClient for a socket on [{}]", socket);
    socketDescription = socket.toString();
    socketChannel = socket.getChannel();
    socketChannel.configureBlocking(true);
    destAddress = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
  }

  public void start() throws NotInitializedException {
    // for client
    if(socketDescription == null && socketChannel != null) {
      socketDescription = socketChannel.socket().toString();
    }
    logger.debug("Starting transport. Socket is {}", socketDescription);

    if (socketChannel == null) {
      throw new NotInitializedException("Transport is not initialized");
    }
    if (!socketChannel.isConnected()) {
      throw new NotInitializedException("Socket channel is not connected");
    }
    if (getParent() == null) {
      throw new NotInitializedException("No parent connection is set is set");
    }
    if (selfThread == null || !selfThread.isAlive()) {
      selfThread = concurrentFactory.getThread("TCPReader", this);
    }

    if (!selfThread.isAlive()) {
      selfThread.setDaemon(true);
      selfThread.start();
    }
  }

  public void run() {
    logger.debug("Transport is started. Socket is [{}]", socketDescription);
    try {
      while (!stop) {
        int dataLength = socketChannel.read(buffer);
        logger.debug("Just read [{}] bytes on [{}]", dataLength, socketDescription);
        if (dataLength == -1) {
          break;
        }
        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        append(data);
        buffer.clear();
      }
    }
    catch (ClosedByInterruptException e) {
      logger.debug("Transport exception ", e);
    }
    catch (AsynchronousCloseException e) {
      logger.debug("Transport exception ", e);
    }
    catch (Throwable e) {
      logger.debug("Transport exception ", e);
    }
    finally {
      try {
        clearBuffer();
        if (socketChannel != null && socketChannel.isOpen()) {
          socketChannel.close();
        }
        getParent().onDisconnect();
      }
      catch (Exception e) {
        logger.debug("Error", e);                    
      }
      stop = false;
      logger.info("Read thread is stopped for socket [{}]", socketDescription);
    }
  }

  public void stop() throws Exception {
    logger.debug("Stopping transport. Socket is [{}]", socketDescription);
    stop = true;
    if (socketChannel != null && socketChannel.isOpen()) {
      socketChannel.close();
    }
    if (selfThread != null) {
      selfThread.join(100);
    }
    clearBuffer();
    logger.debug("Transport is stopped. Socket is [{}]", socketDescription);
  }

  public void release() throws Exception {
    stop();
    destAddress = null;
  }

  private void clearBuffer() throws IOException {
    bufferSize = DEFAULT_BUFFER_SIZE;
    buffer = ByteBuffer.allocate(bufferSize);
  }

  public InetSocketAddress getDestAddress() {
    return this.destAddress;
  }

  public void setDestAddress(InetSocketAddress address) {
	  this.destAddress = address;
    if(logger.isDebugEnabled()) {
      logger.debug("Destination address is set to [{}] : [{}]", destAddress.getHostName(), destAddress.getPort());
    }
  }

  public void setOrigAddress(InetSocketAddress address) {
    this.origAddress = address;
    if(logger.isDebugEnabled()) {
        logger.debug("Origin address is set to [{}] : [{}]", origAddress.getHostName(), origAddress.getPort());
      }
  }
  
  public InetSocketAddress getOrigAddress()
  {
	  return this.origAddress;
  }

  public void sendMessage(ByteBuffer bytes) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("About to send a byte buffer of size [{}] over the TCP nio socket [{}]", bytes.array().length, socketDescription);
    }
    int rc;
    lock.lock();   
    try {
      rc = socketChannel.write(bytes);
    }
    catch (Exception e) {
      logger.debug("Unable to send message", e);
      throw new IOException("Error while sending message: " + e);
    }
    finally {
      lock.unlock();
    }
    if (rc == -1) {
      throw new IOException("Connection closed");
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Sent a byte buffer of size [{}] over the TCP nio socket [{}]", bytes.array().length, socketDescription);
    }
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Transport to ");
    if (this.destAddress != null) {
      buffer.append(this.destAddress.getHostName());
      buffer.append(":");
      buffer.append(this.destAddress.getPort());
    }
    else {
      buffer.append("null");
    }
    buffer.append("@");
    buffer.append(super.toString());
    return buffer.toString();
  }

  boolean isConnected() {
    return socketChannel != null && socketChannel.isConnected();
  }

  /**
   * Adds data to storage
   *
   * @param data data to add
   */
  private void append(byte[] data) {
    if (storage.position() + data.length >= storage.capacity()) {
      ByteBuffer tmp = ByteBuffer.allocate(storage.limit() + data.length * 2);
      byte[] tmpData = new byte[storage.position()];
      storage.flip();
      storage.get(tmpData);
      tmp.put(tmpData);
      storage = tmp;
      logger.warn("Increase storage size. Current size is {}", storage.array().length);
    }

    try {
      storage.put(data);
    }
    catch (BufferOverflowException boe) {
      logger.error("Buffer overflow occured", boe);
    }
    boolean messageReseived;
    do {
      messageReseived = seekMessage(storage);
    } while (messageReseived);
  }

  private boolean seekMessage(ByteBuffer localStorage) {
    if (storage.position() == 0) {
      return false;
    }

    storage.flip();
    int tmp = localStorage.getInt();
    localStorage.position(0);

    byte vers = (byte) (tmp >> 24);
    if (vers != 1) {
      return false;
    }
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
      getParent().onMessageReceived(ByteBuffer.wrap(data));
    }
    catch (AvpDataException e) {
      logger.debug("Garbage was received. Discarding.");
      storage.clear();
      getParent().onAvpDataException(e);
    }
    return true;
  }

}
