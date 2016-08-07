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
import java.net.InetSocketAddress;

import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class TCPTransportClient {
  protected EventLoopGroup workerGroup;
  protected EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(1);
  protected Channel channel;
  protected TCPClientConnection parentConnection;
  protected InetSocketAddress destAddress;
  protected InetSocketAddress sourceAddress; // TODO: what?
  protected String socketDescription;
  protected static final Logger logger = LoggerFactory.getLogger(TCPClientConnection.class);
  protected IMessageParser parser;

  protected static final int CONNECT_TIMEOUT = 500; // mills

  protected TCPTransportClient(TCPClientConnection parentConnection, IMessageParser parser) {
    if (parentConnection == null) {
      throw new IllegalArgumentException("Parent connection cannot be null");
    }
    this.parentConnection = parentConnection;

    if (parser == null) {
      throw new IllegalArgumentException("Parser cannot be null");
    }
    this.parser = parser;
  }

  public TCPTransportClient(TCPClientConnection parentConnection, IMessageParser parser, InetSocketAddress destAddress,
      InetSocketAddress sourceAddress) {
    this(parentConnection, parser);

    logger.debug("Client only connection");

    if (destAddress == null && sourceAddress == null) {
      throw new IllegalArgumentException("Either Destination or Source address is required");
    }

    if (sourceAddress != null) {
      this.sourceAddress = sourceAddress;
    }

    if (destAddress != null) {
      this.destAddress = destAddress;
      this.socketDescription = destAddress.toString();
    }
  }

  public TCPTransportClient(TCPClientConnection parentConnection, IMessageParser parser, Channel channel) {
    this(parentConnection, parser);
    logger.debug("Server only connection");

    if (channel == null) {
      throw new IllegalArgumentException("Channel cannot be null");
    }
    this.channel = channel;
    ChannelPipeline pipeline = this.channel.pipeline();
    pipeline.addLast("decoder", new DiameterMessageDecoder(parentConnection, parser));
    pipeline.addLast("encoder", new DiameterMessageEncoder(parser));
    pipeline.addLast(eventExecutorGroup, "msgHandler", new DiameterMessageHandler(parentConnection));

    this.destAddress = (InetSocketAddress) this.channel.remoteAddress();
  }

  public void start() throws InterruptedException {
    logger.debug("Starting TCP Transport on [{}]", socketDescription);
    if (isConnected()) {
      logger.debug("TCP Transport already started, [{}]", socketDescription);
      return;
    }

    this.workerGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap().group(workerGroup).channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("decoder", new DiameterMessageDecoder(parentConnection, parser));
            pipeline.addLast("encoder", new DiameterMessageEncoder(parser));
            pipeline.addLast(eventExecutorGroup, "msgHandler", new DiameterMessageHandler(parentConnection));
          }
        });

    this.channel = bootstrap.remoteAddress(destAddress).connect().sync().channel();
    logger.debug("TCP Transport connected successfully, [{}]", socketDescription);

    parentConnection.onConnected();
  }

  public void stop() {
    logger.debug("Stopping TCP Transport, [{}]", socketDescription);
    if (!isConnected()) {
      logger.debug("Already stoppped TCP Transport, [{}]", socketDescription);
      return;
    }
    closeChannel();
    closeWorkerGroup();
    closeEventExecutorGroup();
    logger.debug("Transport is stopped [{}]", socketDescription);
  }

  private void closeEventExecutorGroup() {
    if (eventExecutorGroup != null) {
      try {
        eventExecutorGroup.shutdownGracefully().sync();
      } catch (InterruptedException e) {
        logger.error("Error stopping socket " + socketDescription, e);
      }
      eventExecutorGroup = null;
    }
  }

  private void closeWorkerGroup() {
    if (workerGroup != null) {
      try {
        workerGroup.shutdownGracefully().sync();
      } catch (InterruptedException e) {
        logger.error("Error stopping socket " + socketDescription, e);
      }
      workerGroup = null;
    }
  }

  private void closeChannel() {
    if (channel != null) {
      try {
        channel.closeFuture().sync();
      } catch (InterruptedException e) {
        logger.error("Error stopping socket " + socketDescription, e);
      }
      channel = null;
    }
  }

  public void release() throws InterruptedException, IOException {
    logger.debug("Releasing TCP Transport, [{}]", socketDescription);
    stop();
    destAddress = null;
    sourceAddress = null;
  }

  public void sendMessage(IMessage message) {
    if (!isConnected()) {
      throw new IllegalStateException("TCP transport is stopped on socket " + socketDescription);
    }
    channel.writeAndFlush(message);
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Transport to ");
    if (this.destAddress != null) {
      buffer.append(this.destAddress.getHostName());
      buffer.append(":");
      buffer.append(this.destAddress.getPort());
    } else {
      buffer.append("null");
    }
    buffer.append("@");
    buffer.append(super.toString());
    return buffer.toString();
  }

  public TCPClientConnection getParent() {
    return parentConnection;
  }

  public InetSocketAddress getDestAddress() {
    return this.destAddress;
  }

  boolean isConnected() {
    return channel != null && channel.isActive();
  }
}