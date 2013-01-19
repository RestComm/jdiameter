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

package org.jdiameter.server.impl.io.sctp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.channels.Selector;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.DummyConcurrentFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SCTP implementation of {@link org.jdiameter.server.api.io.INetworkGuard}.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class NetworkGuard implements INetworkGuard, Runnable {

  private static final Logger logger = LoggerFactory.getLogger(NetworkGuard.class);
  protected IMessageParser parser;
  protected IConcurrentFactory concurrentFactory;
  protected int port;
  protected CopyOnWriteArrayList<INetworkConnectionListener> listeners = new CopyOnWriteArrayList<INetworkConnectionListener>();
  protected boolean isWork = false;
  protected Selector selector;
  protected ServerSocket serverSocket;
  SCTPServerConnection server;
  private Thread thread;
  protected InetAddress localAddress;

  @Deprecated
  public NetworkGuard(InetAddress inetAddress, int port, IMessageParser parser) throws Exception {
    this(inetAddress, port, null, parser, null);
  }

  public NetworkGuard(InetAddress inetAddress, int port, IConcurrentFactory concurrentFactory, IMessageParser parser,
      IMetaData data) throws Exception {

    this.port = port;
    this.localAddress = inetAddress;
    this.parser = parser;
    this.concurrentFactory = concurrentFactory == null ? new DummyConcurrentFactory() : concurrentFactory;
    this.thread = this.concurrentFactory.getThread("NetworkGuard", this);

    try {
      server = new SCTPServerConnection(null, this.localAddress, port, parser, null, this);
      isWork = true;
      thread.start();
    }
    catch (Exception exc) {
      destroy();
      throw new Exception(exc);
    }
  }

  public void run() {
    try {
      while (isWork) {
        Thread.sleep(10000);
      }
    }
    catch (Exception exc) {
      logger.warn("Server socket stopped", exc);
    }
  }

  public void addListener(INetworkConnectionListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void remListener(INetworkConnectionListener listener) {
    listeners.remove(listener);
  }

  @Override
  public String toString() {
    return "NetworkGuard:" + (serverSocket != null ? serverSocket.toString() : "closed");
  }

  public void onNewRemoteConnection(Server globalServer, Association association) {
    logger.debug("New remote connection");
    try {
      String peerAddress = association.getPeerAddress();
      int peerPort = association.getPeerPort();
      SCTPServerConnection remoteClientConnection = new SCTPServerConnection(null, InetAddress.getByName(peerAddress),
          peerPort, this.localAddress, port, parser, null, this, globalServer, association, server.getManagement());
      notifyListeners(remoteClientConnection);
    }
    catch (Exception exc) {
      logger.error("Error creating new remote connection");
    }
  }

  public void destroy() {
    logger.debug("Destroying");
    try {
      isWork = false;
      try {
        if (thread != null) {
          thread.join(2000);
          if (thread.isAlive()) {
            // FIXME: remove ASAP
            thread.interrupt();
          }
          thread = null;

        }
      }
      catch (InterruptedException e) {
        logger.debug("Can not stop thread", e);
      }
      if (server != null && server.isConnected()) {
        server.disconnect();
        server.destroy();
      }
    }
    catch (Exception e) {
      logger.error("", e);
    }
  }

  private void notifyListeners(SCTPServerConnection server) {
    for (INetworkConnectionListener listener : this.listeners) {
      try {
        listener.newNetworkConnection(server);
      }
      catch (Exception e) {
        logger.debug("Connection listener threw exception!", e);
      }
    }
  }
}