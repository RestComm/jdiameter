/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.jdiameter.common.impl.app.slh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ClientSLhSessionListener;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slh.ServerSLhSessionListener;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.slh.IClientSLhSessionData;
import org.jdiameter.client.impl.app.slh.SLhClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.slh.ISLhMessageFactory;
import org.jdiameter.common.api.app.slh.ISLhSessionData;
import org.jdiameter.common.api.app.slh.ISLhSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.slh.IServerSLhSessionData;
import org.jdiameter.server.impl.app.slh.SLhServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */

public class SLhSessionFactoryImpl implements ISLhSessionFactory, ServerSLhSessionListener, ClientSLhSessionListener,
    ISLhMessageFactory, StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(SLhSessionFactoryImpl.class);

  protected ISessionFactory sessionFactory;

  protected ServerSLhSessionListener serverSessionListener;
  protected ClientSLhSessionListener clientSessionListener;

  protected ISLhMessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateListener;
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<ISLhSessionData> sessionDataFactory;

  public SLhSessionFactoryImpl() {
  };

  public SLhSessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    init(sessionFactory);
  }

  public void init(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<ISLhSessionData>) this.iss.getDataFactory(ISLhSessionData.class);
  }

  /**
    * @return the serverSessionListener
    */
  public ServerSLhSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
    * @param serverSessionListener the serverSessionListener to set
    */
  public void setServerSessionListener(ServerSLhSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
    * @return the serverSessionListener
    */
  public ClientSLhSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
    * @param clientSessionListener the clientSessionListener to set
    */
  public void setClientSessionListener(ClientSLhSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
    * @return the messageFactory
    */
  public ISLhMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
    * @param messageFactory the messageFactory to set
    */
  public void setMessageFactory(ISLhMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
    * @return the stateListener
    */
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
    * @param stateListener the stateListener to set
    */
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {
      if (aClass == ServerSLhSession.class) {
        IServerSLhSessionData sessionData = (IServerSLhSessionData) this.sessionDataFactory.getAppSessionData(ServerSLhSession.class, sessionId);
        SLhServerSessionImpl serverSession = new SLhServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else if (aClass == ClientSLhSession.class) {
        IClientSLhSessionData sessionData = (IClientSLhSessionData) this.sessionDataFactory.getAppSessionData(ClientSLhSession.class, sessionId);
        SLhClientSessionImpl clientSession = new SLhClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else {
        throw new IllegalArgumentException(
            "Wrong session class: " + aClass + ". Supported[" + ServerSLhSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new SLh Session.", e);
    }
    return appSession;
  }

  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;

    try {
      if (aClass == ServerSLhSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerSLhSessionData sessionData = (IServerSLhSessionData) this.sessionDataFactory.getAppSessionData(ServerSLhSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        SLhServerSessionImpl serverSession = new SLhServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());

        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else if (aClass == ClientSLhSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientSLhSessionData sessionData = (IClientSLhSessionData) this.sessionDataFactory.getAppSessionData(ClientSLhSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        SLhClientSessionImpl clientSession = new SLhClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());

        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else {
        throw new IllegalArgumentException(
            "Wrong session class: " + aClass + ". Supported[" + ServerSLhSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new SLh Session.", e);
    }
    return appSession;
  }

  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter SLh Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  public long getApplicationId() {
    return 16777291;
  }

  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter SLh Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  public LCSRoutingInfoAnswer createLCSRoutingInfoAnswer(Answer answer) {
    return new LCSRoutingInfoAnswerImpl(answer);
  }

  public LCSRoutingInfoRequest createLCSRoutingInfoRequest(Request request) {
    return new LCSRoutingInfoRequestImpl(request);
  }

  public void doLCSRoutingInfoRequestEvent(ServerSLhSession appSession, LCSRoutingInfoRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLh Session Factory :: doLCSRoutingInfoRequestEvent :: appSession[{}], Request[{}]", appSession, request);
  }

  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLh Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[] { appSession, request, answer });
  }

  public void doLCSRoutingInfoAnswerEvent(ClientSLhSession appSession, LCSRoutingInfoRequest request, LCSRoutingInfoAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info(
        "Diameter SLh Session Factory :: doLCSRoutingInfoAnswerEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[] { appSession, request, answer });
  }

}