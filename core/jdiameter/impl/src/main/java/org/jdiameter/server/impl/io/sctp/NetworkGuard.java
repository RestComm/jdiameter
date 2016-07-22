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

import java.net.InetAddress;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class NetworkGuard implements INetworkGuard {

  private static final Logger logger = LoggerFactory.getLogger(NetworkGuard.class);
  protected IMessageParser parser;
  protected IConcurrentFactory concurrentFactory;
  protected int port;
  protected CopyOnWriteArrayList<INetworkConnectionListener> listeners = new CopyOnWriteArrayList<INetworkConnectionListener>();
  protected boolean isWork = false;
  protected Selector selector;
  protected List<SCTPServerConnection> serverConnections;

  protected InetAddress[] localAddresses;

  @Deprecated
  public NetworkGuard(InetAddress inetAddress, int port, IMessageParser parser) throws Exception {
    this(inetAddress, port, null, parser, null);
  }

  public NetworkGuard(InetAddress inetAddress, int port, IConcurrentFactory concurrentFactory, IMessageParser parser, IMetaData data) throws Exception {
    this(new InetAddress[]{inetAddress}, port, concurrentFactory, parser, data);
  }

  public NetworkGuard(InetAddress[] inetAddresses, int port, IConcurrentFactory concurrentFactory, IMessageParser parser, IMetaData data) throws Exception {
    this.port = port;
    this.localAddresses = inetAddresses;
    this.parser = parser;
    this.concurrentFactory = concurrentFactory == null ? new DummyConcurrentFactory() : concurrentFactory;
    this.serverConnections = new ArrayList<SCTPServerConnection>();

    try {
      for (InetAddress ia : inetAddresses) {
        final SCTPServerConnection sctpServerConnection = new SCTPServerConnection(null, ia, port, parser, null, this);
        this.serverConnections.add(sctpServerConnection);
      }
    }
    catch (Exception exc) {
      try {
        destroy();
      }
      catch (Exception e) {
        // ignore
      }
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

  @Override
  public void addListener(INetworkConnectionListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void remListener(INetworkConnectionListener listener) {
    listeners.remove(listener);
  }

  @Override
  public String toString() {
    return "NetworkGuard:" + (this.serverConnections.size() != 0 ? this.serverConnections : "closed");
  }

  public void onNewRemoteConnection(Server globalServer, Association association, SCTPServerConnection connection) {
    logger.debug("New remote connection");
    try {
      final String peerAddress = association.getPeerAddress();
      final int peerPort = association.getPeerPort();
      final String localAddress = association.getHostAddress();
      final int localPort = association.getHostPort();
      SCTPServerConnection remoteClientConnection = new SCTPServerConnection(null, InetAddress.getByName(peerAddress),
          peerPort, InetAddress.getByName(localAddress), localPort, parser, null, this, globalServer, association, connection.getManagement());
      notifyListeners(remoteClientConnection);
    }
    catch (Exception exc) {
      logger.error("Error creating new remote connection");
    }
  }

  @Override
  public void destroy() {
    logger.debug("Destroying");
    Iterator<SCTPServerConnection> it = this.serverConnections.iterator();
    while (it.hasNext()) {
      try {
        SCTPServerConnection server = it.next();
        it.remove();
        if (server != null && server.isConnected()) {
          server.disconnect();
          server.destroy();
        }
      }
      catch (Exception e) {
        logger.error("", e);
      }
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