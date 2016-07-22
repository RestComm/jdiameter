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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class StartTlsServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(StartTlsServerHandler.class);
  private final TLSTransportClient tlsTransportClient;

  public StartTlsServerHandler(TLSTransportClient tlsTransportClient) {
    this.tlsTransportClient = tlsTransportClient;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    logger.debug("StartTlsServerHandler");
    ByteBuf buf = (ByteBuf) msg;
    byte[] bytes = new byte[buf.readableBytes()];
    buf.getBytes(buf.readerIndex(), bytes);

    if ("StartTlsRequest".equals(new String(bytes))) {
      logger.debug("Received StartTlsRequest");
      SslContext sslContext = SslContextFactory.getSslContextForServer(this.tlsTransportClient.getConfig());
      SSLEngine sslEngine = sslContext.newEngine(ctx.alloc());
      sslEngine.setUseClientMode(false);
      SslHandler sslHandler = new SslHandler(sslEngine, false);

      final ChannelPipeline pipeline = ctx.pipeline();

      pipeline.remove("decoder");
      pipeline.remove("msgHandler");
      pipeline.remove("encoder");
      pipeline.remove("inbandWriter");
      pipeline.remove(this);

      pipeline.addLast("sslHandler", sslHandler);

      sslHandler.handshakeFuture().addListener(new GenericFutureListener() {

        @Override
        public void operationComplete(Future future) throws Exception {
          if (future.isSuccess()) {
            logger.debug("StartTls server handshake succesfull");

            tlsTransportClient.setTlsHandshakingState(TlsHandshakingState.SHAKEN);

            logger.debug("restoring all handlers");

            pipeline.addLast("decoder", new DiameterMessageDecoder(StartTlsServerHandler.this.tlsTransportClient.getParent(),
                StartTlsServerHandler.this.tlsTransportClient.getParser()));
            pipeline.addLast("msgHandler",
                new DiameterMessageHandler(StartTlsServerHandler.this.tlsTransportClient.getParent(), true));

            pipeline.addLast("encoder", new DiameterMessageEncoder(StartTlsServerHandler.this.tlsTransportClient.getParser()));
            pipeline.addLast("inbandWriter", new InbandSecurityHandler());

          }
        }
      });

      ReferenceCountUtil.release(msg);
      logger.debug("Sending StartTlsResponse");
      ctx.writeAndFlush(Unpooled.wrappedBuffer("StartTlsResponse".getBytes())).addListener(new GenericFutureListener() {

        @Override
        public void operationComplete(Future f) throws Exception {
          if (!f.isSuccess()) {
            logger.error(f.cause().getMessage(), f.cause());
          }

        }
      });
    } else {
      ctx.fireChannelRead(msg);
    }

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(cause.getMessage(), cause);
  }

}
