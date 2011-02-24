/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.server.impl.io.tcp;

import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.transport.tcp.TCPClientConnection;
import org.jdiameter.common.api.concurrent.DummyConcurrentFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
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

  private Thread thread;

  @Deprecated
  public NetworkGuard(InetAddress inetAddress, int port, IMessageParser parser) throws Exception {
    this(inetAddress, port, null, parser);
  }

  public NetworkGuard(InetAddress inetAddress, int port, IConcurrentFactory concurrentFactory, IMessageParser parser) throws Exception {
    this.port = port;
    this.parser = parser;
    this.concurrentFactory = concurrentFactory == null ? new DummyConcurrentFactory() : concurrentFactory;
    this.thread = this.concurrentFactory.getThread("NetworkGuard", this);
    //
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.configureBlocking(false);
      serverSocket = ssc.socket();
      serverSocket.bind(new InetSocketAddress(inetAddress, port));
      selector = Selector.open();
      ssc.register(selector, SelectionKey.OP_ACCEPT);
      isWork = true;
      logger.info("Open server socket {} ", serverSocket);
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
        // without timeout when we kill socket, this causes errors, bug in VM ?
        int num = selector.select(200);
        if (num == 0)
          continue;
        Set keys = selector.selectedKeys();
        try {
          for (Object key1 : keys) {
            SelectionKey key = (SelectionKey) key1;
            if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
              try {
                Socket s = serverSocket.accept();
                logger.info("Open incomming connection {}", s);
                TCPClientConnection client = new TCPClientConnection(null, concurrentFactory, s, parser, null);
                for (INetworkConnectionListener listener : listeners) {
                  listener.newNetworkConnection(client);
                }
              }
              catch (Exception e) {
                logger.warn("Can not create incoming connection", e);
              }
            }
          }
        }
        catch (Exception e) {
          logger.debug("Failed to accept connection,", e);
        }
        finally {
          keys.clear();
        }
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

  public void destroy() {
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
      if (selector != null) {
        selector.close();
        selector = null;
      }
      if (serverSocket != null) {
        serverSocket.close();
        serverSocket = null;
      }
    }
    catch (IOException e) {
      logger.error("", e);
    }
  }
}