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
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.client.impl;

import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RawSession;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RawSessionImpl extends BaseSessionImpl implements RawSession {

  RawSessionImpl(IContainer stack) {
    container = stack;
    this.parser = container.getAssemblerFacility().
        getComponentInstance(IMessageParser.class);
  }

  @Override
  public  Message createMessage(int commandCode, ApplicationId appId, Avp... avps) {
    if ( isValid ) {
      setLastAccessTime();
      IMessage m = parser.createEmptyMessage(commandCode, getAppId(appId));
      m.getAvps().addAvp(avps);
      appendAppId(appId, m);
      return m;
    } else {
      throw new IllegalStateException("Session already released");
    }
  }

  @Override
  public Message createMessage(int commandCode, ApplicationId appId, long hopByHopIdentifier, long endToEndIdentifier, Avp... avps) {
    if ( isValid ) {
      setLastAccessTime();
      IMessage m = parser.createEmptyMessage(commandCode, getAppId(appId));
      if (hopByHopIdentifier >= 0) {
        m.setHopByHopIdentifier(-hopByHopIdentifier);
      }
      if (endToEndIdentifier >= 0) {
        m.setEndToEndIdentifier(endToEndIdentifier);
      }
      m.getAvps().addAvp(avps);
      appendAppId(appId, m);
      return m;
    } else {
      throw new IllegalStateException("Session already released");
    }
  }

  @Override
  public Message createMessage(Message message, boolean copyAvps) {
    if ( isValid ) {
      setLastAccessTime();
      IMessage newMessage = null;
      IMessage inner = (IMessage) message;
      if (copyAvps) {
        newMessage = parser.createEmptyMessage(inner);
        MessageUtility.addOriginAvps(newMessage, container.getMetaData());
      } else {
        newMessage = (IMessage) createMessage(
            inner.getCommandCode(),
            inner.getSingleApplicationId(),
            -1,
            -1
            );
      }
      newMessage.setRequest(message.isRequest());
      newMessage.setProxiable(message.isProxiable());
      newMessage.setError(message.isError());
      newMessage.setReTransmitted(message.isReTransmitted());
      return newMessage;

    } else {
      throw new IllegalStateException("Session already released");
    }
  }

  @Override
  public void send(Message message, EventListener<Message, Message> listener)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    genericSend(message,  listener);
  }

  @Override
  public void send(Message message, EventListener<Message, Message> listener, long timeOut, TimeUnit timeUnit)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    genericSend(message,  listener, timeOut, timeUnit);
  }

  @Override
  public void release() {
    isValid = false;
    container = null;
    parser = null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws InternalException {
    return iface == Session.class;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws InternalException {
    return (T) (iface == Session.class ?  new SessionImpl(container) : null);
  }
}
