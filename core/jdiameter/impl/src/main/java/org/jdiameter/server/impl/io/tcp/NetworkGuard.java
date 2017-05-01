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

package org.jdiameter.server.impl.io.tcp;


import static org.jdiameter.server.impl.helpers.Parameters.BindDelay;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.transport.tcp.TCPClientConnection;
import org.jdiameter.common.api.concurrent.DummyConcurrentFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP implementation of {@link org.jdiameter.server.api.io.INetworkGuard}.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class NetworkGuard implements INetworkGuard {

  private static final Logger logger = LoggerFactory.getLogger(NetworkGuard.class);

  protected IMessageParser parser;
  protected IConcurrentFactory concurrentFactory;
  protected int port;
  protected long bindDelay;
  protected CopyOnWriteArrayList<INetworkConnectionListener> listeners = new CopyOnWriteArrayList<INetworkConnectionListener>();
  protected boolean isWork = false;
  //  protected Selector selector;
  //  protected ServerSocket serverSocket;

  //private Thread thread;
  private List<GuardTask> tasks = new ArrayList<GuardTask>();


  @Deprecated
  public NetworkGuard(InetAddress inetAddress, int port, IMessageParser parser) throws Exception {
    this(inetAddress, port, null, parser, null);
  }

  public NetworkGuard(InetAddress inetAddress, int port,
      IConcurrentFactory concurrentFactory, IMessageParser parser,
      IMetaData data) throws Exception {
    this(new InetAddress[]{inetAddress}, port, concurrentFactory, parser, data);
  }


  public NetworkGuard(InetAddress[] inetAddress, int port,
      IConcurrentFactory concurrentFactory, IMessageParser parser,
      IMetaData data) throws Exception {
    this.port = port;
    this.parser = parser;
    this.concurrentFactory = concurrentFactory == null ? new DummyConcurrentFactory() : concurrentFactory;
    //this.thread = this.concurrentFactory.getThread("NetworkGuard", this);
    this.bindDelay = data.getConfiguration().getLongValue(BindDelay.ordinal(), (Long) BindDelay.defValue());

    try {
      for (int addrIdx = 0; addrIdx < inetAddress.length; addrIdx++) {
        GuardTask guardTask = new GuardTask(new InetSocketAddress(inetAddress[addrIdx], port));
        Thread t = this.concurrentFactory.getThread(guardTask);
        guardTask.thread = t;
        tasks.add(guardTask);
      }
      isWork = true;
      for (GuardTask gt : this.tasks) {
        gt.start();
      }
      //thread.start();
    }
    catch (Exception exc) {
      destroy();
      throw new Exception(exc);
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
    return "NetworkGuard:" + (this.tasks.size() != 0 ? this.tasks : "closed");
  }

  @Override
  public void destroy() {
    isWork = false;
    Iterator<GuardTask> it = this.tasks.iterator();
    while (it.hasNext()) {
      GuardTask gt = it.next();
      it.remove();
      gt.cleanTask();
    }
  }

  private class GuardTask implements Runnable {
    private Thread thread;
    private Selector selector;
    private ServerSocket serverSocket;

    private final ScheduledExecutorService binder = Executors.newSingleThreadScheduledExecutor();

    GuardTask(final InetSocketAddress addr) throws IOException {
      if (bindDelay > 0) {
        logger.info("Socket binding will be delayed by {}ms...", bindDelay);
      }

      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            logger.debug("Binding {} after delaying {}ms...", addr, bindDelay);
            final ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            serverSocket = ssc.socket();
            serverSocket.bind(addr);

            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT, addr);
            logger.info("Open server socket {} ", serverSocket);
          }
          catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };
      binder.schedule(task, bindDelay, TimeUnit.MILLISECONDS);
    }

    public void start() {
      this.thread.start();
    }

    @Override
    public void run() {
      try {
        while (isWork) {
          if (selector == null) {
            logger.trace("Selector is still null, stack is waiting for binding...");
            Thread.sleep(250);
            continue;
          }
          // without timeout when we kill socket, this causes errors, bug in VM ?
          int num = selector.select(100);
          if (num == 0) {
            continue;
          }
          Set<SelectionKey> keys = selector.selectedKeys();
          try {
            for (SelectionKey key : keys) {
              if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                try {
                  Socket s = serverSocket.accept();
                  logger.info("Open incomming connection {}", s);
                  TCPClientConnection client = new TCPClientConnection(null, concurrentFactory, s, parser,
                      null);
                  // PCB added logging
                  logger.debug("Finished initialising TCPClientConnection for {}", s);
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

    public void cleanTask() {
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
        try {
          selector.close();
        }
        catch (Exception e) {
          // ignore
        }
        selector = null;
      }
      if (serverSocket != null) {
        try {
          serverSocket.close();
        }
        catch (Exception e) {
          // ignore
        }
        serverSocket = null;
      }
      if (binder != null) {
        binder.shutdown();
      }
    }

    @Override
    public String toString() {
      return "GuardTask [serverSocket=" + serverSocket + "]";
    }

  }
}