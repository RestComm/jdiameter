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

package org.jdiameter.common.impl.app.ro;

import java.util.concurrent.ScheduledFuture;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.ro.ClientRoSession;
import org.jdiameter.api.ro.ClientRoSessionListener;
import org.jdiameter.api.ro.ServerRoSession;
import org.jdiameter.api.ro.ServerRoSessionListener;
import org.jdiameter.api.ro.events.RoCreditControlAnswer;
import org.jdiameter.api.ro.events.RoCreditControlRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.ro.ClientRoSessionImpl;
import org.jdiameter.client.impl.app.ro.IClientRoSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.ro.IClientRoSessionContext;
import org.jdiameter.common.api.app.ro.IRoMessageFactory;
import org.jdiameter.common.api.app.ro.IRoSessionData;
import org.jdiameter.common.api.app.ro.IRoSessionFactory;
import org.jdiameter.common.api.app.ro.IServerRoSessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.server.impl.app.ro.IServerRoSessionData;
import org.jdiameter.server.impl.app.ro.ServerRoSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Ro Session Factory implementation
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RoSessionFactoryImpl implements IRoSessionFactory, ClientRoSessionListener, ServerRoSessionListener, StateChangeListener<AppSession>,
    IRoMessageFactory, IServerRoSessionContext, IClientRoSessionContext {

  // Message timeout value (in milliseconds)
  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultCreditControlFailureHandling = 0;

  // its seconds
  protected long defaultValidityTime = 60;
  protected long defaultTxTimerValue = 30;

  // local not replicated listeners:
  protected ClientRoSessionListener clientSessionListener;
  protected ServerRoSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected IServerRoSessionContext serverContextListener;
  protected IClientRoSessionContext clientContextListener;
  protected IRoMessageFactory messageFactory;

  protected static final Logger logger = LoggerFactory.getLogger(RoSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<IRoSessionData> sessionDataFactory;
  protected ISessionFactory sessionFactory = null;

  public RoSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IRoSessionData>) this.iss.getDataFactory(IRoSessionData.class);
  }

  public RoSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultCreditControlFailureHandling,
      long defaultValidityTime, long defaultTxTimerValue) {
    this(sessionFactory);

    this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
    this.defaultCreditControlFailureHandling = defaultCreditControlFailureHandling;
    this.defaultValidityTime = defaultValidityTime;
    this.defaultTxTimerValue = defaultTxTimerValue;
  }

  /**
   * @return the clientSessionListener
   */
  @Override
  public ClientRoSessionListener getClientSessionListener() {
    if (clientSessionListener != null) {
      return clientSessionListener;
    }
    else {
      return this;
    }
  }

  /**
   * @param clientSessionListener
   *          the clientSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientRoSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerRoSessionListener getServerSessionListener() {
    if (serverSessionListener != null) {
      return serverSessionListener;
    }
    else {
      return this;
    }
  }

  /**
   * @param serverSessionListener
   *          the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerRoSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverContextListener
   */
  @Override
  public IServerRoSessionContext getServerContextListener() {
    if (serverContextListener != null) {
      return serverContextListener;
    }
    else {
      return this;
    }
  }

  /**
   * @param serverContextListener
   *          the serverContextListener to set
   */
  @Override
  public void setServerContextListener(IServerRoSessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the clientContextListener
   */
  @Override
  public IClientRoSessionContext getClientContextListener() {
    if (clientContextListener != null) {
      return clientContextListener;
    }
    else {
      return this;
    }
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IRoMessageFactory getMessageFactory() {
    if (messageFactory != null) {
      return messageFactory;
    }
    else {
      return this;
    }
  }

  /**
   * @param messageFactory
   *          the messageFactory to set
   */
  @Override
  public void setMessageFactory(IRoMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @param clientContextListener
   *          the clientContextListener to set
   */
  @Override
  public void setClientContextListener(IClientRoSessionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  /**
   * @return the sessionFactory
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory
   *          the sessionFactory to set
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
  }

  /**
   * @return the stateListener
   */
  @Override
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
   *          the stateListener to set
   */
  @Override
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      //TODO:check for existence
      if (aClass == ClientRoSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        ClientRoSessionImpl clientSession = null;

        IClientRoSessionData sessionData = (IClientRoSessionData) this.sessionDataFactory.getAppSessionData(ClientRoSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        clientSession = new ClientRoSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerRoSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerRoSessionData sessionData = (IServerRoSessionData) this.sessionDataFactory.getAppSessionData(ServerRoSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ServerRoSessionImpl serverSession = new ServerRoSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());

        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRoSession.class + "," + ServerRoSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Ro Session.", e);
    }

    return appSession;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    AppSession appSession = null;
    if (sessionId == null) {
      throw new IllegalArgumentException("Session-Id must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }

    try {
      if (aClass == ClientRoSession.class) {
        IClientRoSessionData sessionData = (IClientRoSessionData) this.sessionDataFactory.getAppSessionData(ClientRoSession.class, sessionId);
        ClientRoSessionImpl clientSession = new ClientRoSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());
        // this goes first!
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerRoSession.class) {
        IServerRoSessionData sessionData = (IServerRoSessionData) this.sessionDataFactory.getAppSessionData(ServerRoSession.class, sessionId);
        ServerRoSessionImpl serverSession = new ServerRoSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());

        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRoSession.class + "," + ServerRoSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Credit-Control Session.", e);
    }

    return appSession;
  }

  // Message Handlers ---------------------------------------------------------

  @Override
  public void doCreditControlRequest(ServerRoSession session, RoCreditControlRequest request) throws InternalException {

  }

  @Override
  public void doCreditControlAnswer(ClientRoSession session, RoCreditControlRequest request, RoCreditControlAnswer answer) throws InternalException {

  }

  @Override
  public void doReAuthRequest(ClientRoSession session, ReAuthRequest request) throws InternalException {

  }

  @Override
  public void doReAuthAnswer(ServerRoSession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException {

  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {

  }

  // Message Factory Methods --------------------------------------------------

  @Override
  public RoCreditControlAnswer createCreditControlAnswer(Answer answer) {
    return new RoCreditControlAnswerImpl(answer);
  }

  @Override
  public RoCreditControlRequest createCreditControlRequest(Request req) {
    return new RoCreditControlRequestImpl(req);
  }

  @Override
  public ReAuthAnswer createReAuthAnswer(Answer answer) {
    return new ReAuthAnswerImpl(answer);
  }

  @Override
  public ReAuthRequest createReAuthRequest(Request req) {
    return new ReAuthRequestImpl(req);
  }

  // Context Methods ----------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Ro SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Ro SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  // FIXME: add ctx methods proxy calls!

  @Override
  public void sessionSupervisionTimerExpired(ServerRoSession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerReStarted(ServerRoSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStarted(ServerRoSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStopped(ServerRoSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void timeoutExpired(Request request) {
    // FIXME What should we do when there's a timeout?
  }

  @Override
  public void denyAccessOnDeliverFailure(ClientRoSession clientRoSessionImpl, Message request) {
    // TODO Complete this method.
  }

  @Override
  public void denyAccessOnFailureMessage(ClientRoSession clientRoSessionImpl) {
    // TODO Complete this method.
  }

  @Override
  public void denyAccessOnTxExpire(ClientRoSession clientRoSessionImpl) {
    // this.resourceAdaptor.sessionDestroyed(clientRoSessionImpl.getSessions().get(0).getSessionId(),
    // clientRoSessionImpl);
    clientRoSessionImpl.release();
  }

  @Override
  public int getDefaultCCFHValue() {
    return defaultCreditControlFailureHandling;
  }

  @Override
  public int getDefaultDDFHValue() {
    return defaultDirectDebitingFailureHandling;
  }

  @Override
  public long getDefaultTxTimerValue() {
    return defaultTxTimerValue;
  }

  @Override
  public void grantAccessOnDeliverFailure(ClientRoSession clientRoSessionImpl, Message request) {
    // TODO Auto-generated method stub
  }

  @Override
  public void grantAccessOnFailureMessage(ClientRoSession clientRoSessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void grantAccessOnTxExpire(ClientRoSession clientRoSessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void indicateServiceError(ClientRoSession clientRoSessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void txTimerExpired(ClientRoSession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @Override
  public long[] getApplicationIds() {
    // FIXME: What should we do here?
    return new long[] { 4 };
  }

  @Override
  public long getDefaultValidityTime() {
    return this.defaultValidityTime;
  }

}