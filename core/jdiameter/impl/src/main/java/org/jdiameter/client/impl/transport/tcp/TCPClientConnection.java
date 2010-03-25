package org.jdiameter.client.impl.transport.tcp;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
// FIXME : requires JDK6 : import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPClientConnection implements IConnection {
  
  private static Logger log = LoggerFactory.getLogger(TCPClientConnection.class);

  private final long createdTime;
  private TCPTransportClient client;

  protected static enum EventType {
    CONNECTED, DISCONNECTED, MESSAGE_RECEIVED, DATA_EXCEPTION
  }

  protected static class Event {
    EventType type;
    ByteBuffer message;
    Exception exception;

    Event(EventType type) {
      this.type = type;
    }

    Event(EventType type, Exception exception) {
      this(type);
      this.exception = exception;
    }

    Event(EventType type, ByteBuffer message) {
      this(type);
      this.message = message;
    }
  }

  //FIXME : requires JDK6 : protected LinkedBlockingDeque<Event> buffer = new LinkedBlockingDeque<Event>(64);
  protected LinkedBlockingQueue<Event> buffer = new LinkedBlockingQueue<Event>(64);
  protected IMessageParser parser;
  protected Lock lock = new ReentrantLock();
  protected ConcurrentLinkedQueue<IConnectionListener> listeners = new ConcurrentLinkedQueue<IConnectionListener>();

  // Cached value for connection key
  private String cachedKey = null;

  protected TCPClientConnection(IConcurrentFactory concurrentFactory, IMessageParser parser) {
    this.createdTime = System.currentTimeMillis();
    this.parser = parser;
    client = new TCPTransportClient(concurrentFactory, this);
  }

  public TCPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, Socket socket,
    IMessageParser parser, String ref) throws Exception {
    this(concurrentFactory, parser);
    client = new TCPTransportClient(concurrentFactory, this);
    client.initialize(socket);
    client.start();
  }

  public TCPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref) {
    this(concurrentFactory, parser);
    client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
    client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
  }

  public TCPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener,
      IMessageParser parser, String ref) {
    this(concurrentFactory, parser);
    client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
    client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
    listeners.add(listener);
  }

  public long getCreatedTime() {
    return createdTime;
  }

  public void connect() throws TransportException {
    try {
      getClient().initialize();
      getClient().start();
    } catch (IOException e) {
      throw new TransportException("Cannot init transport: ", TransportError.NetWorkError, e);
    } catch (Exception e) {
      throw new TransportException("Cannot init transport: ", TransportError.Internal, e);
    }
  }

  public void disconnect() throws InternalError {
    try {
      if (getClient() != null) {
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
    } finally {
      parser = null;
      buffer.clear();
      remAllConnectionListener();
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

  public void addConnectionListener(IConnectionListener listener) {
    lock.lock();
    try {
      listeners.add(listener);
      if (buffer.size() != 0) {
        for (Event e : buffer) {
          try {
            onEvent(e);
          } catch (AvpDataException e1) {
          }
        }
        buffer.clear();
      }
    } finally {
      lock.unlock();
    }
  }

  public void remAllConnectionListener() {
    lock.lock();
    try {
      listeners.clear();
    } finally {
      lock.unlock();
    }
  }

  public void remConnectionListener(IConnectionListener listener) {
    lock.lock();
    try {
      listeners.remove(listener);
    } finally {
      lock.unlock();
    }
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;
  }

  public String getKey() {
    if(this.cachedKey == null) {
      this.cachedKey = new StringBuffer("aaa://").append(getRemoteAddress().getHostName()).append(":").append(getRemotePort()).toString();
    }

    return this.cachedKey;
  }

  protected void onDisconnect() throws AvpDataException {
    onEvent(new Event(EventType.DISCONNECTED));
  }

  protected void onMessageReveived(ByteBuffer message) throws AvpDataException {
    if (log.isDebugEnabled()) {
      log.debug("Received message");
    }
    onEvent(new Event(EventType.MESSAGE_RECEIVED, message));
  }

  protected void onAvpDataException(AvpDataException e) {
    try {
      onEvent(new Event(EventType.DATA_EXCEPTION, e));
    } catch (AvpDataException e1) {
    }
  }

  protected void onConnected() {
    try {
      onEvent(new Event(EventType.CONNECTED));
    } catch (AvpDataException e1) {
    }
  }

  protected void onEvent(Event event) throws AvpDataException {
    lock.lock();
    try {
      if (processBufferedMessages(event)) {
        for (IConnectionListener listener : listeners) {
          switch (event.type) {
            case CONNECTED:
              listener.connectionOpened(getKey());
              break;
            case DISCONNECTED:
              listener.connectionClosed(getKey(), null);
              break;
            case MESSAGE_RECEIVED:
              listener.messageReceived(getKey(), parser.createMessage(event.message));
              break;
            case DATA_EXCEPTION:
              listener.internalError(getKey(), null, new TransportException("Avp Data Exception:", 
                  TransportError.ReceivedBrokenMessage, event.exception));
              break;
          }
        }
      }
    } finally {
      lock.unlock();
    }
  }

  protected boolean processBufferedMessages(Event event) throws AvpDataException {
    if (listeners.size() == 0) {
      try {
        buffer.add(event);
      } catch (IllegalStateException e) {
        // FIXME : requires JDK6 : buffer.removeLast();
        Event[] tempBuffer = buffer.toArray(new Event[buffer.size()]);
        buffer.remove(tempBuffer[tempBuffer.length-1]);
        buffer.add(event);
      }
      return false;
    } else {
      return true;
    }
  }
}