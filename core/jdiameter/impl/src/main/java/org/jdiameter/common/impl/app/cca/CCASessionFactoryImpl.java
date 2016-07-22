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

package org.jdiameter.common.impl.app.cca;

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
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.cca.ClientCCASessionImpl;
import org.jdiameter.client.impl.app.cca.IClientCCASessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.cca.ICCAMessageFactory;
import org.jdiameter.common.api.app.cca.ICCASessionData;
import org.jdiameter.common.api.app.cca.ICCASessionFactory;
import org.jdiameter.common.api.app.cca.IClientCCASessionContext;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.server.impl.app.cca.IServerCCASessionData;
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CCASessionFactoryImpl implements ICCASessionFactory, ClientCCASessionListener, ServerCCASessionListener, StateChangeListener<AppSession>,
    ICCAMessageFactory, IServerCCASessionContext, IClientCCASessionContext {

  // Message timeout value (in milliseconds)
  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultCreditControlFailureHandling = 0;

  // its seconds
  protected long defaultValidityTime = 60;
  protected long defaultTxTimerValue = 30;

  // local not replicated listeners:
  protected ClientCCASessionListener clientSessionListener;
  protected ServerCCASessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected IServerCCASessionContext serverContextListener;
  protected IClientCCASessionContext clientContextListener;
  protected ICCAMessageFactory messageFactory;


  protected static final Logger logger = LoggerFactory.getLogger(CCASessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IAppSessionDataFactory<ICCASessionData> sessionDataFactory;
  public CCASessionFactoryImpl() {};

  public CCASessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    init(sessionFactory);
  }

  /**
   * @param sessionFactory2
   */
  public void init(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);

    this.sessionDataFactory = (IAppSessionDataFactory<ICCASessionData>) this.iss.getDataFactory(ICCASessionData.class);
    if (this.sessionDataFactory == null) {
      logger.debug("No factory for CCA Application data, using default/local.");
      this.sessionDataFactory = new CCALocalSessionDataFactory();
    }
  }

  public CCASessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultCreditControlFailureHandling,
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
  public ClientCCASessionListener getClientSessionListener() {
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
  public void setClientSessionListener(ClientCCASessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerCCASessionListener getServerSessionListener() {
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
  public void setServerSessionListener(ServerCCASessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverContextListener
   */
  @Override
  public IServerCCASessionContext getServerContextListener() {
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
  public void setServerContextListener(IServerCCASessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the clientContextListener
   */
  @Override
  public IClientCCASessionContext getClientContextListener() {
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
  public ICCAMessageFactory getMessageFactory() {
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
  public void setMessageFactory(ICCAMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @param clientContextListener
   *          the clientContextListener to set
   */
  @Override
  public void setClientContextListener(IClientCCASessionContext clientContextListener) {
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
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {
      if (aClass == ClientCCASession.class) {
        ClientCCASessionImpl clientSession = null;
        IClientCCASessionData data = (IClientCCASessionData) this.sessionDataFactory.getAppSessionData(ClientCCASession.class, sessionId);

        clientSession = new ClientCCASessionImpl(data, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerCCASession.class) {
        ServerCCASessionImpl serverSession = null;
        IServerCCASessionData data = (IServerCCASessionData) this.sessionDataFactory.getAppSessionData(ServerCCASession.class, sessionId);

        serverSession = new ServerCCASessionImpl(data, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientCCASession.class + "," + ServerCCASession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Credit-Control Session.", e);
    }

    return appSession;
  }
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      //TODO: add check to test if session exists.
      if (aClass == ClientCCASession.class) {
        ClientCCASessionImpl clientSession = null;
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientCCASessionData data = (IClientCCASessionData) this.sessionDataFactory.getAppSessionData(ClientCCASession.class, sessionId);
        data.setApplicationId(applicationId);
        clientSession = new ClientCCASessionImpl(data, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerCCASession.class) {
        ServerCCASessionImpl serverSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerCCASessionData data = (IServerCCASessionData) this.sessionDataFactory.getAppSessionData(ServerCCASession.class, sessionId);
        data.setApplicationId(applicationId);
        serverSession = new ServerCCASessionImpl(data, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());
        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientCCASession.class + "," + ServerCCASession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Credit-Control Session.", e);
    }

    return appSession;
  }

  // default implementation of methods so there are no exception!
  // ------------------------------------------------

  // Message Handlers ---------------------------------------------------------

  @Override
  public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request) throws InternalException {

  }

  @Override
  public void doCreditControlAnswer(ClientCCASession session, JCreditControlRequest request, JCreditControlAnswer answer) throws InternalException {

  }

  @Override
  public void doReAuthRequest(ClientCCASession session, ReAuthRequest request) throws InternalException {

  }

  @Override
  public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException {

  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {

  }

  // Message Factory Methods --------------------------------------------------

  @Override
  public JCreditControlAnswer createCreditControlAnswer(Answer answer) {
    return new JCreditControlAnswerImpl(answer);
  }

  @Override
  public JCreditControlRequest createCreditControlRequest(Request req) {
    return new JCreditControlRequestImpl(req);
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
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter CCA SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter CCA SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  // FIXME: add ctx methods proxy calls!

  @Override
  public void sessionSupervisionTimerExpired(ServerCCASession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @Override
  public void sessionSupervisionTimerReStarted(ServerCCASession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void sessionSupervisionTimerStarted(ServerCCASession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void sessionSupervisionTimerStopped(ServerCCASession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void timeoutExpired(Request request) {
    // FIXME What should we do when there's a timeout?
  }

  @Override
  public void denyAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
    // TODO Complete this method.
  }

  @Override
  public void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
    // TODO Complete this method.
  }

  @Override
  public void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
    clientCCASessionImpl.release();
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
  public void grantAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
    // TODO Auto-generated method stub
  }

  @Override
  public void grantAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void indicateServiceError(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void txTimerExpired(ClientCCASession session) {
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