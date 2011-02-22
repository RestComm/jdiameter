/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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
public class CCASessionFactoryImpl implements ICCASessionFactory, ClientCCASessionListener, ServerCCASessionListener, StateChangeListener<AppSession>, ICCAMessageFactory, IServerCCASessionContext, IClientCCASessionContext {

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


  protected Logger logger = LoggerFactory.getLogger(CCASessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IAppSessionDataFactory<ICCASessionData> sessionDataFactory;
  public CCASessionFactoryImpl(){};

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
    if(this.sessionDataFactory == null) {
      logger.debug("No factory for CCA Application data, using default/local.");
      this.sessionDataFactory = new CCALocalSessionDataFactory();
    }
  }

  public CCASessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultCreditControlFailureHandling, long defaultValidityTime, long defaultTxTimerValue) {
    this(sessionFactory);

    this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
    this.defaultCreditControlFailureHandling = defaultCreditControlFailureHandling;
    this.defaultValidityTime = defaultValidityTime;
    this.defaultTxTimerValue = defaultTxTimerValue;
  }

  /**
   * @return the clientSessionListener
   */
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
  public void setClientSessionListener(ClientCCASessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
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
  public void setServerSessionListener(ServerCCASessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverContextListener
   */
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
  public void setServerContextListener(IServerCCASessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the clientContextListener
   */
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
  public void setMessageFactory(ICCAMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @param clientContextListener
   *          the clientContextListener to set
   */
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
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if(!this.iss.exists(sessionId)) {
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
        clientSession = new ClientCCASessionImpl(data, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());
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
        serverSession = new ServerCCASessionImpl(data, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());
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

  public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request) throws InternalException {

  }

  public void doCreditControlAnswer(ClientCCASession session, JCreditControlRequest request, JCreditControlAnswer answer) throws InternalException {

  }

  public void doReAuthRequest(ClientCCASession session, ReAuthRequest request) throws InternalException {

  }

  public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException {

  }

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {

  }

  // Message Factory Methods --------------------------------------------------

  public JCreditControlAnswer createCreditControlAnswer(Answer answer) {
    return new JCreditControlAnswerImpl(answer);
  }

  public JCreditControlRequest createCreditControlRequest(Request req) {
    return new JCreditControlRequestImpl(req);
  }

  public ReAuthAnswer createReAuthAnswer(Answer answer) {
    return new ReAuthAnswerImpl(answer);
  }

  public ReAuthRequest createReAuthRequest(Request req) {
    return new ReAuthRequestImpl(req);
  }

  // Context Methods ----------------------------------------------------------

  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter CCA SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter CCA SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  // FIXME: add ctx methods proxy calls!

  public void sessionSupervisionTimerExpired(ServerCCASession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerReStarted(ServerCCASession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStarted(ServerCCASession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStopped(ServerCCASession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  public void timeoutExpired(Request request) {
    // FIXME What should we do when there's a timeout?
  }

  public void denyAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
    // TODO Complete this method.
  }

  public void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
    // TODO Complete this method.
  }

  public void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
    clientCCASessionImpl.release();
  }

  public int getDefaultCCFHValue() {
    return defaultCreditControlFailureHandling;
  }

  public int getDefaultDDFHValue() {
    return defaultDirectDebitingFailureHandling;
  }

  public long getDefaultTxTimerValue() {
    return defaultTxTimerValue;
  }

  public void grantAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
    // TODO Auto-generated method stub
  }

  public void grantAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub  
  }

  public void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub
  }

  public void indicateServiceError(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub
  }

  public void txTimerExpired(ClientCCASession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  public long[] getApplicationIds() {
    // FIXME: What should we do here?
    return new long[] { 4 };
  }

  public long getDefaultValidityTime() {
    return this.defaultValidityTime;
  }

}