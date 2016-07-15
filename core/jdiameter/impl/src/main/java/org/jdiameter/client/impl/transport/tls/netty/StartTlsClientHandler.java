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

import javax.net.ssl.SSLEngine;

import org.jdiameter.client.impl.transport.tls.netty.TLSTransportClient.TlsHandshakingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class StartTlsClientHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(StartTlsClientHandler.class);

  private final TLSTransportClient tlsTransportClient;

  public StartTlsClientHandler(TLSTransportClient tlsTransportClient) {
    this.tlsTransportClient = tlsTransportClient;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
    logger.debug("StartTlsClientHandler");
    ByteBuf buf = (ByteBuf) msg;
    byte[] bytes = new byte[buf.readableBytes()];
    buf.getBytes(buf.readerIndex(), bytes);

    if ("StartTlsResponse".equals(new String(bytes))) {
      logger.debug("received StartTlsResponse");

      SslContext sslContext = SslContextFactory.getSslContextForClient(this.tlsTransportClient.getConfig());
      SSLEngine sslEngine = sslContext.newEngine(ctx.alloc());
      sslEngine.setUseClientMode(true);
      SslHandler sslHandler = new SslHandler(sslEngine, false);

      final ChannelPipeline pipeline = ctx.pipeline();
      pipeline.remove("startTlsClientHandler");
      pipeline.addLast("sslHandler", sslHandler);

      logger.debug("StartTls starting handshake");

      sslHandler.handshakeFuture().addListener(new GenericFutureListener() {
        @Override
        public void operationComplete(Future future) throws Exception {
          if (future.isSuccess()) {
            logger.debug("StartTls handshake succesfull");

            tlsTransportClient.setTlsHandshakingState(TlsHandshakingState.SHAKEN);

            logger.debug("restoring all handlers");

            pipeline.addLast("decoder", new DiameterMessageDecoder(StartTlsClientHandler.this.tlsTransportClient.getParent(),
                StartTlsClientHandler.this.tlsTransportClient.getParser()));
            pipeline.addLast("msgHandler",
                new DiameterMessageHandler(StartTlsClientHandler.this.tlsTransportClient.getParent(), true));

            pipeline.addLast("encoder", new DiameterMessageEncoder(StartTlsClientHandler.this.tlsTransportClient.getParser()));
            pipeline.addLast("inbandWriter", new InbandSecurityHandler());
          }
        }
      });

    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(cause.getMessage(), cause);
  }

}
