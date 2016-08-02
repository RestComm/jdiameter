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

package org.jdiameter.client.impl.transport.tls.netty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLSocketFactory;

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
import org.jdiameter.client.impl.transport.tls.TLSUtils;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.impl.helpers.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class TLSClientConnection implements IConnection {

  private static Logger logger = LoggerFactory.getLogger(TLSClientConnection.class);

  private TLSTransportClient client;
  private SSLSocketFactory factory;
  private Configuration sslConfig;

  private final long createdTime = System.currentTimeMillis();

  private LinkedBlockingQueue<Event> buffer = new LinkedBlockingQueue<Event>(64);

  private Lock lock = new ReentrantLock();
  private ConcurrentLinkedQueue<IConnectionListener> listeners = new ConcurrentLinkedQueue<IConnectionListener>();

  // Cached value for connection key
  private String cachedKey = null;

  public TLSClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref) throws Exception {
    String secRef = ref;
    if (secRef == null) {
      if (!config.isAttributeExist(Parameters.SecurityRef.ordinal())) {
        throw new IllegalArgumentException("No security_ref attribute present in local peer!");
      } else {
        secRef = config.getStringValue(Parameters.SecurityRef.ordinal(), "");
      }
    }
    this.sslConfig = TLSUtils.getSSLConfiguration(config, secRef);
    this.client = new TLSTransportClient(this, concurrentFactory, parser, sslConfig,
        new InetSocketAddress(remoteAddress, remotePort), new InetSocketAddress(localAddress, localPort));
    // this.client.start();
  }

  public TLSClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress,
      int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, IMessageParser parser, String ref)
      throws InterruptedException {
    this.listeners.add(listener);

    String secRef = ref;
    if (secRef == null) {
      if (!config.isAttributeExist(Parameters.SecurityRef.ordinal())) {
        throw new IllegalArgumentException("No security_ref attribute present in local peer!");
      } else {
        secRef = config.getStringValue(Parameters.SecurityRef.ordinal(), "");
      }
    }
    this.sslConfig = TLSUtils.getSSLConfiguration(config, secRef);

    this.client = new TLSTransportClient(this, concurrentFactory, parser, sslConfig,
        new InetSocketAddress(remoteAddress, remotePort), new InetSocketAddress(localAddress, localPort));
    // this.client.start();
  }

  public TLSClientConnection(Configuration config, Configuration localPeerSSLConfig, IConcurrentFactory concurrentFactory,
      IMessageParser parser, Channel channel) throws Exception {

    if (localPeerSSLConfig == null) {
      throw new IllegalArgumentException("Can not create connection without TLS parameters");
    }

    this.sslConfig = localPeerSSLConfig;
    this.client = new TLSTransportClient(this, concurrentFactory, parser, sslConfig, channel);

    // this.client.start();
  }

  protected TLSTransportClient getClient() {
    return client;
  }

  public Configuration getSSLConfig() {
    return sslConfig;
  }

  public SSLSocketFactory getSSLFactory() {
    return factory;
  }

  public long getCreatedTime() {
    return createdTime;
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

  public void release() throws IOException {
    try {
      if (getClient() != null) {
        getClient().release();
      }
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    } finally {
      buffer.clear();
      remAllConnectionListener();
    }
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;
  }

  public boolean isConnected() {
    return getClient() != null && getClient().isConnected();
  }

  public boolean isNetworkInitiated() {
    return false;
  }

  public String getKey() {
    if (this.cachedKey == null) {
      this.cachedKey = new StringBuffer("aaas://").append(getRemoteAddress().getHostName()).append(":").append(getRemotePort())
          .toString();
    }

    return this.cachedKey;
  }

  public void connect() throws TransportException {
    try {
      // getClient().initialize();
      getClient().start();
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

  public void sendMessage(IMessage message) throws TransportException, OverloadException {
    try {
      if (getClient() != null) {
        getClient().sendMessage(message);
      }
    } catch (Exception e) {
      throw new TransportException("Cannot send message: ", TransportError.FailedSendMessage, e);
    }
  }

  protected void onDisconnect() throws AvpDataException {
    onEvent(new Event(EventType.DISCONNECTED));
  }

  protected void onMessageReceived(IMessage message) throws AvpDataException {
    // if (logger.isDebugEnabled()) {
    // logger.debug("Received message of size [{}]", message.array().length);
    // }
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
        buffer.remove(tempBuffer[tempBuffer.length - 1]);
        buffer.add(event);
      }
      return false;
    } else {
      return true;
    }
  }

  // --------------------- helper classes ----------------------
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