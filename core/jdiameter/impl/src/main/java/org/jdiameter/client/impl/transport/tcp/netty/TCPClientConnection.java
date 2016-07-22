/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, TeleStax Inc. and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.jdiameter.client.impl.transport.tcp.netty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
// FIXME : requires JDK6 : import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

import io.netty.channel.Channel;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class TCPClientConnection implements IConnection {

  private static Logger logger = LoggerFactory.getLogger(TCPClientConnection.class);

  private final long createdTime = System.currentTimeMillis();
  private TCPTransportClient client;
  // FIXME : requires JDK6 : protected LinkedBlockingDeque<Event> buffer = new
  // LinkedBlockingDeque<Event>(64);
  private LinkedBlockingQueue<Event> buffer = new LinkedBlockingQueue<Event>(64);
  private IMessageParser parser;
  private Lock lock = new ReentrantLock();
  private ConcurrentLinkedQueue<IConnectionListener> listeners = new ConcurrentLinkedQueue<IConnectionListener>();

  // Cached value for connection key
  private String cachedKey = null;

  public TCPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref) {
    this.parser = parser;
    this.client = new TCPTransportClient(this, parser, new InetSocketAddress(remoteAddress, remotePort),
        new InetSocketAddress(localAddress, localPort));

  }

  public TCPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, IMessageParser parser,
      String ref) {
    this(config, concurrentFactory, remoteAddress, remotePort, localAddress, localPort, parser, ref);
    listeners.add(listener);
  }

  public TCPClientConnection(Channel channel, IMessageParser parser) {
    this.parser = parser;
    this.client = new TCPTransportClient(this, parser, channel);
  }

  public long getCreatedTime() {
    return createdTime;
  }

  public void connect() throws TransportException {
    try {
      this.client.start();
    } catch (Exception e) {
      throw new TransportException("Cannot init transport: ", TransportError.Internal, e);
    }
  }

  public void disconnect() throws InternalError {
    logger.debug("In disconnect for [{}]", this.getKey());
    try {
      if (this.client != null) {
        this.client.stop();
      }
    } catch (Exception e) {
      throw new InternalError("Error while stopping transport: " + e.getMessage());
    }
  }

  public void release() throws IOException {
    logger.debug("In release for [{}]", this.getKey());
    try {
      if (this.client != null) {
        this.client.release();
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
      if (this.client != null) {
        this.client.sendMessage(message);
      }
    } catch (Exception e) {
      throw new TransportException("Cannot send message: ", TransportError.FailedSendMessage, e);
    }
  }

  protected TCPTransportClient getClient() {
    return this.client;
  }

  public boolean isNetworkInitiated() {
    return false;
  }

  public boolean isConnected() {
    return this.client != null && this.client.isConnected();
  }

  public InetAddress getRemoteAddress() {
    return this.client.getDestAddress().getAddress();
  }

  public int getRemotePort() {
    return this.client.getDestAddress().getPort();
  }

  public void addConnectionListener(IConnectionListener listener) {
    lock.lock();
    try {
      listeners.add(listener);
      if (buffer.size() != 0) {
        for (Event e : buffer) {
          try {
            logger.debug("Processing event from buffer");
            onEvent(e);
          } catch (AvpDataException e1) {
            // ignore
          }
        }
        buffer.clear();
      }
    } finally {
      lock.unlock();
    }
  }

  public void remAllConnectionListener() {
    logger.debug("Waiting to get lock in order to remove all listeners");
    lock.lock();
    try {
      logger.debug("Removing all listeners on [{}]", this.getKey());
      listeners.clear();
    } finally {
      lock.unlock();
    }
  }

  public void remConnectionListener(IConnectionListener listener) {
    lock.lock();
    try {

      logger.debug("Removing listener [{}] on [{}]", listener.getClass().getName(), this.getKey());
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
    if (this.cachedKey == null) {
      this.cachedKey = new StringBuffer("aaa://").append(getRemoteAddress().getHostName()).append(":").append(getRemotePort())
          .toString();
    }

    return this.cachedKey;
  }

  protected void onDisconnect() throws AvpDataException {
    onEvent(new Event(EventType.DISCONNECTED));
  }

  protected void onMessageReceived(IMessage message) throws AvpDataException {
    onEvent(new Event(EventType.MESSAGE_RECEIVED, message));
  }

  protected void onAvpDataException(AvpDataException e) {
    try {
      onEvent(new Event(EventType.DATA_EXCEPTION, e));
    } catch (AvpDataException e1) {
      // ignore
    }
  }

  protected void onConnected() {
    try {
      onEvent(new Event(EventType.CONNECTED));
    } catch (AvpDataException e1) {
      // ignore
    }
  }

  protected void onEvent(Event event) throws AvpDataException {

    logger.debug("In onEvent for connection [{}]. Getting lock", this.getKey());
    lock.lock();

    logger.debug("Got lock");
    try {
      if (processBufferedMessages(event)) {
        for (IConnectionListener listener : listeners) {

          if (logger.isDebugEnabled()) {
            logger.debug("Passing event to listener. Event type is [{}]", event.type.toString());
          }
          switch (event.type) {
            case CONNECTED:
              listener.connectionOpened(getKey());
              break;
            case DISCONNECTED:
              listener.connectionClosed(getKey(), null);
              break;
            case MESSAGE_RECEIVED:
              listener.messageReceived(getKey(), event.message);
              break;
            case DATA_EXCEPTION:
              listener.internalError(getKey(), null,
                  new TransportException("Avp Data Exception:", TransportError.ReceivedBrokenMessage, event.exception));
              break;
          }
        }
      }
    } finally {
      logger.debug("Releasing lock and finished onEvent for connection [{}]", this.getKey());
      lock.unlock();
    }
  }

  protected boolean processBufferedMessages(Event event) throws AvpDataException {
    if (listeners.size() == 0) {

      logger.debug("listeners.size() == 0 on connection [{}]", this.getKey());
      try {
        buffer.add(event);
      } catch (IllegalStateException e) {
        logger.debug("Got IllegalStateException in processBufferedMessages");
        // FIXME : requires JDK6 : buffer.removeLast();
        Event[] tempBuffer = buffer.toArray(new Event[buffer.size()]);
        buffer.remove(tempBuffer[tempBuffer.length - 1]);
        buffer.add(event);
      }

      logger.debug("processBufferedMessages is returning false");
      return false;
    } else {
      logger.debug("processBufferedMessages is returning true on connection [{}] as there are listeners", getKey());
      return true;
    }
  }

  // ------------------ helper classes ------------------------
  private enum EventType {
    CONNECTED, DISCONNECTED, MESSAGE_RECEIVED, DATA_EXCEPTION
  }

  private static class Event {
    EventType type;
    IMessage message;
    Exception exception;

    Event(EventType type) {
      this.type = type;
    }

    Event(EventType type, Exception exception) {
      this(type);
      this.exception = exception;
    }

    Event(EventType type, IMessage message) {
      this(type);
      this.message = message;
    }
  }

}