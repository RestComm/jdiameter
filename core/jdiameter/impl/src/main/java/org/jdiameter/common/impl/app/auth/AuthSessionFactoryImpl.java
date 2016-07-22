/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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

package org.jdiameter.common.impl.app.auth;

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
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.auth.ClientAuthSessionImpl;
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
import org.jdiameter.server.impl.app.auth.ServerAuthSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Authorization Session Factory implementation
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AuthSessionFactoryImpl implements IAuthSessionFactory, IAuthMessageFactory, ServerAuthSessionListener,
    ClientAuthSessionListener, IClientAuthActionContext, IServerAuthActionContext, StateChangeListener<AppSession> {

  private static final long authAppId = 19301L;

  protected IAuthMessageFactory messageFactory;
  protected ServerAuthSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected ClientAuthSessionListener clientSessionListener;
  protected boolean stateles;
  protected long messageTimeout = 5000;
  protected static final Logger logger = LoggerFactory.getLogger(AuthSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IServerAuthActionContext serverSessionContext;
  protected IClientAuthActionContext clientSessionContext;
  protected IAppSessionDataFactory<IAuthSessionData> sessionDataFactory;

  public AuthSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IAuthSessionData>) this.iss.getDataFactory(IAuthSessionData.class);
  }

  /**
   * @return the clientSessionContext
   */
  @Override
  public IClientAuthActionContext getClientSessionContext() {
    return clientSessionContext != null ? clientSessionContext : this;
  }

  /**
   * @param clientSessionContext
   *            the clientSessionContext to set
   */
  @Override
  public void setClientSessionContext(IClientAuthActionContext clientSessionContext) {
    this.clientSessionContext = clientSessionContext;
  }

  /**
   * @return the serverSessionContext
   */
  @Override
  public IServerAuthActionContext getServerSessionContext() {
    return serverSessionContext != null ? serverSessionContext : this;
  }

  /**
   * @param serverSessionContext
   *            the serverSessionContext to set
   */
  @Override
  public void setServerSessionContext(IServerAuthActionContext serverSessionContext) {
    this.serverSessionContext = serverSessionContext;
  }

  /**
   * @return the messageTimeout
   */
  @Override
  public long getMessageTimeout() {
    return messageTimeout;
  }

  /**
   * @param messageTimeout
   *            the messageTimeout to set
   */
  @Override
  public void setMessageTimeout(long messageTimeout) {
    this.messageTimeout = messageTimeout;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IAuthMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  @Override
  public void setMessageFactory(IAuthMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerAuthSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerAuthSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the stateListener
   */
  @Override
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
   * @param stateListener
   *            the stateListener to set
   */
  @Override
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  /**
   * @return the clientSessionListener
   */
  @Override
  public ClientAuthSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientAuthSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  @Override
  public boolean isStateles() {
    return stateles;
  }

  @Override
  public void setStateles(boolean stateles) {
    this.stateles = stateles;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    try {
      if (aClass == ServerAuthSession.class) {
        IServerAuthSessionData sessionData = (IServerAuthSessionData) this.sessionDataFactory.getAppSessionData(ServerAuthSession.class, sessionId);
        ServerAuthSessionImpl session = new ServerAuthSessionImpl(sessionData, sessionFactory, getServerSessionListener(), getMessageFactory(),
            getStateListener(), getServerSessionContext(), messageTimeout, isStateles());

        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else {
        if (aClass == ClientAuthSession.class) {
          IClientAuthSessionData sessionData = (IClientAuthSessionData) this.sessionDataFactory.getAppSessionData(ClientAuthSession.class, sessionId);
          ClientAuthSessionImpl session = new ClientAuthSessionImpl(sessionData, sessionFactory, getClientSessionListener(), getMessageFactory(),
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

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      if (aClass == ServerAuthSession.class) {

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
        ServerAuthSessionImpl session = new ServerAuthSessionImpl(sessionData, sessionFactory, getServerSessionListener(), getMessageFactory(),
            getStateListener(), getServerSessionContext(), messageTimeout, isStateles());

        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else {
        if (aClass == ClientAuthSession.class) {
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
          ClientAuthSessionImpl session = new ClientAuthSessionImpl(sessionData, sessionFactory, getClientSessionListener(), getMessageFactory(),
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

  @Override
  public AppAnswerEvent createAuthAnswer(Answer answer) {
    return new AppAnswerEventImpl(answer);
  }

  @Override
  public AppRequestEvent createAuthRequest(Request request) {
    return new AppRequestEventImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.auth.IAuthMessageFactory#getAuthMessageCommandCode()
   */
  @Override
  public int getAuthMessageCommandCode() {
    return 258;
  }

  @Override
  public ApplicationId getApplicationId() {
    return ApplicationId.createByAuthAppId(authAppId);
  }

  // Message Handlers -------------------------------------------------------

  @Override
  public void doAbortSessionRequestEvent(ClientAuthSession appSession, AbortSessionRequest asr)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAbortSessionRequestEvent :: appSession[{}], ASR[{}]", appSession, asr);
  }

  @Override
  public void doAbortSessionAnswerEvent(ServerAuthSession appSession, AbortSessionAnswer asa)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAbortSessionAnswerEvent :: appSession[{}], ASA[{}]", appSession, asa);
  }

  @Override
  public void doSessionTerminationRequestEvent(ServerAuthSession appSession, SessionTermRequest str)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doSessionTerminationRequestEvent :: appSession[{}], STR[{}]", appSession, str);
  }

  @Override
  public void doSessionTerminationAnswerEvent(ClientAuthSession appSession, SessionTermAnswer sta)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doSessionTerminationAnswerEvent :: appSession[{}], STA[{}]", appSession, sta);
  }

  @Override
  public void doAuthRequestEvent(ServerAuthSession appSession, AppRequestEvent request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAuthRequestEvent :: appSession[{}], Request[{}]", appSession, request);
  }

  @Override
  public void doAuthAnswerEvent(ClientAuthSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAuthAnswerEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  @Override
  public void doReAuthRequestEvent(ClientAuthSession appSession, ReAuthRequest rar)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doReAuthRequestEvent :: appSession[{}], RAR[{}]", appSession, rar);
  }

  @Override
  public void doReAuthAnswerEvent(ServerAuthSession appSession, ReAuthRequest rar, ReAuthAnswer raa)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doReAuthAnswerEvent :: appSession[{}], RAR[{}], RAA[{}]", new Object[]{appSession, rar, raa});
  }

  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  // State Change Listener --------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Base AuthorizationSessionFactory  :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Base AuthorizationSessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]",
        new Object[] {source, oldState, newState});
  }

  // Context Methods --------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.common.api.app.auth.IClientAuthActionContext#accessTimeoutElapses(org.jdiameter.api.auth.ClientAuthSession)
   */
  @Override
  public void accessTimeoutElapses(ClientAuthSession session) throws InternalException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#createAccessTimer()
   */
  @Override
  public long getAccessTimeout() throws InternalException {
    return 20000;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#disconnectUserOrDev(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.Message)
   */
  @Override
  public void disconnectUserOrDev(ClientAuthSession session, Message request) throws InternalException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.common.api.app.auth.IServerAuthActionContext#
   * accessTimeoutElapses(org.jdiameter.api.auth.ServerAuthSession)
   */
  @Override
  public void accessTimeoutElapses(ServerAuthSession session) throws InternalException {
    // TODO Auto-generated method stub
  }

}
