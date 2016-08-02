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
import org.jdiameter.client.api.IMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public class InbandSecurityHandler extends ChannelOutboundHandlerAdapter {
  protected static final Logger logger = LoggerFactory.getLogger(InbandSecurityHandler.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    logger.debug("InbandSecurityHandler");
    IMessage message = (IMessage) msg;
    if (message.getCommandCode() == IMessage.CAPABILITIES_EXCHANGE_REQUEST
        || message.getCommandCode() == IMessage.CAPABILITIES_EXCHANGE_ANSWER) {
      logger.debug("Writing inband security for CER/CEA msg {}", message.getCommandCode());
      AvpSet set = message.getAvps();
      set.removeAvp(Avp.INBAND_SECURITY_ID);
      set.addAvp(Avp.INBAND_SECURITY_ID, 1);
    }
    ctx.write(msg, promise);
  }
}
