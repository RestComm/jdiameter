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
import java.net.InetSocketAddress;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class TLSTransportClient {
  private static final Logger logger = LoggerFactory.getLogger(TLSTransportClient.class);

  private TLSClientConnection parentConnection;
  private IConcurrentFactory concurrentFactory;
  private IMessageParser parser;
  private Configuration config;

  private InetSocketAddress destAddress;
  private InetSocketAddress origAddress;
  private String socketDescription = null;

  private Channel channel;
  private EventLoopGroup workerGroup;

  private volatile TlsHandshakingState tlsHandshakingState = TlsHandshakingState.INIT;

  enum TlsHandshakingState {
    INIT, SHAKING, SHAKEN
  }

  protected TLSTransportClient(TLSClientConnection parenConnection, IConcurrentFactory concurrentFactory, IMessageParser parser,
      Configuration config) {
    this.parentConnection = parenConnection;
    this.concurrentFactory = concurrentFactory;
    this.parser = parser;
    this.config = config;
  }

  public TLSTransportClient(TLSClientConnection parenConnection, IConcurrentFactory concurrentFactory, IMessageParser parser,
      Configuration config, InetSocketAddress destAddress, InetSocketAddress origAddress) {
    this(parenConnection, concurrentFactory, parser, config);

    if (destAddress == null) {
      throw new IllegalArgumentException("Destination address is required");
    }
    this.destAddress = destAddress;
    this.origAddress = origAddress;
    this.socketDescription = origAddress.toString() + "->" + destAddress.toString();

    logger.debug("Created TLSTransportClient (client) for {}", socketDescription);
  }

  public TLSTransportClient(TLSClientConnection parenConnection, IConcurrentFactory concurrentFactory, IMessageParser parser,
      Configuration config, Channel channel) {
    this(parenConnection, concurrentFactory, parser, config);
    if (channel == null) {
      throw new IllegalArgumentException("Channel is required");
    }
    this.channel = channel;
    this.origAddress = (InetSocketAddress) this.channel.localAddress();
    this.destAddress = (InetSocketAddress) this.channel.remoteAddress();
    this.socketDescription = origAddress.toString() + "->" + destAddress.toString();

    ChannelPipeline pipeline = this.channel.pipeline();
    pipeline.addLast("startTlsServerHandler", new StartTlsServerHandler(this));
    pipeline.addLast("decoder", new DiameterMessageDecoder(parenConnection, parser));
    pipeline.addLast("msgHandler", new DiameterMessageHandler(parentConnection, true));
    pipeline.addLast("encoder", new DiameterMessageEncoder(parser));
    pipeline.addLast("inbandWriter", new InbandSecurityHandler());

    logger.debug("Created TLSTransportClient (server) for {}", socketDescription);
  }

  // only client side
  public void start() throws InterruptedException {
    logger.debug("Staring client TLSTransportClient {} ", socketDescription);
    if (isConnected()) {
      logger.debug("Already connected TLSTransportClient {} ", socketDescription);
      return;
    }

    workerGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new DiameterMessageDecoder(parentConnection, parser));
        pipeline.addLast("msgHandler", new DiameterMessageHandler(parentConnection, false));
        pipeline.addLast("startTlsInitiator", new StartTlsInitiator(config, TLSTransportClient.this));
        pipeline.addLast("encoder", new DiameterMessageEncoder(parser));
        pipeline.addLast("inbandWriter", new InbandSecurityHandler());
      }
    });

    this.channel = bootstrap.remoteAddress(destAddress).connect().sync().channel();

    parentConnection.onConnected();

    logger.debug("Started TLS Transport on Socket {}", socketDescription);
  }

  public TLSClientConnection getParent() {
    return parentConnection;
  }

  public InetSocketAddress getDestAddress() {
    return this.destAddress;
  }

  public void setDestAddress(InetSocketAddress address) {
    this.destAddress = address;
    if (logger.isDebugEnabled()) {
      logger.debug("Destination address is set to [{}] : [{}]", destAddress.getHostName(), destAddress.getPort());
    }
  }

  public void setOrigAddress(InetSocketAddress address) {
    this.origAddress = address;
    if (logger.isDebugEnabled()) {
      logger.debug("Origin address is set to [{}] : [{}]", origAddress.getHostName(), origAddress.getPort());
    }
  }

  public InetSocketAddress getOrigAddress() {
    return this.origAddress;
  }

  void sendMessage(IMessage message) throws IOException {
    if (!isConnected()) {
      throw new IOException("Failed to send message over [" + socketDescription + "]");
    }

    if (this.tlsHandshakingState == TlsHandshakingState.SHAKING) {
      return;
    }

    logger.debug("About to send a message over the TLS socket [{}]", socketDescription);
    channel.writeAndFlush(message);
  }

  boolean isConnected() {
    return this.channel != null && this.channel.isActive();
  }

  public void stop() {
    //logger.debug("Stopping TLS Transport {}", socketDescription);

    closeChannel();
    closeWorkerGroup();

    //logger.debug("TLS Transport is stopped {}", socketDescription);

    getParent().disconnect();
  }

  public void release() throws Exception {
    stop();
    destAddress = null;
    origAddress = null;
  }

  private void closeChannel() {
    if (channel != null && channel.isActive()) {
      try {
        channel.closeFuture().sync();
      } catch (InterruptedException e) {
        logger.error("Error stopping socket " + socketDescription, e);
      }
      channel = null;
    }
  }

  private void closeWorkerGroup() {
    if (workerGroup != null && !workerGroup.isShuttingDown()) {
      try {
        workerGroup.shutdownGracefully().sync();
      } catch (InterruptedException e) {
        logger.error("Error stopping socket " + socketDescription, e);
      }
      workerGroup = null;
    }
  }

  public TlsHandshakingState getTlsHandshakingState() {
    return tlsHandshakingState;
  }

  public void setTlsHandshakingState(TlsHandshakingState tlsHandshakingState) {
    this.tlsHandshakingState = tlsHandshakingState;
  }

  public TLSClientConnection getParentConnection() {
    return parentConnection;
  }

  public IMessageParser getParser() {
    return parser;
  }

  public Configuration getConfig() {
    return config;
  }

}