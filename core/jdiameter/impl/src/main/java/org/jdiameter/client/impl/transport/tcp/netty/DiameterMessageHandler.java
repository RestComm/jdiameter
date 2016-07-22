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

  protected final TCPClientConnection parentConnection;

  public DiameterMessageHandler(TCPClientConnection parentConnection) {
    this.parentConnection = parentConnection;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    logger.debug("Received message TCP Transport from [{}]", ctx.channel().remoteAddress());
    IMessage m = (IMessage) msg;
    try {
      logger.debug("Passing message on to parent");
      parentConnection.onMessageReceived(m);
      logger.debug("Finished passing message on to parent");
    } catch (AvpDataException e) {
      logger.debug("Garbage was received. Discarding.");
      parentConnection.onAvpDataException(e);
    } finally {
      ReferenceCountUtil.release(m);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(cause.getMessage(), cause);
  }
}
