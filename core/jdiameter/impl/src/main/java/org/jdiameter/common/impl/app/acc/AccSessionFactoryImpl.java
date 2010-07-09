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
package org.jdiameter.common.impl.app.acc;

import java.util.concurrent.ScheduledFuture;

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
import org.jdiameter.common.api.app.acc.IAccSessionFactory;
import org.jdiameter.common.api.app.acc.IClientAccActionContext;
import org.jdiameter.common.api.app.acc.IServerAccActionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Account Session Factory implementation
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AccSessionFactoryImpl implements IAccSessionFactory, ServerAccSessionListener, ClientAccSessionListener, IClientAccActionContext, IServerAccActionContext, StateChangeListener<AppSession> {

  protected Logger logger = LoggerFactory.getLogger(AccSessionFactoryImpl.class);

  protected ServerAccSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected ClientAccSessionListener clientSessionListener;
  protected IClientAccActionContext clientContextListener;
  protected IServerAccActionContext serverContextListener;

  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected long messageTimeout = 5000;
  protected ApplicationId applicationId;

  protected AccSessionFactoryImpl() {
  }
  
  public AccSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  // ACC Factory Methods ------------------------------------------------------

  /**
   * @return the serverSessionListener
   */
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
  public void setServerSessionListener(ServerAccSessionListener serverSessionListener) {
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
   *          the stateListener to set
   */
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  /**
   * @return the clientSessionListener
   */
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
  public void setClientSessionListener(ClientAccSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the clientContextListener
   */
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
  public void setClientContextListener(IClientAccActionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  /**
   * @return the serverContextListener
   */
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
  public void setServerContextListener(IServerAccActionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the messageTimeout
   */
  public long getMessageTimeout() {
    return messageTimeout;
  }

  /**
   * @param messageTimeout
   *          the messageTimeout to set
   */
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
   * @param sessionFactory the sessionFactory to set
   */
  public void setSessionFactory(ISessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    if(this.iss == null) {
      this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IAccSessionFactory#getApplicationId()
   */
  public ApplicationId getApplicationId() {
    return this.applicationId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IAccSessionFactory#setApplicationId( org.jdiameter.api.ApplicationId)
   */
  public void setApplicationId(ApplicationId id) {
    this.applicationId = id;
  }

  // App Session Factory ------------------------------------------------------

  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      if (aClass == ServerAccSession.class) {
        Request request = null;
        if (args != null && args.length > 0) {
          request = (Request) args[0];
        }
        // FIXME: determine how to get boolean flag!
        ServerAccSessionImpl session = new ServerAccSessionImpl(sessionId, sessionFactory, request, getServerSessionListener(), getServerContextListener(), getStateListener(), messageTimeout, true);
        iss.addSession(session);
        session.getSessions().get(0).setRequestListener(session);
        return session;
      }
      else if (aClass == ClientAccSession.class) {
        ClientAccSessionImpl session = new ClientAccSessionImpl(sessionId, sessionFactory, getClientSessionListener(), getClientContextListener(), getStateListener(), this.getApplicationId());

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

  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter ACC SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter ACC SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  // ///////////////////
  // Event listeners //
  // ///////////////////

  public void doAccRequestEvent(ServerAccSession appSession, AccountRequest acr) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AccountingSessionFactory :: doAccRequestEvent :: appSession[" + appSession + "], Request[" + acr + "]");
  }

  public void doAccAnswerEvent(ClientAccSession appSession, AccountRequest acr, AccountAnswer aca) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("doAccAnswerEvent :: appSession[" + appSession + "], Request[" + acr + "], Answer[" + aca + "]");
  }

  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Base AccountingSessionFactory :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");
  }

  // Client context -----------------------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext#disconnectUserOrDev (org.jdiameter.api.Request)
   */
  public void disconnectUserOrDev(ClientAccSession appSession, Request sessionTermRequest) throws InternalException {
    logger.info("disconnectUserOrDev :: appSession[" + appSession + "], Request[" + sessionTermRequest + "]");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext#failedSendRecord (org.jdiameter.api.Request)
   */
  public boolean failedSendRecord(ClientAccSession appSession, Request accRequest) throws InternalException {
    logger.info("failedSendRecord :: appSession[" + appSession + "], Request[" + accRequest + "]");
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IClientAccActionContext# interimIntervalElapses(org.jdiameter.api.Request)
   */
  public void interimIntervalElapses(ClientAccSession appSession, Request interimRequest) throws InternalException {
    logger.info("interimIntervalElapses :: appSession[" + appSession + "], Request[" + interimRequest + "]");
  }

  // Server context -----------------------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @seeorg.jdiameter.common.api.app.acc.IServerAccActionContext#sessionTimeoutElapses(org.jdiameter.api.acc.ServerAccSession)
   */
  public void sessionTimeoutElapses(ServerAccSession appSession) throws InternalException {
    logger.info("sessionTimeoutElapses :: appSession[" + appSession + "]");

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IServerAccActionContext#sessionTimerStarted(org.jdiameter.api.acc.ServerAccSession, java.util.concurrent.ScheduledFuture)
   */
  @SuppressWarnings("unchecked")
  public void sessionTimerStarted(ServerAccSession appSession, ScheduledFuture timer) throws InternalException {
    logger.info("sessionTimerStarted :: appSession[" + appSession + "]");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.acc.IServerAccActionContext#srssionTimerCanceled(org.jdiameter.api.acc.ServerAccSession, java.util.concurrent.ScheduledFuture)
   */
  @SuppressWarnings("unchecked")
  public void sessionTimerCanceled(ServerAccSession appSession, ScheduledFuture timer) throws InternalException {
    logger.info("sessionTimerCanceled :: appSession[" + appSession + "]");
  }

}
