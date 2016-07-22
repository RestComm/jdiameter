/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, TeleStax Inc. and individual contributors
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

package org.jdiameter.server.impl.io.sctp;

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
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.Management;
import org.mobicents.protocols.api.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SCTPServerConnection implements IConnection {

  private static Logger logger = LoggerFactory.getLogger(SCTPServerConnection.class);
  private final long createdTime;
  private SCTPTransportServer server;
  // FIXME : requires JDK6 : protected LinkedBlockingDeque<Event> buffer = new LinkedBlockingDeque<Event>(64);
  private LinkedBlockingQueue<Event> buffer = new LinkedBlockingQueue<Event>(64);
  private IMessageParser parser;
  private Lock lock = new ReentrantLock();
  private ConcurrentLinkedQueue<IConnectionListener> listeners = new ConcurrentLinkedQueue<IConnectionListener>();
  // Cached value for connection key
  private String cachedKey = null;
  private NetworkGuard parentGuard;

  protected SCTPServerConnection(IMessageParser parser, NetworkGuard guard) {
    this.createdTime = System.currentTimeMillis();
    this.parser = parser;
    this.parentGuard = guard;
    server = new SCTPTransportServer(this);
  }

  // this creates the listening server - no dest address is passed it is automatically 0.0.0.0:0
  public SCTPServerConnection(Configuration config, InetAddress localAddress, int localPort, IMessageParser parser, String ref,
      NetworkGuard guard) throws Exception {
    this(parser, guard);

    logger.debug("SCTP Server constructor for listening server @ {}:{}", localAddress, localPort);
    server.setOrigAddress(new InetSocketAddress(localAddress, localPort));
    server.startServer();
  }

  // this creates the remote client connection
  public SCTPServerConnection(Configuration config, InetAddress remoteAddress, int remotePort, InetAddress localAddress,
      int localPort, IMessageParser parser, String ref, NetworkGuard guard, Server globalServer, Association association,
      Management management) throws Exception {
    this(parser, guard);

    logger.debug("SCTP Server constructor for remote client connections @ {}:{} <=> {}:{}", new Object[]{localAddress, localPort, remoteAddress, remotePort});
    server.setOrigAddress(new InetSocketAddress(localAddress, localPort));
    server.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
    server.setManagement(management);
    server.startNewRemoteConnection(globalServer, association, remoteAddress.getHostAddress(), remotePort);
  }

  @Override
  public long getCreatedTime() {
    return createdTime;
  }

  @Override
  public void connect() throws TransportException {
    // NOP. Only for client. TODO: Consider remove from connection interface
  }

  @Override
  public void disconnect() throws InternalError {
    logger.debug("Disconnecting SCTP Server Connection {}", this.getKey());
    try {
      if (getServer() != null) {
        getServer().stop();
      }
    }
    catch (Exception e) {
      throw new InternalError("Error while stopping transport: " + e.getMessage());
    }
  }

  public Management getManagement() {
    return this.getServer().getManagement();
  }

  public void destroy() throws InternalError {
    logger.debug("Destroying SCTP Server Connection {}", this.getKey());
    try {
      if (getServer() != null) {
        getServer().destroy();
      }
    }
    catch (Exception e) {
      throw new InternalError("Error while stopping transport: " + e.getMessage());
    }
  }

  @Override
  public void release() throws IOException {
    logger.debug("Releasing SCTP Server Connection {}", this.getKey());
    try {
      if (getServer() != null) {
        getServer().release();
      }
    }
    catch (Exception e) {
      throw new IOException(e.getMessage());
    }
    finally {
      // parser = null;
      buffer.clear();
      remAllConnectionListener();
    }
  }

  public void onNewRemoteConnection(Server server, Association association) {
    this.getParentGuard().onNewRemoteConnection(server, association, this);
  }

  public NetworkGuard getParentGuard() {
    return parentGuard;
  }

  @Override
  public void sendMessage(IMessage message) throws TransportException, OverloadException {
    try {
      if (getServer() != null) {
        getServer().sendMessage(parser.encodeMessage(message));
      }
    }
    catch (Exception e) {
      throw new TransportException("Cannot send message: ", TransportError.FailedSendMessage, e);
    }
  }

  protected SCTPTransportServer getServer() {
    return server;
  }

  @Override
  public boolean isNetworkInitiated() {
    return false;
  }

  @Override
  public boolean isConnected() {
    return getServer() != null && getServer().isConnected();
  }

  @Override
  public InetAddress getRemoteAddress() {
    return getServer().getDestAddress().getAddress();
  }

  @Override
  public int getRemotePort() {
    if (getServer() == null) {
      logger.debug("server is null");
    }
    else if (getServer().getDestAddress() == null) {
      logger.debug("dest address is null");
    }
    else if (getServer().getDestAddress().getPort() == 0) {
      logger.debug("dest address port is 0");
    }

    return getServer().getDestAddress().getPort();
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

  protected void logDetails() throws AvpDataException {
    if (logger.isDebugEnabled()) {
      logger.debug("Listeners for {}", this.getKey());
      for (IConnectionListener listener : listeners) {
        logger.debug("Listener [{}]", listener);
      }
      logger.debug("Event Queue for {}", this.getKey());
      for (Event event : buffer) {
        logger.debug("Event [{}]", event);
      }
    }
  }

  protected void onEvent(Event event) throws AvpDataException {
    logger.debug("In onEvent for connection [{}]. Getting lock", this.getKey());
    lock.lock();

    logDetails();

    try {
      // if (processBufferedMessages(event)) {
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
      // }
    }
    finally {
      lock.unlock();
    }
  }

  // protected boolean processBufferedMessages(Event event) throws AvpDataException {
  // if (listeners.size() == 0) {
  // try {
  // buffer.add(event);
  // } catch (IllegalStateException e) {
  // // FIXME : requires JDK6 : buffer.removeLast();
  // Event[] tempBuffer = buffer.toArray(new Event[buffer.size()]);
  // buffer.remove(tempBuffer[tempBuffer.length - 1]);
  // buffer.add(event);
  // }
  // return false;
  // } else {
  // return true;
  // }
  // }

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