/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.client.impl;

/*
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
import org.jdiameter.common.api.data.ISessionDatasource;

import java.util.concurrent.TimeUnit;

/**
 * Implementation for {@link ISession}
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SessionImpl extends BaseSessionImpl implements ISession {

  private static final long serialVersionUID = 1L;

  SessionImpl(IContainer container) {
    setContainer(container);
    sessionId = generateSessionId();
  }

  void setContainer(IContainer container) {
    this.container = container;
    this.parser = (IMessageParser) container.getAssemblerFacility().getComponentInstance(IMessageParser.class);
  }

  public void send(Message message, EventListener<Request, Answer> listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    genericSend(message,  listener);
  }

  public void send(Message message, EventListener<Request, Answer> listener, long timeout, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    genericSend(message, listener, timeout, timeUnit);
  }

  public void setRequestListener(NetworkReqListener listener) {
    if (listener != null) {
      super.reqListener = listener;
      container.addSessionListener(sessionId, listener);
    }
  }

  public NetworkReqListener getReqListener() {
    return super.reqListener;
  }

  public Request createRequest(int commandCode, ApplicationId appId, String destRealm) {
    if (isValid) {
      lastAccessedTime = System.currentTimeMillis();
      IRequest m = parser.createEmptyMessage(IRequest.class, commandCode, getAppId(appId));
      m.setNetworkRequest(false);
      m.setRequest(true);
      m.getAvps().addAvp(Avp.SESSION_ID, sessionId, true, false, false);
      appendAppId(appId, m);
      if (destRealm != null) {
        m.getAvps().addAvp(Avp.DESTINATION_REALM, destRealm, true, false, true);
      }
      MessageUtility.addOriginAvps(m, container.getMetaData());
      return m;
    }
    else {
      throw new IllegalStateException("Session already released");
    }
  }

  public Request createRequest(int commandCode, ApplicationId appId, String destRealm, String destHost) {
    if (isValid) {
      lastAccessedTime = System.currentTimeMillis();
      IRequest m = parser.createEmptyMessage(IRequest.class, commandCode, getAppId(appId));
      m.setNetworkRequest(false);
      m.setRequest(true);
      m.getAvps().addAvp(Avp.SESSION_ID, sessionId, true, false, false);
      appendAppId(appId, m);
      if (destRealm != null) {
        m.getAvps().addAvp(Avp.DESTINATION_REALM, destRealm, true, false, true);
      }
      if (destHost != null) {
        m.getAvps().addAvp(Avp.DESTINATION_HOST, destHost, true, false, true);
      }
      MessageUtility.addOriginAvps(m, container.getMetaData());
      return m;
    }
    else {
      throw new IllegalStateException("Session already released");
    }
  }

  public Request createRequest(Request prevRequest) {
    if (isValid) {
      lastAccessedTime = System.currentTimeMillis();
      IRequest request = parser.createEmptyMessage(Request.class, (IMessage) prevRequest);
      request.setRequest(true);
      request.setNetworkRequest(false);
      MessageUtility.addOriginAvps(request, container.getMetaData());
      return request;
    }
    else {
      throw new IllegalStateException("Session already released");
    }
  }

  public void release() {
    isValid = false;
    if (container != null) {
      container.removeSessionListener(sessionId);
      // FIXME
      container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class).removeSession(sessionId);
    }
    container = null;
    parser = null;
    reqListener = null;
  }

  public boolean isWrapperFor(Class<?> iface) throws InternalException {
    return iface == RawSession.class;
  }

  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> iface) throws InternalException {
    return (T) (iface == RawSession.class ?  new RawSessionImpl(container) : null);
  }
   
}
