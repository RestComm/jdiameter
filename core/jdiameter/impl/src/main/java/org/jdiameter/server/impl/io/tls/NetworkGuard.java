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
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.server.impl.io.tls;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.transport.tls.TLSClientConnection;
import org.jdiameter.client.impl.transport.tls.TLSUtils;
import org.jdiameter.common.api.concurrent.DummyConcurrentFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TLS implementation of {@link org.jdiameter.server.api.io.INetworkGuard}.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class NetworkGuard implements INetworkGuard, Runnable {

  private static final Logger logger = LoggerFactory.getLogger(NetworkGuard.class);

  private IMessageParser parser;
  private IConcurrentFactory concurrentFactory;
  private int port;
  private CopyOnWriteArrayList<INetworkConnectionListener> listeners = new CopyOnWriteArrayList<INetworkConnectionListener>();
  private boolean isWork = false;
  // private SSLServerSocket serverSocket;
  private ServerSocket serverSocket;
  private Configuration localPeerSSLConfig;
  private Thread thread;
  private String secRef;

  public NetworkGuard(InetAddress inetAddress, int port, IConcurrentFactory concurrentFactory, IMessageParser parser, IMetaData data) throws Exception {
    this.port = port;
    this.parser = parser;
    this.concurrentFactory = concurrentFactory == null ? new DummyConcurrentFactory() : concurrentFactory;
    this.thread = this.concurrentFactory.getThread("NetworkGuard", this);
    // extract sec_ref from local peer;
    Configuration conf = data.getConfiguration();

    if (!conf.isAttributeExist(org.jdiameter.client.impl.helpers.Parameters.SecurityRef.ordinal())) {
      throw new IllegalArgumentException("No security_ref attribute present in local peer!");
    }

    String secRef = conf.getStringValue(org.jdiameter.client.impl.helpers.Parameters.SecurityRef.ordinal(), "");
    // now need to get proper security data.
    this.localPeerSSLConfig = TLSUtils.getSSLConfiguration(conf, secRef);

    if (this.localPeerSSLConfig == null) {
      throw new IllegalArgumentException("No Security for security_reference '" + secRef + "'");
    }
    // SSLContext sslContext = TLSUtils.getSecureContext(localPeerSSLConfig);

    try {
      // SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
      // this.serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket();

      this.serverSocket = new ServerSocket();
      this.serverSocket.bind(new InetSocketAddress(inetAddress, port));

      this.isWork = true;
      logger.info("Open server socket {} ", serverSocket);
      this.thread.start();
    }
    catch (Exception exc) {
      destroy();
      throw new Exception(exc);
    }
  }

  @Override
  public void run() {
    try {
      while (this.isWork) {
        // without timeout when we kill socket, this causes errors, bug in VM ?
        try {
          Socket clientConnection = serverSocket.accept();
          logger.info("Open incomming SSL connection {}", clientConnection);
          TLSClientConnection client = new TLSClientConnection(null, this.localPeerSSLConfig, this.concurrentFactory, clientConnection, parser);

          this.notifyListeners(client);
        }
        catch (Exception e) {
          logger.debug("Failed to accept connection,", e);
        }
      }
    }
    catch (Exception exc) {
      logger.warn("Server socket stopped", exc);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.server.api.io.INetworkGuard#addListener(org.jdiameter.server .api.io.INetworkConnectionListener)
   */
  @Override
  public void addListener(INetworkConnectionListener listener) {
    if (!this.listeners.contains(listener)) {
      this.listeners.add(listener);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.server.api.io.INetworkGuard#remListener(org.jdiameter.server .api.io.INetworkConnectionListener)
   */
  @Override
  public void remListener(INetworkConnectionListener listener) {
    this.listeners.remove(listener);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.server.api.io.INetworkGuard#destroy()
   */
  @Override
  public void destroy() {
    try {
      this.isWork = false;
      try {
        if (this.thread != null) {
          this.thread.join(2000);
          if (this.thread.isAlive()) {
            // FIXME: remove ASAP
            this.thread.interrupt();
          }
          thread = null;
        }
      }
      catch (InterruptedException e) {
        logger.debug("Can not stop thread", e);
      }

      if (this.serverSocket != null) {
        this.serverSocket.close();
        this.serverSocket = null;
      }
    }
    catch (IOException e) {
      logger.error("", e);
    }
  }

  // ------------------------- private section ---------------------------

  private void notifyListeners(TLSClientConnection client) {
    for (INetworkConnectionListener listener : this.listeners) {
      try {
        listener.newNetworkConnection(client);
      }
      catch (Exception e) {
        logger.debug("Connection listener threw exception!", e);
      }
    }
  }

}
