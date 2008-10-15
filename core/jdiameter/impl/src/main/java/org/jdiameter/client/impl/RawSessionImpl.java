package org.jdiameter.client.impl;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.*;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;

import java.util.concurrent.TimeUnit;

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
           throw new IllegalAccessError("Session already released");
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
           throw new IllegalAccessError("Session already released");
        }
    }

    public Message createMessage(Message message, boolean copyAvps) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
            IMessage newMessage = null;
            IMessage inner = (IMessage) message;
            if (copyAvps) {
                newMessage = parser.createEmptyMessage(inner);
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
            throw new IllegalAccessError("Session already released");
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
