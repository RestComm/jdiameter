/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2013, TeleStax and individual contributors as indicated
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

package org.jdiameter.client.impl.transport.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
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

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SCTPClientConnection implements IConnection {

  private static Logger logger = LoggerFactory.getLogger(SCTPClientConnection.class);
  private final long createdTime;
  private SCTPTransportClient client;
  // FIXME : requires JDK6 : protected LinkedBlockingDeque<Event> buffer = new LinkedBlockingDeque<Event>(64);
  private LinkedBlockingQueue<Event> buffer = new LinkedBlockingQueue<Event>(64);
  private IMessageParser parser;
  private Lock lock = new ReentrantLock();
  private ConcurrentLinkedQueue<IConnectionListener> listeners = new ConcurrentLinkedQueue<IConnectionListener>();
  // Cached value for connection key
  private String cachedKey = null;

  protected SCTPClientConnection(IMessageParser parser) {
    this.createdTime = System.currentTimeMillis();
    this.parser = parser;
    client = new SCTPTransportClient(this);
  }

  public SCTPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref) {
    this(parser);

    logger.debug("SCTP Client constructor. Remote [{}:{}] Local [{}:{}]", new Object[] { remoteAddress, remotePort,
        localAddress, localPort });
    client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
    client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
  }

  public SCTPClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, IMessageParser parser, String ref) {
    this(parser);

    logger.debug("SCTP Client constructor (with ref). Remote [{}:{}] Local [{}:{}]", new Object[] { remoteAddress, remotePort,
        localAddress, localPort });
    client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
    client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
    listeners.add(listener);
  }

  @Override
  public long getCreatedTime() {
    return createdTime;
  }

  @Override
  public void connect() throws TransportException {
    try {
      getClient().initialize();
      getClient().start();
    }
    catch (IOException e) {
      throw new TransportException("Cannot init transport: ", TransportError.NetWorkError, e);
    }
    catch (Exception e) {
      throw new TransportException("Cannot init transport: ", TransportError.Internal, e);
    }
  }

  @Override
  public void disconnect() throws InternalError {
    try {
      if (getClient() != null) {
        getClient().stop();
      }
    }
    catch (Exception e) {
      throw new InternalError("Error while stopping transport: " + e.getMessage());
    }
  }

  @Override
  public void release() throws IOException {
    try {
      if (getClient() != null) {
        getClient().release();
      }
    }
    catch (Exception e) {
      throw new IOException(e.getMessage());
    }
    finally {
      parser = null;
      buffer.clear();
      remAllConnectionListener();
    }
  }

  @Override
  public void sendMessage(IMessage message) throws TransportException, OverloadException {
    try {
      if (getClient() != null) {
        getClient().sendMessage(parser.encodeMessage(message));
      }
    }
    catch (Exception e) {
      throw new TransportException("Cannot send message: ", TransportError.FailedSendMessage, e);
    }
  }

  protected SCTPTransportClient getClient() {
    return client;
  }

  @Override
  public boolean isNetworkInitiated() {
    return false;
  }

  @Override
  public boolean isConnected() {
    return getClient() != null && getClient().isConnected();
  }

  @Override
  public InetAddress getRemoteAddress() {
    return getClient().getDestAddress().getAddress();
  }

  @Override
  public int getRemotePort() {
    return getClient().getDestAddress().getPort();
  }

  @Override
  public void addConnectionListener(IConnectionListener listener) {
    lock.lock();
    try {
      listeners.add(listener);
      if (buffer.size() != 0) {
        for (Event e : buffer) {
          try {
            onEvent(e);
          }
          catch (AvpDataException e1) {
            // ignore
          }
        }
        buffer.clear();
      }
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public void remAllConnectionListener() {
    lock.lock();
    try {
      listeners.clear();
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public void remConnectionListener(IConnectionListener listener) {
    lock.lock();
    try {
      listeners.remove(listener);
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;
  }

  @Override
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

  protected void onMessageReceived(ByteBuffer message) throws AvpDataException {
    if (logger.isDebugEnabled()) {
      logger.debug("Received message of size [{}]", message.array().length);
    }
    onEvent(new Event(EventType.MESSAGE_RECEIVED, message));
  }

  protected void onAvpDataException(AvpDataException e) {
    try {
      onEvent(new Event(EventType.DATA_EXCEPTION, e));
    }
    catch (AvpDataException e1) {
      // ignore
    }
  }

  protected void onConnected() {
    try {
      onEvent(new Event(EventType.CONNECTED));
    }
    catch (AvpDataException e1) {
      // ignore
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
    }
    finally {
      lock.unlock();
    }
  }

  protected boolean processBufferedMessages(Event event) throws AvpDataException {
    if (listeners.size() == 0) {
      try {
        buffer.add(event);
      }
      catch (IllegalStateException e) {
        // FIXME : requires JDK6 : buffer.removeLast();
        Event[] tempBuffer = buffer.toArray(new Event[buffer.size()]);
        buffer.remove(tempBuffer[tempBuffer.length - 1]);
        buffer.add(event);
      }
      return false;
    }
    else {
      return true;
    }
  }

  // ------------------ helper classes ------------------------
  private enum EventType {
    CONNECTED, DISCONNECTED, MESSAGE_RECEIVED, DATA_EXCEPTION
  }

  private static class Event {

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
}