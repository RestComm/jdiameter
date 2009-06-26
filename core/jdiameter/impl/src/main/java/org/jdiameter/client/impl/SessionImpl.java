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
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.ISession;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.helpers.UIDGenerator;

import java.util.concurrent.TimeUnit;

public class SessionImpl extends BaseSessionImpl implements ISession{

    protected static UIDGenerator uid = new UIDGenerator();
    protected String sessionId;

    SessionImpl(IContainer container) {
        setContainer(container);
        setSessionId();
    }

    void setContainer(IContainer container) {
        this.container = container;
        this.parser = (IMessageParser) container.getAssemblerFacility().
                getComponentInstance(IMessageParser.class);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void send(Message message, EventListener<Request, Answer> listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        genericSend(message,  listener);
    }

    public void send(Message message, EventListener<Request, Answer> listener, long timeout, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        genericSend(message, listener, timeout, timeUnit);
    }

    public void setRequestListener(NetworkReqListener listener) {
        if (listener != null) {
            this.reqListener = listener;
            container.addSessionListener(sessionId, listener);
        }
    }

    public NetworkReqListener getReqListener() {
        return reqListener;
    }

    public Request createRequest(int commandCode, ApplicationId appId, String destRealm) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
            IRequest m = parser.createEmptyMessage(IRequest.class, commandCode, getAppId(appId));
            m.setNetworkRequest(false);
            m.setRequest(true);
            m.getAvps().addAvp(Avp.SESSION_ID, sessionId, true, false, false);
            appendAppId(appId, m);
            if (destRealm != null) m.getAvps().addAvp(Avp.DESTINATION_REALM, destRealm, true, false, true);
            return m;
        } else {
            throw new IllegalAccessError("Session already released");
        }
    }

    public Request createRequest(int commandCode, ApplicationId appId, String destRealm, String destHost) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
            IRequest m = parser.createEmptyMessage(IRequest.class, commandCode, getAppId(appId));
            m.setNetworkRequest(false);
            m.setRequest(true);
            m.getAvps().addAvp(Avp.SESSION_ID, sessionId, true, false, false);
            appendAppId(appId, m);
            if (destRealm != null) m.getAvps().addAvp(Avp.DESTINATION_REALM, destRealm, true, false, true);
            if (destHost != null) m.getAvps().addAvp(Avp.DESTINATION_HOST, destHost, true, false, true);
            return m;
        } else {
            throw new IllegalAccessError("Session already released");
        }
    }

    public Request createRequest(Request prevRequest) {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();
            IRequest request = parser.createEmptyMessage(Request.class, (IMessage) prevRequest);
            request.setRequest(true);
            request.setNetworkRequest(false);
            return request;
        } else {
            throw new IllegalAccessError("Session already released");
        }
    }

    public void release() {
        isValid = false;
        if (container != null)
            container.removeSessionListener(sessionId);
        container = null;
        parser = null;
        reqListener = null;
    }

    protected void setSessionId() {
        long id = uid.nextLong();
        long high32 = (id & 0xffffffff00000000L) >> 32;
        long low32 = (id & 0xffffffffL);
        sessionId = new StringBuffer().
                append(container.getMetaData().getLocalPeer().getUri().getFQDN()).
                append(";").append(high32).append(";").append(low32).
                toString();
    }

    public boolean isWrapperFor(Class<?> iface) throws InternalException {
        return iface == RawSession.class;
    }

    public <T> T unwrap(Class<T> iface) throws InternalException {
        return (T) (iface == RawSession.class ?  new RawSessionImpl(container) : null);
    }    
}
