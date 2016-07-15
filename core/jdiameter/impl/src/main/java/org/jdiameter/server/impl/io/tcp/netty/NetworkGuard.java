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

package org.jdiameter.server.impl.io.tcp.netty;

import static org.jdiameter.server.impl.helpers.Parameters.BindDelay;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.transport.tcp.netty.TCPClientConnection;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TCP implementation of {@link org.jdiameter.server.api.io.INetworkGuard}.
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class NetworkGuard implements INetworkGuard {
  private static final Logger logger = LoggerFactory.getLogger(NetworkGuard.class);

  protected CopyOnWriteArrayList<INetworkConnectionListener> listeners = new CopyOnWriteArrayList<INetworkConnectionListener>();

  protected IMessageParser parser;
  protected int port;
  protected InetAddress[] localAddresses;
  protected long bindDelay;

  protected final EventLoopGroup bossGroup = new NioEventLoopGroup();
  protected final EventLoopGroup workerGroup = new NioEventLoopGroup();
  protected List<Channel> channels = new CopyOnWriteArrayList<Channel>();

  protected final ScheduledExecutorService binderExecutor = Executors.newSingleThreadScheduledExecutor();

  Runnable binderTask = new Runnable() {
    public void run() {
      bindAll();
    }
  };

  private void bindAll() {
    for (int i = 0; i < localAddresses.length; i++) {
      bind(new InetSocketAddress(localAddresses[i], port));
    }
  }

  private void bind(InetSocketAddress localAddress) {
    logger.debug("Binding to socket [{}]", localAddress);
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ClientHandler());
          }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

    try {
      channels.add(bootstrap.bind(localAddress).sync().channel());
      logger.debug("Bound to socket [{}]", localAddress);
    } catch (InterruptedException e) {
      logger.error("Failed to bind to socket " + localAddress, e);
    }

    /*
     * bootstrap.bind(port).addListener(new ChannelFutureListener() {
     *
     * @Override public void operationComplete(ChannelFuture channelFuture) throws Exception { if (channelFuture.isSuccess()) {
     * channels.add(channelFuture.channel()); } } });
     */
  }

  public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
      logger.debug("Received connection on socket [{}] from [{}]", ctx.channel().localAddress(), ctx.channel().remoteAddress());

      TCPClientConnection client = new TCPClientConnection(ctx.channel(), parser);
      for (INetworkConnectionListener listener : listeners) {
        listener.newNetworkConnection(client);
      }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      logger.error(cause.getMessage(), cause);
    }
  }

  @Deprecated
  public NetworkGuard(InetAddress inetAddress, int port, IMessageParser parser) throws Exception {
    this(inetAddress, port, null, parser, null);
  }

  public NetworkGuard(InetAddress inetAddress, int port, IConcurrentFactory concurrentFactory, IMessageParser parser,
      IMetaData data) throws Exception {
    this(new InetAddress[] { inetAddress }, port, concurrentFactory, parser, data);
  }

  public NetworkGuard(InetAddress[] inetAddress, int port, IConcurrentFactory concurrentFactory, IMessageParser parser,
      IMetaData data) throws Exception {
    this.parser = parser;
    this.localAddresses = inetAddress;
    this.port = port;
    this.bindDelay = data.getConfiguration().getLongValue(BindDelay.ordinal(), (Long) BindDelay.defValue());
    this.binderExecutor.schedule(binderTask, bindDelay, TimeUnit.MILLISECONDS);
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
    return "NetworkGuard:" + (this.localAddresses.length != 0 ? this.localAddresses : "closed");
  }

  public void destroy() {
    logger.debug("Destroying network guard");
    closeChannels();
    closeWorkerGroup();
    closeBossGroup();
    binderExecutor.shutdown();
  }

  private void closeWorkerGroup() {
    try {
      workerGroup.shutdownGracefully().sync();
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private void closeBossGroup() {
    try {
      bossGroup.shutdownGracefully().sync();
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private void closeChannels() {
    for (Channel channel : channels) {
      try {
        logger.debug("Closing channel on socket [{}]", channel.localAddress());
        channel.close().sync();
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

}