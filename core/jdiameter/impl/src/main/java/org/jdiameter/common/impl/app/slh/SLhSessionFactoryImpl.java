/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.impl.app.slh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ClientSLhSessionListener;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slh.ServerSLhSessionListener;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.slh.ISLhClientSessionData;
import org.jdiameter.client.impl.app.slh.SLhClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.slh.ISLhMessageFactory;
import org.jdiameter.common.api.app.slh.ISLhSessionData;
import org.jdiameter.common.api.app.slh.ISLhSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.slh.ISLhServerSessionData;
import org.jdiameter.server.impl.app.slh.SLhServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class SLhSessionFactoryImpl implements ISLhSessionFactory, StateChangeListener<AppSession>, ClientSLhSessionListener, ServerSLhSessionListener, ISLhMessageFactory {

  protected Logger logger = LoggerFactory.getLogger(SLhSessionFactoryImpl.class);

  // Listeners provided by developer ----------------------------------------
  protected ClientSLhSessionListener clientSLhSessionListener;
  protected ServerSLhSessionListener serverSLhSessionListener;
  protected ISLhMessageFactory messageFactory;
  //not used.
  protected StateChangeListener<AppSession> stateChangeListener;

  // Our magic --------------------------------------------------------------
  protected ISessionFactory sessionFactory;
  protected ISessionDatasource sessionDataSource;
  protected IAppSessionDataFactory<ISLhSessionData> sessionDataFactory;
  protected long messageTimeout = 10000; // 10s default timeout
  protected static final long applicationId = 16777291;

  public SLhSessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.sessionDataSource = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<ISLhSessionData>) this.sessionDataSource.getDataFactory(ISLhSessionData.class);
    if(this.sessionDataFactory == null) {
      logger.debug("No factory for SLh Application data, using default/local.");
      this.sessionDataFactory = new SLhLocalSessionDataFactory();
    }
  }

  /**
   * @return the clientSLhSessionListener
   */
  public ClientSLhSessionListener getClientSLhSessionListener() {
    if (this.clientSLhSessionListener == null) {
      return this;
    }
    else {
      return clientSLhSessionListener;
    }
  }

  /**
   * @param clientSLhSessionListener
   *            the clientSLhSessionListener to set
   */
  public void setClientSLhSessionListener(ClientSLhSessionListener clientSLhSessionListener) {
    this.clientSLhSessionListener = clientSLhSessionListener;
  }

  /**
   * @return the serverSLhSessionListener
   */
  public ServerSLhSessionListener getServerSLhSessionListener() {
    if (this.serverSLhSessionListener == null) {
      return this;
    }
    else {
      return serverSLhSessionListener;
    }
  }

  /**
   * @param serverSLhSessionListener
   *            the serverSLhSessionListener to set
   */
  public void setServerSLhSessionListener(ServerSLhSessionListener serverSLhSessionListener) {
    this.serverSLhSessionListener = serverSLhSessionListener;
  }

  /**
   * @return the messageFactory
   */
  public ISLhMessageFactory getMessageFactory() {
    if (this.messageFactory == null) {
      return this;
    }
    else {
      return messageFactory;
    }
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  public void setMessageFactory(ISLhMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the stateChangeListener
   */
  public StateChangeListener<AppSession> getStateChangeListener() {
    return stateChangeListener;
  }

  /**
   * @param stateChangeListener
   *            the stateChangeListener to set
   */
  public void setStateChangeListener(StateChangeListener<AppSession> stateChangeListener) {
    this.stateChangeListener = stateChangeListener;
  }

  // IAppSession ------------------------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.app.IAppSessionFactory#getNewSession(java.lang.String, java.lang.Class, 
   *   org.jdiameter.api.ApplicationId, java.lang.Object[])
   */
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      // FIXME: add proper handling for SessionId
      if (aClass == ClientSLhSession.class) {
        SLhClientSessionImpl clientSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }

        ISLhClientSessionData sessionData = (ISLhClientSessionData) this.sessionDataFactory.getAppSessionData(ClientSLhSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        clientSession = new SLhClientSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSLhSessionListener());
        sessionDataSource.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        return clientSession;
      }
      else if (aClass == ServerSLhSession.class) {
        SLhServerSessionImpl serverSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        ISLhServerSessionData sessionData = (ISLhServerSessionData) this.sessionDataFactory.getAppSessionData(ServerSLhSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        serverSession = new SLhServerSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, getServerSLhSessionListener());
        sessionDataSource.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        return serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: [" + aClass + "]. Supported[" + ClientSLhSession.class + "]");
      }
    }
    catch (IllegalArgumentException iae) {
      throw iae;
    }
    catch (Exception e) {
      logger.error("Failure to obtain new SLh Session.", e);
    }

    return null;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("Session-Id must not be null");
    }
    if(!this.sessionDataSource.exists(sessionId)) {
      return null;
    }

    AppSession appSession = null;
    try {
      if(aClass == ServerSLhSession.class) {
        ISLhServerSessionData sessionData = (ISLhServerSessionData) this.sessionDataFactory.getAppSessionData(ServerSLhSession.class, sessionId);
        appSession = new SLhServerSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, getServerSLhSessionListener());
        appSession.getSessions().get(0).setRequestListener((NetworkReqListener) appSession);
      }
      else if (aClass == ClientSLhSession.class) {
        ISLhClientSessionData sessionData = (ISLhClientSessionData) this.sessionDataFactory.getAppSessionData(ClientSLhSession.class, sessionId);
        appSession = new SLhClientSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSLhSessionListener());
        appSession.getSessions().get(0).setRequestListener((NetworkReqListener) appSession);
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerSLhSession.class + "," + ClientSLhSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new SLh Session.", e);
    }

    return appSession;
  }

  // Methods to handle default values for user listeners --------------------
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter SLh SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter SLh SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  // Message Handlers -------------------------------------------------------

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) 
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  public void doLCSRoutingInfoAnswerEvent(ClientSLhSession session, LCSRoutingInfoRequest request, LCSRoutingInfoAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  public void doLCSRoutingInfoRequestEvent(ServerSLhSession session, LCSRoutingInfoRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  // Message Factory ----------------------------------------------------------
  public AppAnswerEvent createLCSRoutingInfoAnswer(Answer answer) {
    return new LCSRoutingInfoAnswerImpl(answer);
  }

  public AppRequestEvent createLCSRoutingInfoRequest(Request request) {
    return new LCSRoutingInfoRequestImpl(request);
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getMessageTimeout() {
    return messageTimeout;
  }
}
