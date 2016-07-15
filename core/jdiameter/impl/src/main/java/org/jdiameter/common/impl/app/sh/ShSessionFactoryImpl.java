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

package org.jdiameter.common.impl.app.sh;

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
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.ServerShSessionListener;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.sh.IShClientSessionData;
import org.jdiameter.client.impl.app.sh.ShClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.api.app.sh.IShSessionData;
import org.jdiameter.common.api.app.sh.IShSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.sh.IShServerSessionData;
import org.jdiameter.server.impl.app.sh.ShServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ShSessionFactoryImpl implements IShSessionFactory, StateChangeListener<AppSession>, ClientShSessionListener, ServerShSessionListener,
    IShMessageFactory {

  protected static final Logger logger = LoggerFactory.getLogger(ShSessionFactoryImpl.class);

  // Listeners provided by developer ----------------------------------------
  protected ClientShSessionListener clientShSessionListener;
  protected ServerShSessionListener serverShSessionListener;
  protected IShMessageFactory messageFactory;
  //not used.
  protected StateChangeListener<AppSession> stateChangeListener;

  // Our magic --------------------------------------------------------------
  protected ISessionFactory sessionFactory;
  protected ISessionDatasource sessionDataSource;
  protected IAppSessionDataFactory<IShSessionData> sessionDataFactory;
  protected long messageTimeout = 10000; // 10s default timeout
  protected static final long applicationId = 16777217;

  public ShSessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.sessionDataSource = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IShSessionData>) this.sessionDataSource.getDataFactory(IShSessionData.class);
    if (this.sessionDataFactory == null) {
      logger.debug("No factory for Sh Application data, using default/local.");
      this.sessionDataFactory = new ShLocalSessionDataFactory();
    }
  }

  /**
   * @return the clientShSessionListener
   */
  @Override
  public ClientShSessionListener getClientShSessionListener() {
    if (this.clientShSessionListener == null) {
      return this;
    }
    else {
      return clientShSessionListener;
    }
  }

  /**
   * @param clientShSessionListener
   *            the clientShSessionListener to set
   */
  @Override
  public void setClientShSessionListener(ClientShSessionListener clientShSessionListener) {
    this.clientShSessionListener = clientShSessionListener;
  }

  /**
   * @return the serverShSessionListener
   */
  @Override
  public ServerShSessionListener getServerShSessionListener() {
    if (this.serverShSessionListener == null) {
      return this;
    }
    else {
      return serverShSessionListener;
    }
  }

  /**
   * @param serverShSessionListener
   *            the serverShSessionListener to set
   */
  @Override
  public void setServerShSessionListener(ServerShSessionListener serverShSessionListener) {
    this.serverShSessionListener = serverShSessionListener;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IShMessageFactory getMessageFactory() {
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
  @Override
  public void setMessageFactory(IShMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the stateChangeListener
   */
  @Override
  public StateChangeListener<AppSession> getStateChangeListener() {
    return stateChangeListener;
  }

  /**
   * @param stateChangeListener
   *            the stateChangeListener to set
   */
  @Override
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
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    try {
      // FIXME: add proper handling for SessionId
      if (aClass == ClientShSession.class) {
        ShClientSessionImpl clientSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }

        IShClientSessionData sessionData = (IShClientSessionData) this.sessionDataFactory.getAppSessionData(ClientShSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        clientSession = new ShClientSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientShSessionListener());
        sessionDataSource.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        return clientSession;
      }
      else if (aClass == ServerShSession.class) {
        ShServerSessionImpl serverSession = null;

        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IShServerSessionData sessionData = (IShServerSessionData) this.sessionDataFactory.getAppSessionData(ServerShSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        serverSession = new ShServerSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, getServerShSessionListener());
        sessionDataSource.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        return serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: [" + aClass + "]. Supported[" + ClientShSession.class + "]");
      }
    }
    catch (IllegalArgumentException iae) {
      throw iae;
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Sh Session.", e);
    }

    return null;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("Session-Id must not be null");
    }
    if (!this.sessionDataSource.exists(sessionId)) {
      return null;
    }

    AppSession appSession = null;
    try {
      if (aClass == ServerShSession.class) {
        IShServerSessionData sessionData = (IShServerSessionData) this.sessionDataFactory.getAppSessionData(ServerShSession.class, sessionId);
        appSession = new ShServerSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, getServerShSessionListener());
        appSession.getSessions().get(0).setRequestListener((NetworkReqListener) appSession);
      }
      else if (aClass == ClientShSession.class) {
        IShClientSessionData sessionData = (IShClientSessionData) this.sessionDataFactory.getAppSessionData(ClientShSession.class, sessionId);
        appSession = new ShClientSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientShSessionListener());
        appSession.getSessions().get(0).setRequestListener((NetworkReqListener) appSession);
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerShSession.class + "," + ClientShSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Sh Session.", e);
    }

    return appSession;
  }

  // Methods to handle default values for user listeners --------------------
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Sh SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Sh SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
  }

  // Message Handlers -------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.sh.ClientShSessionListener#doOtherEvent(org.jdiameter.api.app.AppSession,
   *   org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.sh.ClientShSessionListener#doProfileUpdateAnswerEvent(org.jdiameter.api.sh.ClientShSession,
   *   org.jdiameter.api.sh.events.ProfileUpdateRequest, org.jdiameter.api.sh.events.ProfileUpdateAnswer)
   */
  @Override
  public void doProfileUpdateAnswerEvent(ClientShSession session, ProfileUpdateRequest request, ProfileUpdateAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.sh.ClientShSessionListener#doPushNotificationRequestEvent(
   *   org.jdiameter.api.sh.ClientShSession, org.jdiameter.api.sh.events.PushNotificationRequest)
   */
  @Override
  public void doPushNotificationRequestEvent(ClientShSession session, PushNotificationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.sh.ClientShSessionListener#doSubscribeNotificationsAnswerEvent(org.jdiameter.api.sh.ClientShSession,
   *   org.jdiameter.api.sh.events.SubscribeNotificationsRequest, org.jdiameter.api.sh.events.SubscribeNotificationsAnswer)
   */
  @Override
  public void doSubscribeNotificationsAnswerEvent(ClientShSession session, SubscribeNotificationsRequest request,
      SubscribeNotificationsAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.sh.ClientShSessionListener#doUserDataAnswerEvent(org
   * .jdiameter.api.sh.ClientShSession,
   * org.jdiameter.api.sh.events.UserDataRequest,
   * org.jdiameter.api.sh.events.UserDataAnswer)
   */
  @Override
  public void doUserDataAnswerEvent(ClientShSession session, UserDataRequest request, UserDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.sh.ServerShSessionListener#doProfileUpdateRequestEvent
   * (org.jdiameter.api.sh.ServerShSession,
   * org.jdiameter.api.sh.events.ProfileUpdateRequest)
   */
  @Override
  public void doProfileUpdateRequestEvent(ServerShSession session, ProfileUpdateRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.sh.ServerShSessionListener#doPushNotificationAnswerEvent
   * (org.jdiameter.api.sh.ServerShSession,
   * org.jdiameter.api.sh.events.PushNotificationRequest,
   * org.jdiameter.api.sh.events.PushNotificationAnswer)
   */
  @Override
  public void doPushNotificationAnswerEvent(ServerShSession session, PushNotificationRequest request, PushNotificationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.api.sh.ServerShSessionListener#
   * doSubscribeNotificationsRequestEvent
   * (org.jdiameter.api.sh.ServerShSession,
   * org.jdiameter.api.sh.events.SubscribeNotificationsRequest)
   */
  @Override
  public void doSubscribeNotificationsRequestEvent(ServerShSession session, SubscribeNotificationsRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.sh.ServerShSessionListener#doUserDataRequestEvent(org
   * .jdiameter.api.sh.ServerShSession,
   * org.jdiameter.api.sh.events.UserDataRequest)
   */
  @Override
  public void doUserDataRequestEvent(ServerShSession session, UserDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  // Message Factory ----------------------------------------------------------

  @Override
  public AppAnswerEvent createProfileUpdateAnswer(Answer answer) {
    return new ProfileUpdateAnswerImpl(answer);
  }

  @Override
  public AppRequestEvent createProfileUpdateRequest(Request request) {
    return new ProfileUpdateRequestImpl(request);
  }

  @Override
  public AppAnswerEvent createPushNotificationAnswer(Answer answer) {
    return new PushNotificationAnswerImpl(answer);
  }

  @Override
  public AppRequestEvent createPushNotificationRequest(Request request) {
    return new PushNotificationRequestImpl(request);
  }

  @Override
  public AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer) {
    return new SubscribeNotificationsAnswerImpl(answer);
  }

  @Override
  public AppRequestEvent createSubscribeNotificationsRequest(Request request) {
    return new SubscribeNotificationsRequestImpl(request);
  }

  @Override
  public AppAnswerEvent createUserDataAnswer(Answer answer) {
    return new UserDataAnswerImpl(answer);
  }

  @Override
  public AppRequestEvent createUserDataRequest(Request request) {
    return new UserDataRequestImpl(request);
  }

  @Override
  public long getApplicationId() {
    return applicationId;
  }

  @Override
  public long getMessageTimeout() {
    return messageTimeout;
  }
}
