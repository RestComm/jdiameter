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
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
import org.jdiameter.common.api.app.auth.IAuthSessionFactory;
import org.jdiameter.common.api.app.auth.IClientAuthActionContext;
import org.jdiameter.common.api.app.auth.IServerAuthActionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
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

  private final static long authAppId = 19301L;

  protected IAuthMessageFactory messageFactory;
  protected ServerAuthSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected ClientAuthSessionListener clientSessionListener;
  protected boolean stateles;
  protected long messageTimeout = 5000;
  protected Logger logger = LoggerFactory.getLogger(AuthSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IServerAuthActionContext serverSessionContext;
  protected IClientAuthActionContext clientSessionContext;

  public AuthSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  /**
   * @return the clientSessionContext
   */
  public IClientAuthActionContext getClientSessionContext() {
    if (this.clientSessionContext != null) {
      return clientSessionContext;
    }
    else {
      return this;
    }
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
    if (this.serverSessionContext != null) {
      return serverSessionContext;
    }
    else {
      return this;
    }
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
    if (this.messageFactory != null) {
      return messageFactory;
    }
    else {
      return this;
    }
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
    if (this.serverSessionListener != null) {
      return serverSessionListener;
    }
    else {
      return this;
    }
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
    if (this.stateListener != null) {
      return stateListener;
    }
    else {
      return this;
    }
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
    if (this.clientSessionListener != null) {
      return clientSessionListener;
    }
    else {
      return this;
    }
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

  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      if (aClass == ServerAuthSession.class) {
        Request request = null;
        if (args != null && args.length > 0) {
          request = (Request) args[0];
        }

        ServerAuthSessionImpl session = new ServerAuthSessionImpl(sessionId, sessionFactory, request, getServerSessionListener(),
            getMessageFactory(), getStateListener(), getServerSessionContext(), messageTimeout, isStateles());

        iss.addSession(session);
        // iss.setSessionListener(clientSession.getSessionId(),
        // (NetworkReqListener) appSession);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else {
        if (aClass == ClientAuthSession.class) {
          ClientAuthSessionImpl session = new ClientAuthSessionImpl(sessionId, sessionFactory, getClientSessionListener(),
              getMessageFactory(), getStateListener(), getClientSessionContext(), isStateles());

          iss.addSession(session);
          // iss.setSessionListener(clientSession.getSessionId(),
          // (NetworkReqListener) appSession);
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
    return 258;
  }

  public ApplicationId getApplicationId() {
    return ApplicationId.createByAuthAppId(authAppId);
  }

  // Message Handlers -------------------------------------------------------

  public void doAbortSessionRequestEvent(ClientAuthSession appSession, AbortSessionRequest asr) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAbortSessionRequestEvent :: appSession[{}], ASR[{}]", appSession, asr);
  }

  public void doAbortSessionAnswerEvent(ServerAuthSession appSession, AbortSessionAnswer asa) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAbortSessionAnswerEvent :: appSession[{}], ASA[{}]", appSession, asa);
  }

  public void doSessionTerminationRequestEvent(ServerAuthSession appSession, SessionTermRequest str) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doSessionTerminationRequestEvent :: appSession[{}], STR[{}]", appSession, str);

  }

  public void doSessionTerminationAnswerEvent(ClientAuthSession appSession, SessionTermAnswer sta) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doSessionTerminationAnswerEvent :: appSession[{}], STA[{}]", appSession, sta);

  }

  public void doAuthRequestEvent(ServerAuthSession appSession, AppRequestEvent request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAuthRequestEvent :: appSession[{}], Request[{}]", appSession, request);
  }

  public void doAuthAnswerEvent(ClientAuthSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doAuthAnswerEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  public void doReAuthRequestEvent(ClientAuthSession appSession, ReAuthRequest rar) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doReAuthRequestEvent :: appSession[{}], RAR[{}]", appSession, rar);
  }

  public void doReAuthAnswerEvent(ServerAuthSession appSession, ReAuthRequest rar, ReAuthAnswer raa) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doReAuthAnswerEvent :: appSession[{}], RAR[{}], RAA[{}]", new Object[]{appSession, rar, raa});
  }

  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AuthorizationSessionFactory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  // State Change Listener --------------------------------------------------

  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Base AuthorizationSessionFactory  :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Base AuthorizationSessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] {source, oldState, newState});
  }

  // Context Methods --------------------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @seeorg.jdiameter.common.api.app.auth.IClientAuthActionContext#accessTimeoutElapses(org.jdiameter.api.auth.ClientAuthSession)
   */
  public void accessTimeoutElapses(ClientAuthSession session) throws InternalException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#createAccessTimer()
   */
  public long getAccessTimeout() throws InternalException {
    return 20000;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.auth.IClientAuthActionContext#disconnectUserOrDev(org.jdiameter.api.auth.ClientAuthSession, org.jdiameter.api.Message)
   */
  public void disconnectUserOrDev(ClientAuthSession session, Message request) throws InternalException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.jdiameter.common.api.app.auth.IServerAuthActionContext#
   * accessTimeoutElapses(org.jdiameter.api.auth.ServerAuthSession)
   */
  public void accessTimeoutElapses(ServerAuthSession session) throws InternalException {
    // TODO Auto-generated method stub
  }

}
