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

import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class DiameterMessageEncoder extends MessageToByteEncoder<IMessage> {
  private static final Logger logger = LoggerFactory.getLogger(DiameterMessageEncoder.class);

  protected final IMessageParser parser;

  public DiameterMessageEncoder(IMessageParser parser) {
    this.parser = parser;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, IMessage msg, ByteBuf out) throws Exception {
    logger.debug("DiameterMessageEncoder");
    logger.debug("Encoding message command code {}", msg.getCommandCode());
    out.writeBytes(Unpooled.wrappedBuffer(parser.encodeMessage(msg)));
  }

}
