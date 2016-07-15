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

package org.jdiameter.common.impl.app.gx;

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
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.ClientGxSessionListener;
import org.jdiameter.api.gx.ServerGxSession;
import org.jdiameter.api.gx.ServerGxSessionListener;
import org.jdiameter.api.gx.events.GxCreditControlAnswer;
import org.jdiameter.api.gx.events.GxCreditControlRequest;
import org.jdiameter.api.gx.events.GxReAuthAnswer;
import org.jdiameter.api.gx.events.GxReAuthRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.gx.ClientGxSessionImpl;
import org.jdiameter.client.impl.app.gx.IClientGxSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.gx.IClientGxSessionContext;
import org.jdiameter.common.api.app.gx.IGxMessageFactory;
import org.jdiameter.common.api.app.gx.IGxSessionData;
import org.jdiameter.common.api.app.gx.IGxSessionFactory;
import org.jdiameter.common.api.app.gx.IServerGxSessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.gx.IServerGxSessionData;
import org.jdiameter.server.impl.app.gx.ServerGxSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Gx Session Factory implementation.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:carl-magnus.bjorkell@emblacom.com"> Carl-Magnus Björkell </a>
 */
public class GxSessionFactoryImpl implements IGxSessionFactory, ClientGxSessionListener, ServerGxSessionListener, StateChangeListener<AppSession>,
    IGxMessageFactory, IServerGxSessionContext, IClientGxSessionContext {

  // Message timeout value (in milliseconds)
  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultCreditControlFailureHandling = 0;
  // its seconds
  protected long defaultValidityTime = 60;
  protected long defaultTxTimerValue = 30;
  // local not replicated listeners:
  protected ClientGxSessionListener clientSessionListener;
  protected ServerGxSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected IServerGxSessionContext serverContextListener;
  protected IClientGxSessionContext clientContextListener;
  protected IGxMessageFactory messageFactory;
  protected static final Logger logger = LoggerFactory.getLogger(GxSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IAppSessionDataFactory<IGxSessionData> sessionDataFactory;

  public GxSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IGxSessionData>) this.iss.getDataFactory(IGxSessionData.class);

  }

  public GxSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultCreditControlFailureHandling,
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
  public ClientGxSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param clientSessionListener
   *          the clientSessionListener to set
   */
  @Override
  public void setClientSessionListener(final ClientGxSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerGxSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *          the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerGxSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverContextListener
   */
  @Override
  public IServerGxSessionContext getServerContextListener() {
    return serverContextListener != null ? serverContextListener : this;
  }

  /**
   * @param serverContextListener
   *          the serverContextListener to set
   */
  @Override
  public void setServerContextListener(IServerGxSessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the clientContextListener
   */
  @Override
  public IClientGxSessionContext getClientContextListener() {
    return clientContextListener != null ? clientContextListener : this;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IGxMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *          the messageFactory to set
   */
  @Override
  public void setMessageFactory(final IGxMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @param clientContextListener
   *          the clientContextListener to set
   */
  @Override
  public void setClientContextListener(IClientGxSessionContext clientContextListener) {
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
    return stateListener != null ? stateListener : this;
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
      if (aClass == ClientGxSession.class) {
        IClientGxSessionData sessionData =  (IClientGxSessionData) this.sessionDataFactory.getAppSessionData(ClientGxSession.class, sessionId);
        ClientGxSessionImpl clientSession = new ClientGxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());

        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerGxSession.class) {
        IServerGxSessionData sessionData =  (IServerGxSessionData) this.sessionDataFactory.getAppSessionData(ServerGxSession.class, sessionId);
        ServerGxSessionImpl serverSession = new ServerGxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());

        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientGxSession.class + "," + ServerGxSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Gx Session.", e);
    }

    return appSession;
  }

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      // FIXME:
      if (aClass == ClientGxSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientGxSessionData sessionData =  (IClientGxSessionData) this.sessionDataFactory.getAppSessionData(ClientGxSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ClientGxSessionImpl clientSession = new ClientGxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerGxSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerGxSessionData sessionData =  (IServerGxSessionData) this.sessionDataFactory.getAppSessionData(ServerGxSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ServerGxSessionImpl serverSession = new ServerGxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());
        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientGxSession.class + "," + ServerGxSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Gx Session.", e);
    }

    return appSession;
  }

  // Default implementation of methods so there are no exception!

  // Message Handlers --------------------------------------------------------
  @Override
  public void doCreditControlRequest(ServerGxSession session, GxCreditControlRequest request) throws InternalException {
  }

  @Override
  public void doCreditControlAnswer(ClientGxSession session, GxCreditControlRequest request, GxCreditControlAnswer answer) throws InternalException {
  }

  @Override
  public void doGxReAuthRequest(ClientGxSession session, GxReAuthRequest request) throws InternalException {
  }

  @Override
  public void doGxReAuthAnswer(ServerGxSession session, GxReAuthRequest request, GxReAuthAnswer answer) throws InternalException {
  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
  }

  // Message Factory Methods -------------------------------------------------
  @Override
  public GxCreditControlAnswer createCreditControlAnswer(Answer answer) {
    return new GxCreditControlAnswerImpl(answer);
  }

  @Override
  public GxCreditControlRequest createCreditControlRequest(Request req) {
    return new GxCreditControlRequestImpl(req);
  }

  @Override
  public GxReAuthAnswer createGxReAuthAnswer(Answer answer) {
    return new GxReAuthAnswerImpl(answer);
  }

  @Override
  public GxReAuthRequest createGxReAuthRequest(Request req) {
    return new GxReAuthRequestImpl(req);
  }

  // Context Methods ---------------------------------------------------------
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Gx SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Gx SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  // FIXME: add ctx methods proxy calls!
  @Override
  public void sessionSupervisionTimerExpired(ServerGxSession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerReStarted(ServerGxSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStarted(ServerGxSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStopped(ServerGxSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  @Override
  public void timeoutExpired(Request request) {
    // FIXME What should we do when there's a timeout?
  }

  @Override
  public void denyAccessOnDeliverFailure(ClientGxSession clientGxSessionImpl, Message request) {
    // TODO Complete this method.
  }

  @Override
  public void denyAccessOnFailureMessage(ClientGxSession clientGxSessionImpl) {
    // TODO Complete this method.
  }

  @Override
  public void denyAccessOnTxExpire(ClientGxSession clientGxSessionImpl) {
    // this.resourceAdaptor.sessionDestroyed(clientGxSessionImpl.getSessions().get(0).getSessionId(),
    // clientGxSessionImpl);
    clientGxSessionImpl.release();
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
  public void grantAccessOnDeliverFailure(ClientGxSession clientGxSessionImpl, Message request) {
    // TODO Auto-generated method stub
  }

  @Override
  public void grantAccessOnFailureMessage(ClientGxSession clientGxSessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void grantAccessOnTxExpire(ClientGxSession clientGxSessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void indicateServiceError(ClientGxSession clientGxSessionImpl) {
    // TODO Auto-generated method stub
  }

  @Override
  public void txTimerExpired(ClientGxSession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  @Override
  public long[] getApplicationIds() {
    // FIXME: What should we do here?
    return new long[] {16777238, 16777238};
  }

  @Override
  public long getDefaultValidityTime() {
    return this.defaultValidityTime;
  }
}
