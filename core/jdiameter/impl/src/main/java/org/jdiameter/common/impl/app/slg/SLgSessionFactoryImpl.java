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

package org.jdiameter.common.impl.app.slg;

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
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.slg.ISLgClientSessionData;
import org.jdiameter.client.impl.app.slg.SLgClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.api.app.slg.ISLgSessionData;
import org.jdiameter.common.api.app.slg.ISLgSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.slg.ISLgServerSessionData;
import org.jdiameter.server.impl.app.slg.SLgServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class SLgSessionFactoryImpl implements ISLgSessionFactory, StateChangeListener<AppSession>, ClientSLgSessionListener, ServerSLgSessionListener, ISLgMessageFactory {

  protected Logger logger = LoggerFactory.getLogger(SLgSessionFactoryImpl.class);

  // Listeners provided by developer ----------------------------------------
  protected ClientSLgSessionListener clientSLgSessionListener;
  protected ServerSLgSessionListener serverSLgSessionListener;
  protected ISLgMessageFactory messageFactory;
  //not used.
  protected StateChangeListener<AppSession> stateChangeListener;

  // Our magic --------------------------------------------------------------
  protected ISessionFactory sessionFactory;
  protected ISessionDatasource sessionDataSource;
  protected IAppSessionDataFactory<ISLgSessionData> sessionDataFactory;
  protected long messageTimeout = 10000; // 10s default timeout
  protected static final long applicationId = 16777255;

  public SLgSessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.sessionDataSource = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<ISLgSessionData>) this.sessionDataSource.getDataFactory(ISLgSessionData.class);
    if(this.sessionDataFactory == null) {
      logger.debug("No factory for SLg Application data, using default/local.");
      this.sessionDataFactory = new SLgLocalSessionDataFactory();
    }
  }

  /**
   * @return the clientSLgSessionListener
   */
  public ClientSLgSessionListener getClientSLgSessionListener() {
    if (this.clientSLgSessionListener == null) {
      return this;
    }
    else {
      return clientSLgSessionListener;
    }
  }

  /**
   * @param clientSLgSessionListener
   *            the clientSLgSessionListener to set
   */
  public void setClientSLgSessionListener(ClientSLgSessionListener clientSLgSessionListener) {
    this.clientSLgSessionListener = clientSLgSessionListener;
  }

  /**
   * @return the serverSLgSessionListener
   */
  public ServerSLgSessionListener getServerSLgSessionListener() {
    if (this.serverSLgSessionListener == null) {
      return this;
    }
    else {
      return serverSLgSessionListener;
    }
  }

  /**
   * @param serverSLgSessionListener
   *            the serverSLgSessionListener to set
   */
  public void setServerSLgSessionListener(ServerSLgSessionListener serverSLgSessionListener) {
    this.serverSLgSessionListener = serverSLgSessionListener;
  }

  /**
   * @return the messageFactory
   */
  public ISLgMessageFactory getMessageFactory() {
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
  public void setMessageFactory(ISLgMessageFactory messageFactory) {
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
      if (aClass == ClientSLgSession.class) {
        SLgClientSessionImpl clientSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }

        ISLgClientSessionData sessionData = (ISLgClientSessionData) this.sessionDataFactory.getAppSessionData(ClientSLgSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        clientSession = new SLgClientSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSLgSessionListener());
        sessionDataSource.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        return clientSession;
      }
      else if (aClass == ServerSLgSession.class) {
        SLgServerSessionImpl serverSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        ISLgServerSessionData sessionData = (ISLgServerSessionData) this.sessionDataFactory.getAppSessionData(ServerSLgSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        serverSession = new SLgServerSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, getServerSLgSessionListener());
        sessionDataSource.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        return serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: [" + aClass + "]. Supported[" + ClientSLgSession.class + "]");
      }
    }
    catch (IllegalArgumentException iae) {
      throw iae;
    }
    catch (Exception e) {
      logger.error("Failure to obtain new SLg Session.", e);
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
      if(aClass == ServerSLgSession.class) {
        ISLgServerSessionData sessionData = (ISLgServerSessionData) this.sessionDataFactory.getAppSessionData(ServerSLgSession.class, sessionId);
        appSession = new SLgServerSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, getServerSLgSessionListener());
        appSession.getSessions().get(0).setRequestListener((NetworkReqListener) appSession);
      }
      else if (aClass == ClientSLgSession.class) {
        ISLgClientSessionData sessionData = (ISLgClientSessionData) this.sessionDataFactory.getAppSessionData(ClientSLgSession.class, sessionId);
        appSession = new SLgClientSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSLgSessionListener());
        appSession.getSessions().get(0).setRequestListener((NetworkReqListener) appSession);
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerSLgSession.class + "," + ClientSLgSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new SLg Session.", e);
    }

    return appSession;
  }

  // Methods to handle default values for user listeners --------------------
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter SLg SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter SLg SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  // Message Handlers -------------------------------------------------------

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) 
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  @Override
  public void doLocationReportAnswerEvent(ServerSLgSession session, LocationReportRequest request, LocationReportAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	// TODO Auto-generated method stub	
  }
  
  @Override
  public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request) 
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	// TODO Auto-generated method stub
  }
  
  @Override
  public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	// TODO Auto-generated method stub
  }
  
  @Override
  public void doProvideLocationRequestEvent(ServerSLgSession session, ProvideLocationRequest request) 
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	// TODO Auto-generated method stub
  }
  
  // Message Factory ----------------------------------------------------------
  @Override
  public AppAnswerEvent createProvideLocationAnswer(Answer answer) {
    return new ProvideLocationAnswerImpl(answer);
  }
 
  @Override
  public AppRequestEvent createProvideLocationRequest(Request request) {
    return new ProvideLocationRequestImpl(request);
  }
  
  @Override
  public AppAnswerEvent createLocationReportAnswer(Answer answer) {
	return new LocationReportAnswerImpl(answer);
  }
  
  @Override
  public AppRequestEvent createLocationReportRequest(Request request) {
	return new LocationReportRequestImpl(request);
  }
  

  public long getApplicationId() {
    return applicationId;
  }

  public long getMessageTimeout() {
    return messageTimeout;
  }
}
