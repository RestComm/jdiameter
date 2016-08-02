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

package org.jdiameter.common.impl.app.s13;

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
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ClientS13SessionListener;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.api.s13.ServerS13SessionListener;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.s13.IClientS13SessionData;
import org.jdiameter.client.impl.app.s13.S13ClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s13.IS13MessageFactory;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.app.s13.IS13SessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.s13.IServerS13SessionData;
import org.jdiameter.server.impl.app.s13.S13ServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S13SessionFactoryImpl implements IS13SessionFactory, ServerS13SessionListener, ClientS13SessionListener, IS13MessageFactory,
    StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(S13SessionFactoryImpl.class);

  protected ISessionFactory sessionFactory;

  protected ServerS13SessionListener serverSessionListener;
  protected ClientS13SessionListener clientSessionListener;

  protected IS13MessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateListener;
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<IS13SessionData> sessionDataFactory;

  public S13SessionFactoryImpl() {};

  public S13SessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    init(sessionFactory);
  }

  public void init(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IS13SessionData>) this.iss.getDataFactory(IS13SessionData.class);
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerS13SessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerS13SessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ClientS13SessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param serverSessionListener the serverSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientS13SessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IS13MessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory the messageFactory to set
   */
  @Override
  public void setMessageFactory(IS13MessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the stateListener
   */
  @Override
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
   * @param stateListener the stateListener to set
   */
  @Override
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {
      if (aClass == ServerS13Session.class) {
        IServerS13SessionData sessionData = (IServerS13SessionData) this.sessionDataFactory.getAppSessionData(ServerS13Session.class, sessionId);
        S13ServerSessionImpl serverSession = new S13ServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else if (aClass == ClientS13Session.class) {
        IClientS13SessionData sessionData = (IClientS13SessionData) this.sessionDataFactory.getAppSessionData(ClientS13Session.class, sessionId);
        S13ClientSessionImpl clientSession = new S13ClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerS13Session.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new S13 Session.", e);
    }
    return appSession;
  }

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;

    try {
      if (aClass == ServerS13Session.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerS13SessionData sessionData = (IServerS13SessionData) this.sessionDataFactory.getAppSessionData(ServerS13Session.class, sessionId);
        sessionData.setApplicationId(applicationId);
        S13ServerSessionImpl serverSession = new S13ServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());

        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      } else if (aClass == ClientS13Session.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          } else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientS13SessionData sessionData = (IClientS13SessionData) this.sessionDataFactory.getAppSessionData(ClientS13Session.class, sessionId);
        sessionData.setApplicationId(applicationId);
        S13ClientSessionImpl clientSession = new S13ClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());

        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      } else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerS13Session.class + "]");
      }
    } catch (Exception e) {
      logger.error("Failure to obtain new S13 Session.", e);
    }
    return appSession;
  }

  @Override
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter S13 Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  @Override
  public long getApplicationId() {
    return 16777252;
  }

  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter S13 Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  @Override
  public JMEIdentityCheckAnswer createMEIdentityCheckAnswer(Answer answer) {
    return new JMEIdentityCheckAnswerImpl(answer);
  }

  @Override
  public JMEIdentityCheckRequest createMEIdentityCheckRequest(Request request) {
    return new JMEIdentityCheckRequestImpl(request);
  }

  @Override
  public void doMEIdentityCheckRequestEvent(ServerS13Session appSession, JMEIdentityCheckRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S13 Session Factory :: doMEIdentityCheckRequestEvent :: appSession[{}], Request[{}]", appSession, request);
  }

  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S13 Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doMEIdentityCheckAnswerEvent(ClientS13Session appSession, JMEIdentityCheckRequest request, JMEIdentityCheckAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S13 Session Factory :: doMEIdentityCheckAnswerEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }
}
