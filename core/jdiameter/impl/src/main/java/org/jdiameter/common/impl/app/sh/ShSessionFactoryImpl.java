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
package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
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
import org.jdiameter.client.impl.app.sh.ShClientSessionImpl;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.api.app.sh.IShSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdiameter.server.impl.app.sh.ShServerSessionImpl;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ShSessionFactoryImpl implements IShSessionFactory, StateChangeListener<AppSession>, ClientShSessionListener, ServerShSessionListener, IShMessageFactory {

  protected Logger logger = LoggerFactory.getLogger(ShSessionFactoryImpl.class);

  // Listeners provided by developer ----------------------------------------
  protected ClientShSessionListener clientShSessionListener;
  protected ServerShSessionListener serverShSessionListener;
  protected IShMessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateChangeListener;

  // Our magic --------------------------------------------------------------
  protected ISessionFactory sessionFactory;
  protected ISessionDatasource sessionDataSource;

  protected long messageTimeout = 10000; // 10s default timeout
  protected static final long applicationId = 16777217;

  public ShSessionFactoryImpl(SessionFactory sessionFactory) {
    super();
    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.sessionDataSource = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  /**
   * @return the clientShSessionListener
   */
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
  public void setClientShSessionListener(ClientShSessionListener clientShSessionListener) {
    this.clientShSessionListener = clientShSessionListener;
  }

  /**
   * @return the serverShSessionListener
   */
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
  public void setServerShSessionListener(ServerShSessionListener serverShSessionListener) {
    this.serverShSessionListener = serverShSessionListener;
  }

  /**
   * @return the messageFactory
   */
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
  public void setMessageFactory(IShMessageFactory messageFactory) {
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
      if (aClass == ClientShSession.class) {
        ShClientSessionImpl clientSession = null;

        if (args != null && args.length > 0
            && (args[0] instanceof Request || (args[0] instanceof Message && ((Message) args[0]).isRequest()))) {
          Message request = (Message) args[0];
          clientSession = new ShClientSessionImpl(request.getSessionId(), this.getMessageFactory(), sessionFactory, this
              .getClientShSessionListener());
        }
        else {
          clientSession = new ShClientSessionImpl(null, this.getMessageFactory(), sessionFactory, this.getClientShSessionListener());
        }

        sessionDataSource.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        return clientSession;
      }
      else if (aClass == ServerShSession.class) {
        ShServerSessionImpl serverSession = null;

        if (args != null && args.length > 0
            && (args[0] instanceof Request || (args[0] instanceof Message && ((Message) args[0]).isRequest()))) {
          Message request = (Message) args[0];
          serverSession = new ShServerSessionImpl(request.getSessionId(), this.getMessageFactory(), sessionFactory, this
              .getServerShSessionListener());
        }
        else {
          serverSession = new ShServerSessionImpl(null, this.getMessageFactory(), sessionFactory, getServerShSessionListener());
        }

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

  // Methods to handle default values for user listeners --------------------

  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Sh SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
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
  public void doUserDataRequestEvent(ServerShSession session, UserDataRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub
  }

  // Message Factory ----------------------------------------------------------

  public AppAnswerEvent createProfileUpdateAnswer(Answer answer) {
    return new ProfileUpdateAnswerImpl(answer);
  }

  public AppRequestEvent createProfileUpdateRequest(Request request) {
    return new ProfileUpdateRequestImpl(request);
  }

  public AppAnswerEvent createPushNotificationAnswer(Answer answer) {
    return new PushNotificationAnswerImpl(answer);
  }

  public AppRequestEvent createPushNotificationRequest(Request request) {
    return new PushNotificationRequestImpl(request);
  }

  public AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer) {
    return new SubscribeNotificationsAnswerImpl(answer);
  }

  public AppRequestEvent createSubscribeNotificationsRequest(Request request) {
    return new SubscribeNotificationsRequestImpl(request);
  }

  public AppAnswerEvent createUserDataAnswer(Answer answer) {
    return new UserDataAnswerImpl(answer);
  }

  public AppRequestEvent createUserDataRequest(Request request) {
    return new UserDataRequestImpl(request);
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getMessageTimeout() {
    return messageTimeout;
  }
}
