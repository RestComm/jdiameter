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

package org.jdiameter.common.impl.app.slg;

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
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.slg.IClientSLgSessionData;
import org.jdiameter.client.impl.app.slg.SLgClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.api.app.slg.ISLgSessionData;
import org.jdiameter.common.api.app.slg.ISLgSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.slg.IServerSLgSessionData;
import org.jdiameter.server.impl.app.slg.SLgServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public class SLgSessionFactoryImpl implements ISLgSessionFactory, ServerSLgSessionListener, ClientSLgSessionListener,
    ISLgMessageFactory, StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(SLgSessionFactoryImpl.class);

  protected ISessionFactory sessionFactory;

  protected ServerSLgSessionListener serverSessionListener;
  protected ClientSLgSessionListener clientSessionListener;

  protected ISLgMessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateListener;
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<ISLgSessionData> sessionDataFactory;

  public SLgSessionFactoryImpl() {
  }

  public SLgSessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    init(sessionFactory);
  }

  public void init(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<ISLgSessionData>) this.iss.getDataFactory(ISLgSessionData.class);
  }

  /**
   * @return the serverSessionListener
   */
  public ServerSLgSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener the serverSessionListener to set
   */
  public void setServerSessionListener(ServerSLgSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  public ClientSLgSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param clientSessionListener the clientSessionListener to set
   */
  public void setClientSessionListener(ClientSLgSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the messageFactory
   */
  public ISLgMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory the messageFactory to set
   */
  public void setMessageFactory(ISLgMessageFactory messageFactory) {
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
      if (aClass == ServerSLgSession.class) {
        IServerSLgSessionData sessionData = (IServerSLgSessionData) this.sessionDataFactory
            .getAppSessionData(ServerSLgSession.class, sessionId);
        SLgServerSessionImpl serverSession = new SLgServerSessionImpl(sessionData, getMessageFactory(), sessionFactory,
            this.getServerSessionListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else if (aClass == ClientSLgSession.class) {
        IClientSLgSessionData sessionData = (IClientSLgSessionData) this.sessionDataFactory
            .getAppSessionData(ClientSLgSession.class, sessionId);
        SLgClientSessionImpl clientSession = new SLgClientSessionImpl(sessionData, getMessageFactory(), sessionFactory,
            this.getClientSessionListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else {
        throw new IllegalArgumentException(
            "Wrong session class: " + aClass + ". Supported[" + ServerSLgSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new SLg Session.", e);
    }
    return appSession;
  }

  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId,
                                  Object[] args) {
    AppSession appSession = null;

    try {
      if (aClass == ServerSLgSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerSLgSessionData sessionData = (IServerSLgSessionData) this.sessionDataFactory
            .getAppSessionData(ServerSLgSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        SLgServerSessionImpl serverSession = new SLgServerSessionImpl(sessionData, getMessageFactory(), sessionFactory,
            this.getServerSessionListener());

        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else if (aClass == ClientSLgSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientSLgSessionData sessionData = (IClientSLgSessionData) this.sessionDataFactory
            .getAppSessionData(ClientSLgSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        SLgClientSessionImpl clientSession = new SLgClientSessionImpl(sessionData, getMessageFactory(), sessionFactory,
            this.getClientSessionListener());

        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else {
        throw new IllegalArgumentException(
            "Wrong session class: " + aClass + ". Supported[" + ServerSLgSession.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new SLg Session.", e);
    }
    return appSession;
  }

  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter SLg Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  public long getApplicationId() {
    return 16777255;
  }

  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter SLg Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]",
        new Object[] { source, oldState, newState });
  }

  public ProvideLocationRequest createProvideLocationRequest(Request request) {
    return new ProvideLocationRequestImpl(request);
  }

  public ProvideLocationAnswer createProvideLocationAnswer(Answer answer) {
    return new ProvideLocationAnswerImpl(answer);
  }

  public void doProvideLocationRequestEvent(ServerSLgSession appSession, ProvideLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLg Session Factory :: doProvideLocationRequestEvent :: appSession[{}], Request[{}]", appSession,
        request);
  }

  public void doProvideLocationAnswerEvent(ClientSLgSession appSession, ProvideLocationRequest request,
                                           ProvideLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLg Session Factory :: doProvideLocationAnswerEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[] { appSession, request, answer });
  }

  public LocationReportRequest createLocationReportRequest(Request request) {
    return new LocationReportRequestImpl(request);
  }

  public LocationReportAnswer createLocationReportAnswer(Answer answer) {
    return new LocationReportAnswerImpl(answer);
  }

  public void doLocationReportRequestEvent(ClientSLgSession appSession, LocationReportRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLg Session Factory :: doLocationReportRequestEvent :: appSession[{}], Request[{}]", appSession,
        request);
  }

  public void doLocationReportAnswerEvent(ServerSLgSession appSession, LocationReportRequest request,
                                          LocationReportAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLg Session Factory :: doLocationReportAnswerEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[] { appSession, request, answer });
  }

  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter SLg Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[] { appSession, request, answer });
  }

}