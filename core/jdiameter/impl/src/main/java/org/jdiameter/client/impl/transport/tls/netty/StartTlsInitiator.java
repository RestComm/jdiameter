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

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.impl.transport.tls.netty.TLSTransportClient.TlsHandshakingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class StartTlsInitiator extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(StartTlsInitiator.class);
  private final Configuration config;
  private final TLSTransportClient tlsTransportClient;

  public StartTlsInitiator(Configuration config, TLSTransportClient tlsTransportClient) {
    this.config = config;
    this.tlsTransportClient = tlsTransportClient;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof IMessage) {
      IMessage m = (IMessage) msg;
      logger.debug("StartTlsInitiator");
      if (m.getCommandCode() == IMessage.CAPABILITIES_EXCHANGE_ANSWER
          && this.tlsTransportClient.getTlsHandshakingState() == TlsHandshakingState.INIT) {
        AvpSet set = m.getAvps();
        Avp inbandAvp = set.getAvp(Avp.INBAND_SECURITY_ID);
        if (inbandAvp != null && inbandAvp.getUnsigned32() == 1) {
          this.tlsTransportClient.setTlsHandshakingState(TlsHandshakingState.SHAKING);

          final ChannelPipeline pipeline = ctx.pipeline();
          pipeline.remove("decoder");
          pipeline.remove("msgHandler");
          pipeline.remove(this);
          pipeline.remove("encoder");
          pipeline.remove("inbandWriter");

          pipeline.addLast("startTlsClientHandler", new StartTlsClientHandler(this.tlsTransportClient));

          logger.debug("Sending StartTlsRequest");
          ctx.writeAndFlush(Unpooled.wrappedBuffer("StartTlsRequest".getBytes())).addListener(new GenericFutureListener() {

            @Override
            public void operationComplete(Future f) throws Exception {
              if (!f.isSuccess()) {
                logger.error(f.cause().getMessage(), f.cause());
              }
            }
          });

        }
      }
    }

    ReferenceCountUtil.release(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(cause.getMessage(), cause);
  }

}
