/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.impl.app.gq;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.gq.GqClientSession;
import org.jdiameter.api.gq.GqServerSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.gq.GqClientSessionImpl;
import org.jdiameter.client.impl.app.auth.IClientAuthSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
import org.jdiameter.common.api.app.auth.IAuthSessionData;
import org.jdiameter.common.api.app.auth.IAuthSessionFactory;
import org.jdiameter.common.api.app.auth.IClientAuthActionContext;
import org.jdiameter.common.api.app.auth.IServerAuthActionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.server.impl.app.auth.IServerAuthSessionData;
import org.jdiameter.server.impl.app.gq.GqServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Gq Application Session Factory implementation
 * 
 * @author <a href="mailto:webdev@web-ukraine.info"> Yulian Oifa </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class GqSessionFactoryImpl implements IAuthSessionFactory, IAuthMessageFactory, ServerAuthSessionListener,
ClientAuthSessionListener, IClientAuthActionContext, IServerAuthActionContext, StateChangeListener<AppSession> {

  private final static long authAppId = 16777222L;

  protected IAuthMessageFactory messageFactory;
  protected ServerAuthSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected ClientAuthSessionListener clientSessionListener;
  protected boolean stateles;
  protected long messageTimeout = 5000;
  protected final static Logger logger = LoggerFactory.getLogger(GqSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IServerAuthActionContext serverSessionContext;
  protected IClientAuthActionContext clientSessionContext;
  protected IAppSessionDataFactory<IAuthSessionData> sessionDataFactory;

  public GqSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IAuthSessionData>) this.iss.getDataFactory(IAuthSessionData.class);
  }

  /**
   * @return the clientSessionContext
   */
  public IClientAuthActionContext getClientSessionContext() {
    return clientSessionContext != null ? clientSessionContext : this;
  }

  /**
   * @param clientSessionContext
   *            the clientSessionContext to set
   */
  public void setClientSessionContext(IClientAuthActionContext clientSessionContext) {
    this.clientSessionContext = clientSessionContext;
  }

  /**
   * @return the serverSessionContext
   */
  public IServerAuthActionContext getServerSessionContext() {
    return serverSessionContext != null ? serverSessionContext : this;
  }

  /**
   * @param serverSessionContext
   *            the serverSessionContext to set
   */
  public void setServerSessionContext(IServerAuthActionContext serverSessionContext) {
    this.serverSessionContext = serverSessionContext;
  }

  /**
   * @return the messageTimeout
   */
  public long getMessageTimeout() {
    return messageTimeout;
  }

  /**
   * @param messageTimeout
   *            the messageTimeout to set
   */
  public void setMessageTimeout(long messageTimeout) {
    this.messageTimeout = messageTimeout;
  }

  /**
   * @return the messageFactory
   */
  public IAuthMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  public void setMessageFactory(IAuthMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the serverSessionListener
   */
  public ServerAuthSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  public void setServerSessionListener(ServerAuthSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the stateListener
   */
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
   * @param stateListener
   *            the stateListener to set
   */
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  /**
   * @return the clientSessionListener
   */
  public ClientAuthSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  public void setClientSessionListener(ClientAuthSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  public boolean isStateles() {
    return stateles;
  }

  public void setStateles(boolean stateles) {
    this.stateles = stateles;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if(!this.iss.exists(sessionId)) {
      return null;
    }
    try {
      if (aClass == GqServerSession.class) {
        IServerAuthSessionData sessionData = (IServerAuthSessionData) this.sessionDataFactory.getAppSessionData(GqServerSession.class, sessionId);
        GqServerSessionImpl session = new GqServerSessionImpl(sessionData, sessionFactory, getServerSessionListener(), getMessageFactory(),
            getStateListener(), getServerSessionContext(), messageTimeout, isStateles());

        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else {
        if (aClass == GqClientSession.class) {
          IClientAuthSessionData sessionData = (IClientAuthSessionData) this.sessionDataFactory.getAppSessionData(GqClientSession.class, sessionId);
          GqClientSessionImpl session = new GqClientSessionImpl(sessionData, sessionFactory, getClientSessionListener(), getMessageFactory(),
              getStateListener(), getClientSessionContext(), isStateles());

          session.getSessions().get(0).setRequestListener(session);
          return session;
        }
      }
    }
    catch (Exception e) {
      logger.error("Failure trying to obtain new authorization session", e);
    }

    return null;
  }

  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      if (aClass == GqServerSession.class) {

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }

        IServerAuthSessionData sessionData = (IServerAuthSessionData) this.sessionDataFactory.getAppSessionData(ServerAuthSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        GqServerSessionImpl session = new GqServerSessionImpl(sessionData, sessionFactory, getServerSessionListener(), getMessageFactory(),
            getStateListener(), getServerSessionContext(), messageTimeout, isStateles());

        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else {
        if (aClass == GqClientSession.class) {
          if (sessionId == null) {
            if (args != null && args.length > 0 && args[0] instanceof Request) {
              Request request = (Request) args[0];
              sessionId = request.getSessionId();
            }
            else {
              sessionId = this.sessionFactory.getSessionId();
            }
          }
          IClientAuthSessionData sessionData = (IClientAuthSessionData) this.sessionDataFactory.getAppSessionData(ClientAuthSession.class, sessionId);
          sessionData.setApplicationId(applicationId);
          GqClientSessionImpl session = new GqClientSessionImpl(sessionData, sessionFactory, getClientSessionListener(), getMessageFactory(),
              getStateListener(), getClientSessionContext(), isStateles());

          iss.addSession(session);
          // iss.setSessionListener(clientSession.getSessionId(), (NetworkReqListener) appSession);
          session.getSessions().get(0).setRequestListener(session);
          return session;
        }
      }
    }
    catch (Exception e) {
      logger.error("Failure trying to obtain new authorization session", e);
    }

    return null;
  }

  // Message Factory Methods ------------------------------------------------

  public AppAnswerEvent createAuthAnswer(Answer answer) {
    return new AppAnswerEventImpl(answer);
  }

  public AppRequestEvent createAuthRequest(Request request) {
    return new AppRequestEventImpl(request);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.auth.IAuthMessageFactory#getAuthMessageCommandCode()
   */
  public int getAuthMessageCommandCode() {
    return 265;
  }

  public ApplicationId getApplicationId() {
    return ApplicationId.createByAuthAppId(authAppId);
  }

  // Message Handlers -------------------------------------------------------

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ClientAuthSessionListener#doAbortSessionRequestEvent(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.auth.events.AbortSessionRequest)
   */
  public void doAbortSessionRequestEvent(ClientAuthSession appSession, AbortSessionRequest asr) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doAbortSessionRequestEvent :: appSession[{}], ASR[{}]", appSession, asr);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ServerAuthSessionListener#doAbortSessionAnswerEvent(org.jdiameter.api.auth.ServerAuthSession, org.jdiameter.api.auth.events.AbortSessionAnswer)
   */
  public void doAbortSessionAnswerEvent(ServerAuthSession appSession, AbortSessionAnswer asa) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doAbortSessionAnswerEvent :: appSession[{}], ASA[{}]", appSession, asa);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ServerAuthSessionListener#doSessionTerminationRequestEvent(org.jdiameter.api.auth.ServerAuthSession, org.jdiameter.api.auth.events.SessionTermRequest)
   */
  public void doSessionTerminationRequestEvent(ServerAuthSession appSession, SessionTermRequest str) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doSessionTerminationRequestEvent :: appSession[{}], STR[{}]", appSession, str);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ClientAuthSessionListener#doSessionTerminationAnswerEvent(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.auth.events.SessionTermAnswer)
   */
  public void doSessionTerminationAnswerEvent(ClientAuthSession appSession, SessionTermAnswer sta) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doSessionTerminationAnswerEvent :: appSession[{}], STA[{}]", appSession, sta);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ServerAuthSessionListener#doAuthRequestEvent(org.jdiameter.api.auth.ServerAuthSession, org.jdiameter.api.app.AppRequestEvent)
   */
  public void doAuthRequestEvent(ServerAuthSession appSession, AppRequestEvent request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doAuthRequestEvent :: appSession[{}], Request[{}]", appSession, request);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ClientAuthSessionListener#doAuthAnswerEvent(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  public void doAuthAnswerEvent(ClientAuthSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doAuthAnswerEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ClientAuthSessionListener#doReAuthRequestEvent(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.auth.events.ReAuthRequest)
   */
  public void doReAuthRequestEvent(ClientAuthSession appSession, ReAuthRequest rar) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doReAuthRequestEvent :: appSession[{}], RAR[{}]", appSession, rar);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ServerAuthSessionListener#doReAuthAnswerEvent(org.jdiameter.api.auth.ServerAuthSession, org.jdiameter.api.auth.events.ReAuthRequest, org.jdiameter.api.auth.events.ReAuthAnswer)
   */
  public void doReAuthAnswerEvent(ServerAuthSession appSession, ReAuthRequest rar, ReAuthAnswer raa) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doReAuthAnswerEvent :: appSession[{}], RAR[{}], RAA[{}]", new Object[]{appSession, rar, raa});
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.auth.ServerAuthSessionListener#doOtherEvent(org.jdiameter.api.app.AppSession, org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Gq AuthorizationSessionFactory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  // State Change Listener --------------------------------------------------

  /* (non-Javadoc)
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum, java.lang.Enum)
   */
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Gq AuthorizationSessionFactory  :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Gq AuthorizationSessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] {source, oldState, newState});
  }

  // Context Methods --------------------------------------------------------

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#accessTimeoutElapses(org.jdiameter.api.auth.ClientAuthSession)
   */
  public void accessTimeoutElapses(ClientAuthSession session) throws InternalException {
    // TODO Auto-generated method stub
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#getAccessTimeout()
   */
  public long getAccessTimeout() throws InternalException {
    return 20000;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#disconnectUserOrDev(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.Message)
   */
  public void disconnectUserOrDev(ClientAuthSession session, Message request) throws InternalException {
    // TODO Auto-generated method stub
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.auth.IServerAuthActionContext#accessTimeoutElapses(org.jdiameter.api.auth.ServerAuthSession)
   */
  public void accessTimeoutElapses(ServerAuthSession session) throws InternalException {
    // TODO Auto-generated method stub
  }
}
