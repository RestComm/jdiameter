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

package org.jdiameter.common.impl.app.acc;

import java.util.concurrent.ScheduledFuture;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.acc.ClientAccSessionImpl;
import org.jdiameter.client.impl.app.acc.IClientAccSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.acc.IAccMessageFactory;
import org.jdiameter.common.api.app.acc.IAccSessionData;
import org.jdiameter.common.api.app.acc.IAccSessionFactory;
import org.jdiameter.common.api.app.acc.IClientAccActionContext;
import org.jdiameter.common.api.app.acc.IServerAccActionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.acc.IServerAccSessionData;
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Account Session Factory implementation
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AccSessionFactoryImpl implements IAccSessionFactory, IAccMessageFactory, ServerAccSessionListener, ClientAccSessionListener,
    IClientAccActionContext, IServerAccActionContext, StateChangeListener<AppSession> {

  protected static final Logger logger = LoggerFactory.getLogger(AccSessionFactoryImpl.class);

  protected ServerAccSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected ClientAccSessionListener clientSessionListener;
  protected IClientAccActionContext clientContextListener;
  protected IServerAccActionContext serverContextListener;

  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected ApplicationId applicationId;
  protected IAccMessageFactory messageFactory = this;
  protected IAppSessionDataFactory<IAccSessionData> sessionDataFactory;

  protected AccSessionFactoryImpl() {
  }

  public AccSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    setSessionFactory((ISessionFactory) sessionFactory);
  }

  // ACC Factory Methods ------------------------------------------------------

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerAccSessionListener getServerSessionListener() {
    if (this.serverSessionListener != null) {
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
  public void setServerSessionListener(ServerAccSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
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

  /**
   * @return the clientSessionListener
   */
  @Override
  public ClientAccSessionListener getClientSessionListener() {
    if (this.clientSessionListener != null) {
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
  public void setClientSessionListener(ClientAccSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the clientContextListener
   */
  @Override
  public IClientAccActionContext getClientContextListener() {
    if (this.clientContextListener != null) {
      return clientContextListener;
    }
    else {
      return this;
    }
  }

  /**
   * @param clientContextListener
   *          the clientContextListener to set
   */
  @Override
  public void setClientContextListener(IClientAccActionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  /**
   * @return the serverContextListener
   */
  @Override
  public IServerAccActionContext getServerContextListener() {
    if (this.serverContextListener != null) {
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
  public void setServerContextListener(IServerAccActionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  @Override
  public void setMessageFactory(IAccMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  @Override
  public IAccMessageFactory getMessageFactory() {
    return this.messageFactory;

  }
  /**
   * @return the sessionFactory
   */
  public ISessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory the sessionFactory to set
   */
  public void setSessionFactory(ISessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    if (this.iss == null) {
      this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
      this.sessionDataFactory = (IAppSessionDataFactory<IAccSessionData>) this.iss.getDataFactory(IAccSessionData.class);
      if (this.sessionDataFactory == null) {
        logger.debug("No factory for Accounting Application data, using default/local.");
        this.sessionDataFactory = new AccLocalSessionDataFactory();
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IAccSessionFactory#getApplicationId()
   */
  @Override
  public ApplicationId getApplicationId() {
    return this.applicationId;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IAccSessionFactory#setApplicationId( org.jdiameter.api.ApplicationId)
   */
  @Override
  public void setApplicationId(ApplicationId id) {
    this.applicationId = id;
  }

  // App Session Factory ------------------------------------------------------

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
      if (aClass == ClientAccSession.class) {
        IClientAccSessionData data = (IClientAccSessionData) this.sessionDataFactory.getAppSessionData(ClientAccSession.class, sessionId);
        ClientAccSessionImpl clientSession =
            new ClientAccSessionImpl(data, sessionFactory, getClientSessionListener(), getClientContextListener(), getStateListener());

        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerAccSession.class) {
        ServerAccSessionImpl serverSession = null;
        IServerAccSessionData data = (IServerAccSessionData) this.sessionDataFactory.getAppSessionData(ServerAccSession.class, sessionId);

        //here we use shorter con, since some data is already present.
        serverSession = new ServerAccSessionImpl(data, sessionFactory,  getServerSessionListener(), getServerContextListener(), getStateListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientAccSession.class + "," + ServerAccSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Accounting Session.", e);
    }

    return appSession;
  }

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      if (aClass == ServerAccSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        boolean stateless = true;
        if (args != null && args.length > 0) {
          for (Object o:args) {
            if (o instanceof Boolean) {
              stateless = (Boolean) o;
            }
          }
        }

        IServerAccSessionData data = (IServerAccSessionData) this.sessionDataFactory.getAppSessionData(ServerAccSession.class, sessionId);
        data.setApplicationId(applicationId);
        ServerAccSessionImpl session = new ServerAccSessionImpl(data, sessionFactory, getServerSessionListener(), getServerContextListener(),
            getStateListener(), stateless);
        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else if (aClass == ClientAccSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientAccSessionData data = (IClientAccSessionData) this.sessionDataFactory.getAppSessionData(ClientAccSession.class, sessionId);
        data.setApplicationId(applicationId);
        ClientAccSessionImpl session = new ClientAccSessionImpl(data, sessionFactory, getClientSessionListener(), getClientContextListener(),
            getStateListener());

        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Accounting Session.", e);
    }

    return null;
  }

  // State Change Listener ----------------------------------------------------

  @Override
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter ACC SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter ACC SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  // ///////////////////
  // Event listeners //
  // ///////////////////

  @Override
  public void doAccRequestEvent(ServerAccSession appSession, AccountRequest acr)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AccountingSessionFactory :: doAccRequestEvent :: appSession[" + appSession + "], Request[" + acr + "]");
  }

  @Override
  public void doAccAnswerEvent(ClientAccSession appSession, AccountRequest acr, AccountAnswer aca)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("doAccAnswerEvent :: appSession[" + appSession + "], Request[" + acr + "], Answer[" + aca + "]");
  }

  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AccountingSessionFactory :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");
  }

  // Client context -----------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext#disconnectUserOrDev (org.jdiameter.api.Request)
   */
  @Override
  public void disconnectUserOrDev(ClientAccSession appSession, Request sessionTermRequest) throws InternalException {
    logger.info("disconnectUserOrDev :: appSession[" + appSession + "], Request[" + sessionTermRequest + "]");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext#failedSendRecord (org.jdiameter.api.Request)
   */
  @Override
  public boolean failedSendRecord(ClientAccSession appSession, Request accRequest) throws InternalException {
    logger.info("failedSendRecord :: appSession[" + appSession + "], Request[" + accRequest + "]");
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext# interimIntervalElapses(org.jdiameter.api.Request)
   */
  @Override
  public void interimIntervalElapses(ClientAccSession appSession, Request interimRequest) throws InternalException {
    logger.info("interimIntervalElapses :: appSession[" + appSession + "], Request[" + interimRequest + "]");
  }

  // Server context -----------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.common.api.app.acc.IServerAccActionContext#sessionTimeoutElapses(org.jdiameter.api.acc.ServerAccSession)
   */
  @Override
  public void sessionTimeoutElapses(ServerAccSession appSession) throws InternalException {
    logger.info("sessionTimeoutElapses :: appSession[" + appSession + "]");

  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IServerAccActionContext#
   *    sessionTimerStarted(org.jdiameter.api.acc.ServerAccSession, java.util.concurrent.ScheduledFuture)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void sessionTimerStarted(ServerAccSession appSession, ScheduledFuture timer) throws InternalException {
    logger.info("sessionTimerStarted :: appSession[" + appSession + "]");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IServerAccActionContext#
   *    sessionTimerCanceled(org.jdiameter.api.acc.ServerAccSession, java.util.concurrent.ScheduledFuture)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void sessionTimerCanceled(ServerAccSession appSession, ScheduledFuture timer) throws InternalException {
    logger.info("sessionTimerCanceled :: appSession[" + appSession + "]");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IAccMessageFactory#getAccMessageCommandCode()
   */
  @Override
  public int getAccMessageCommandCode() {
    // TODO Auto-generated method stub
    return AccountRequest.code;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IAccMessageFactory#createAccRequest(org.jdiameter.api.Request)
   */
  @Override
  public AccountRequest createAccRequest(Request request) {
    return new AccountRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IAccMessageFactory#createAccAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public AccountAnswer createAccAnswer(Answer answer) {
    return new AccountAnswerImpl(answer);
  }

}
