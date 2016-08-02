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

package org.jdiameter.common.impl.app.rf;

import java.util.concurrent.ScheduledFuture;

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
import org.jdiameter.api.rf.ClientRfSession;
import org.jdiameter.api.rf.ClientRfSessionListener;
import org.jdiameter.api.rf.ServerRfSession;
import org.jdiameter.api.rf.ServerRfSessionListener;
import org.jdiameter.api.rf.events.RfAccountingAnswer;
import org.jdiameter.api.rf.events.RfAccountingRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.rf.ClientRfSessionImpl;
import org.jdiameter.client.impl.app.rf.IClientRfSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.rf.IClientRfActionContext;
import org.jdiameter.common.api.app.rf.IRfSessionData;
import org.jdiameter.common.api.app.rf.IRfSessionFactory;
import org.jdiameter.common.api.app.rf.IServerRfActionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.rf.IServerRfSessionData;
import org.jdiameter.server.impl.app.rf.ServerRfSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Rf Session Factory implementation
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfSessionFactoryImpl implements IRfSessionFactory, ServerRfSessionListener, ClientRfSessionListener, IClientRfActionContext,
    IServerRfActionContext, StateChangeListener<AppSession> {

  protected static final Logger logger = LoggerFactory.getLogger(RfSessionFactoryImpl.class);

  protected ServerRfSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected ClientRfSessionListener clientSessionListener;
  protected IClientRfActionContext clientContextListener;
  protected IServerRfActionContext serverContextListener;

  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected long messageTimeout = 5000;
  protected ApplicationId applicationId;
  protected IAppSessionDataFactory<IRfSessionData> sessionDataFactory;

  protected RfSessionFactoryImpl() {
  }

  public RfSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IRfSessionData>) this.iss.getDataFactory(IRfSessionData.class);
  }

  // ACC Factory Methods
  // ------------------------------------------------------

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerRfSessionListener getServerSessionListener() {
    if (this.serverSessionListener != null) {
      return serverSessionListener;
    } else {
      return this;
    }
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerRfSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the stateListener
   */
  @Override
  public StateChangeListener<AppSession> getStateListener() {
    if (this.stateListener != null) {
      return stateListener;
    } else {
      return this;
    }
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
  public ClientRfSessionListener getClientSessionListener() {
    if (this.clientSessionListener != null) {
      return clientSessionListener;
    } else {
      return this;
    }
  }

  /**
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientRfSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the clientContextListener
   */
  @Override
  public IClientRfActionContext getClientContextListener() {
    if (this.clientContextListener != null) {
      return clientContextListener;
    } else {
      return this;
    }
  }

  /**
   * @param clientContextListener
   *            the clientContextListener to set
   */
  @Override
  public void setClientContextListener(IClientRfActionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  /**
   * @return the serverContextListener
   */
  @Override
  public IServerRfActionContext getServerContextListener() {
    if (this.serverContextListener != null) {
      return serverContextListener;
    } else {
      return this;
    }
  }

  /**
   * @param serverContextListener
   *            the serverContextListener to set
   */
  @Override
  public void setServerContextListener(IServerRfActionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
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
   * @return the sessionFactory
   */
  public ISessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory
   *            the sessionFactory to set
   */
  public void setSessionFactory(ISessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    if (this.iss == null) {
      this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.acc.IAccSessionFactory#getApplicationId()
   */
  @Override
  public ApplicationId getApplicationId() {
    return this.applicationId;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.acc.IAccSessionFactory#setApplicationId(
   * org.jdiameter.api.ApplicationId)
   */
  @Override
  public void setApplicationId(ApplicationId id) {
    this.applicationId = id;
  }

  // App Session Factory
  // ------------------------------------------------------
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
      if (aClass == ServerRfSession.class) {
        IServerRfSessionData sessionData = (IServerRfSessionData) this.sessionDataFactory.getAppSessionData(ServerRfSession.class, sessionId);
        // FIXME: determine how to get boolean flag!
        ServerRfSessionImpl session = new ServerRfSessionImpl(sessionData, sessionFactory, getServerSessionListener(), getServerContextListener(),
            getStateListener(), messageTimeout, true);

        session.getSessions().get(0).setRequestListener(session);
        appSession = session;
      }
      else if (aClass == ClientRfSession.class) {
        IClientRfSessionData sessionData = (IClientRfSessionData) this.sessionDataFactory.getAppSessionData(ClientRfSession.class, sessionId);
        ClientRfSessionImpl session = new ClientRfSessionImpl(sessionData, sessionFactory, getClientSessionListener(), getClientContextListener(),
            getStateListener(), this.getApplicationId());

        session.getSessions().get(0).setRequestListener(session);
        appSession = session;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRfSession.class + "," + ServerRfSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Rf Session.", e);
    }

    return appSession;
  }
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      if (aClass == ServerRfSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerRfSessionData sessionData = (IServerRfSessionData) this.sessionDataFactory.getAppSessionData(ServerRfSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        // FIXME: determine how to get boolean flag!
        ServerRfSessionImpl session = new ServerRfSessionImpl(sessionData, sessionFactory, getServerSessionListener(), getServerContextListener(),
            getStateListener(), messageTimeout, true);

        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else if (aClass == ClientRfSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientRfSessionData sessionData = (IClientRfSessionData) this.sessionDataFactory.getAppSessionData(ClientRfSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ClientRfSessionImpl session = new ClientRfSessionImpl(sessionData, sessionFactory, getClientSessionListener(), getClientContextListener(),
            getStateListener(), this.getApplicationId());

        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRfSession.class + "," + ServerRfSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Rf Session.", e);
    }

    return null;
  }

  // State Change Listener
  // ----------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter ACC SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object,
   * java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Rf SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  // ///////////////////
  // Event listeners //
  // ///////////////////

  @Override
  public void doRfAccountingRequestEvent(ServerRfSession appSession, RfAccountingRequest acr)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base RfSessionFactory :: doAccRequestEvent :: appSession[" + appSession + "], Request[" + acr + "]");
  }

  @Override
  public void doRfAccountingAnswerEvent(ClientRfSession appSession, RfAccountingRequest acr, RfAccountingAnswer aca)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("doRfAnswerEvent :: appSession[" + appSession + "], Request[" + acr + "], Answer[" + aca + "]");
  }

  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base RfountingSessionFactory :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");
  }

  // Client context
  // -----------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.Rf.IClientRfActionContext#disconnectUserOrDev
   * (org.jdiameter.api.Request)
   */
  @Override
  public void disconnectUserOrDev(ClientRfSession appSession, Request sessionTermRequest) throws InternalException {
    logger.info("disconnectUserOrDev :: appSession[" + appSession + "], Request[" + sessionTermRequest + "]");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.Rf.IClientRfActionContext#failedSendRecord
   * (org.jdiameter.api.Request)
   */
  @Override
  public boolean failedSendRecord(ClientRfSession appSession, Request rfRequest) throws InternalException {
    logger.info("failedSendRecord :: appSession[" + appSession + "], Request[" + rfRequest + "]");
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext#
   * interimIntervalElapses(org.jdiameter.api.Request)
   */
  @Override
  public void interimIntervalElapses(ClientRfSession appSession, Request interimRequest) throws InternalException {
    logger.info("interimIntervalElapses :: appSession[" + appSession + "], Request[" + interimRequest + "]");
  }

  // Server context
  // -----------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.common.api.app.Rf.IServerRfActionContext#
   * sessionTimeoutElapses(org.jdiameter.api.Rf.ServerRfSession)
   */
  @Override
  public void sessionTimeoutElapses(ServerRfSession appSession) throws InternalException {
    logger.info("sessionTimeoutElapses :: appSession[" + appSession + "]");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.Rf.IServerRfActionContext#sessionTimerStarted
   * (org.jdiameter.api.Rf.ServerRfSession,
   * java.util.concurrent.ScheduledFuture)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void sessionTimerStarted(ServerRfSession appSession, ScheduledFuture timer) throws InternalException {
    logger.info("sessionTimerStarted :: appSession[" + appSession + "]");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.Rf.IServerRfActionContext#srssionTimerCanceled
   * (org.jdiameter.api.Rf.ServerRfSession,
   * java.util.concurrent.ScheduledFuture)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void sessionTimerCanceled(ServerRfSession appSession, ScheduledFuture timer) throws InternalException {
    logger.info("sessionTimerCanceled :: appSession[" + appSession + "]");
  }

}
