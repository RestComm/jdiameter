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

import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.IMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class DiameterMessageHandler extends ChannelInboundHandlerAdapter {
  protected static final Logger logger = LoggerFactory.getLogger(DiameterMessageHandler.class);

  protected final TLSClientConnection parentConnection;
  protected boolean autoRelease;

  public DiameterMessageHandler(TLSClientConnection parentConnection, boolean autoRelease) {
    this.parentConnection = parentConnection;
    this.autoRelease = autoRelease;
  }

  public void setAutoRelease(boolean autoRelease) {
    this.autoRelease = autoRelease;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof IMessage) {
      IMessage m = (IMessage) msg;
      logger.debug("Received message {} TLS Transport {}", m.getCommandCode(), this.parentConnection.getKey());
      try {
        logger.debug("Passing message on to parent {}", this.parentConnection.getKey());
        parentConnection.onMessageReceived(m);
        logger.debug("Finished passing message on to parent {}", this.parentConnection.getKey());
      } catch (AvpDataException e) {
        logger.debug("Garbage was received. Discarding. {}", this.parentConnection.getKey());
        parentConnection.onAvpDataException(e);
      } finally {
        if (autoRelease) {
          ReferenceCountUtil.release(msg);
        } else {
          ctx.fireChannelRead(m);
        }
      }
    }
  }

}
