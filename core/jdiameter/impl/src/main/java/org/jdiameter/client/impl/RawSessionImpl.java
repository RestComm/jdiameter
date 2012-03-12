/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RawSessionImpl extends BaseSessionImpl implements RawSession {

    RawSessionImpl(IContainer stack) {
        container = stack;
        this.parser = (IMessageParser) container.getAssemblerFacility().
                getComponentInstance(IMessageParser.class);
    }

    public  Message createMessage(int commandCode, ApplicationId appId, Avp... avps) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
            IMessage m = parser.createEmptyMessage(commandCode, getAppId(appId));
            m.getAvps().addAvp(avps);
            appendAppId(appId, m);
            return m;
        } else {
           throw new IllegalStateException("Session already released");
        }
    }

    public Message createMessage(int commandCode, ApplicationId appId, long hopByHopIdentifier, long endToEndIdentifier, Avp... avps) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
            IMessage m = parser.createEmptyMessage(commandCode, getAppId(appId));
            if (hopByHopIdentifier >= 0)
                m.setHopByHopIdentifier(-hopByHopIdentifier);
            if (endToEndIdentifier >=0)
                m.setEndToEndIdentifier(endToEndIdentifier);
            m.getAvps().addAvp(avps);
            appendAppId(appId, m);
            return m;
        } else {
           throw new IllegalStateException("Session already released");
        }
    }

    public Message createMessage(Message message, boolean copyAvps) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
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

    public void send(Message message, EventListener<Message, Message> listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        genericSend(message,  listener);
    }

    public void send(Message message, EventListener<Message, Message> listener, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        genericSend(message,  listener, timeOut, timeUnit);
    }

    public void release() {
        isValid = false;
        container = null;
        parser = null;
    }

    public boolean isWrapperFor(Class<?> iface) throws InternalException {
        return iface == Session.class;
    }

    public <T> T unwrap(Class<T> iface) throws InternalException {
        return (T) (iface == Session.class ?  new SessionImpl(container) : null);
    }
}
