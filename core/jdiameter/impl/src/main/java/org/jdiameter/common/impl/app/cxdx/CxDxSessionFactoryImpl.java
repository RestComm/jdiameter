 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
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
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.common.impl.app.cxdx;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSessionListener;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.cxdx.CxDxClientSessionImpl;
import org.jdiameter.client.impl.app.cxdx.IClientCxDxSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.api.app.cxdx.ICxDxSessionData;
import org.jdiameter.common.api.app.cxdx.ICxDxSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl;
import org.jdiameter.server.impl.app.cxdx.IServerCxDxSessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CxDxSessionFactoryImpl implements ICxDxSessionFactory, ClientCxDxSessionListener, ServerCxDxSessionListener,
    ICxDxMessageFactory, StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(CxDxSessionFactoryImpl.class);

  protected ISessionFactory sessionFactory;

  protected ClientCxDxSessionListener clientSessionListener;

  protected ServerCxDxSessionListener serverSessionListener;

  protected ICxDxMessageFactory messageFactory;

  protected StateChangeListener<AppSession> stateListener;

  protected ISessionDatasource iss;

  protected IAppSessionDataFactory<ICxDxSessionData> sessionDataFactory;

  public CxDxSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<ICxDxSessionData>) this.iss.getDataFactory(ICxDxSessionData.class);
  }

  /**
   * @return the clientSessionListener
   */
  @Override
  public ClientCxDxSessionListener getClientSessionListener() {
    if (clientSessionListener != null) {
      return clientSessionListener;
    }
    else {
      return this;
    }
  }

  /**
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientCxDxSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerCxDxSessionListener getServerSessionListener() {
    if (serverSessionListener != null) {
      return serverSessionListener;
    }
    else {
      return this;
    }
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerCxDxSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public ICxDxMessageFactory getMessageFactory() {
    if (messageFactory != null) {
      return messageFactory;
    }
    else {
      return this;
    }
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  @Override
  public void setMessageFactory(ICxDxMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the stateListener
   */
  @Override
  public StateChangeListener<AppSession> getStateListener() {
    if (stateListener != null) {
      return stateListener;
    }
    else {
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
      if (aClass == ClientCxDxSession.class) {
        IClientCxDxSessionData sessionData = (IClientCxDxSessionData) this.sessionDataFactory.getAppSessionData(ClientCxDxSession.class, sessionId);
        CxDxClientSessionImpl clientSession = new CxDxClientSessionImpl(sessionData, this.getMessageFactory(), this.sessionFactory,
            this.getClientSessionListener());

        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerCxDxSession.class) {
        IServerCxDxSessionData sessionData = (IServerCxDxSessionData) this.sessionDataFactory.getAppSessionData(ServerCxDxSession.class, sessionId);
        CxDxServerSessionImpl serverSession = new CxDxServerSessionImpl(sessionData, getMessageFactory(), sessionFactory,
            this.getServerSessionListener());

        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientAccSession.class + "," + ServerAccSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Cx/Dx Session.", e);
    }

    return appSession;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.app.IAppSessionFactory#getNewSession(java.lang
   * .String, java.lang.Class, org.jdiameter.api.ApplicationId,
   * java.lang.Object[])
   */
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;

    if (aClass == ClientCxDxSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }

      IClientCxDxSessionData sessionData = (IClientCxDxSessionData) this.sessionDataFactory.getAppSessionData(ClientCxDxSession.class, sessionId);
      sessionData.setApplicationId(applicationId);
      CxDxClientSessionImpl clientSession = new CxDxClientSessionImpl(sessionData, this.getMessageFactory(), this.sessionFactory, this
          .getClientSessionListener());

      iss.addSession(clientSession);
      clientSession.getSessions().get(0).setRequestListener(clientSession);
      appSession = clientSession;
    }
    else if (aClass == ServerCxDxSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IServerCxDxSessionData sessionData = (IServerCxDxSessionData) this.sessionDataFactory.getAppSessionData(ServerCxDxSession.class, sessionId);
      sessionData.setApplicationId(applicationId);
      CxDxServerSessionImpl serverSession = new CxDxServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());

      iss.addSession(serverSession);
      serverSession.getSessions().get(0).setRequestListener(serverSession);
      appSession = serverSession;
    }
    else {
      throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerCxDxSession.class + "," + ClientCxDxSession.class + "]");
    }

    return appSession;
  }

  // Cx/Dx Message Factory Methods ------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createLocationInfoAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public JLocationInfoAnswer createLocationInfoAnswer(Answer answer) {
    return new JLocationInfoAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createLocationInfoRequest(org.jdiameter.api.Request)
   */
  @Override
  public JLocationInfoRequest createLocationInfoRequest(Request request) {
    return new JLocationInfoRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createMultimediaAuthAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public JMultimediaAuthAnswer createMultimediaAuthAnswer(Answer answer) {
    return new JMultimediaAuthAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createMultimediaAuthRequest(org.jdiameter.api.Request)
   */
  @Override
  public JMultimediaAuthRequest createMultimediaAuthRequest(Request request) {
    return new JMultimediaAuthRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createPushProfileAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public JPushProfileAnswer createPushProfileAnswer(Answer answer) {
    return new JPushProfileAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createPushProfileRequest(org.jdiameter.api.Request)
   */
  @Override
  public JPushProfileRequest createPushProfileRequest(Request request) {
    return new JPushProfileRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createRegistrationTerminationAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public JRegistrationTerminationAnswer createRegistrationTerminationAnswer(Answer answer) {
    return new JRegistrationTerminationAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createRegistrationTerminationRequest(org.jdiameter.api.Request)
   */
  @Override
  public JRegistrationTerminationRequest createRegistrationTerminationRequest(Request request) {
    return new JRegistrationTerminationRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createServerAssignmentAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public JServerAssignmentAnswer createServerAssignmentAnswer(Answer answer) {
    return new JServerAssignmentAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createServerAssignmentRequest(org.jdiameter.api.Request)
   */
  @Override
  public JServerAssignmentRequest createServerAssignmentRequest(Request request) {
    return new JServerAssignmentRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createUserAuthorizationAnswer(org.jdiameter.api.Answer)
   */
  @Override
  public JUserAuthorizationAnswer createUserAuthorizationAnswer(Answer answer) {
    return new JUserAuthorizationAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createUserAuthorizationRequest(org.jdiameter.api.Request)
   */
  @Override
  public JUserAuthorizationRequest createUserAuthorizationRequest(Request request) {
    return new JUserAuthorizationRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#getApplicationId()
   */
  @Override
  public long getApplicationId() {
    return 16777216;
  }

  // Session Listeners --------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doLocationInformationRequest(
   *   org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JLocationInfoRequest)
   */
  @Override
  public void doLocationInformationRequest(ServerCxDxSession appSession, JLocationInfoRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doLocationInformationRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doMultimediaAuthRequest(
   *   org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JMultimediaAuthRequest)
   */
  @Override
  public void doMultimediaAuthRequest(ServerCxDxSession appSession, JMultimediaAuthRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doMultimediaAuthRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doOtherEvent(org.jdiameter.api.app.AppSession,
   *   org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doPushProfileAnswer(org.jdiameter.api.cxdx.ServerCxDxSession,
   *   org.jdiameter.api.cxdx.events.JPushProfileRequest, org.jdiameter.api.cxdx.events.JPushProfileAnswer)
   */
  @Override
  public void doPushProfileAnswer(ServerCxDxSession appSession, JPushProfileRequest request, JPushProfileAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doPushProfileAnswer :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doRegistrationTerminationAnswer(org.jdiameter.api.cxdx.ServerCxDxSession,
   *   org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest, org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
   */
  @Override
  public void doRegistrationTerminationAnswer(ServerCxDxSession appSession, JRegistrationTerminationRequest request,
      JRegistrationTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doRegistrationTerminationAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.cxdx.ServerCxDxSessionListener#doServerAssignmentRequest(
   *   org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JServerAssignmentRequest)
   */
  @Override
  public void doServerAssignmentRequest(ServerCxDxSession appSession, JServerAssignmentRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doServerAssignmentRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doUserAuthorizationRequest(
   *   org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JUserAuthorizationRequest)
   */
  @Override
  public void doUserAuthorizationRequest(ServerCxDxSession appSession, JUserAuthorizationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doUserAuthorizationRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doLocationInformationAnswer(org.jdiameter.api.cxdx.ClientCxDxSession,
   *   org.jdiameter.api.cxdx.events.JLocationInfoRequest, org.jdiameter.api.cxdx.events.JLocationInfoAnswer)
   */
  @Override
  public void doLocationInformationAnswer(ClientCxDxSession appSession, JLocationInfoRequest request, JLocationInfoAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doLocationInformationAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doMultimediaAuthAnswer(org.jdiameter.api.cxdx.ClientCxDxSession,
   *   org.jdiameter.api.cxdx.events.JMultimediaAuthRequest, org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer)
   */
  @Override
  public void doMultimediaAuthAnswer(ClientCxDxSession appSession, JMultimediaAuthRequest request, JMultimediaAuthAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doMultimediaAuthAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doPushProfileRequest(
   *   org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JPushProfileRequest)
   */
  @Override
  public void doPushProfileRequest(ClientCxDxSession appSession, JPushProfileRequest request) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doPushProfileRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  /*
   * (non-Javadoc)
   *
   * @seeorg.jdiameter.api.cxdx.ClientCxDxSessionListener#doRegistrationTerminationRequest(
   *   org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest)
   */
  @Override
  public void doRegistrationTerminationRequest(ClientCxDxSession appSession, JRegistrationTerminationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doRegistrationTerminationRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doServerAssignmentAnswer(org.jdiameter.api.cxdx.ClientCxDxSession,
   *   org.jdiameter.api.cxdx.events.JServerAssignmentRequest, org.jdiameter.api.cxdx.events.JServerAssignmentAnswer)
   */
  @Override
  public void doServerAssignmentAnswer(ClientCxDxSession appSession, JServerAssignmentRequest request, JServerAssignmentAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doServerAssignmentAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doUserAuthorizationAnswer(org.jdiameter.api.cxdx.ClientCxDxSession,
   *   org.jdiameter.api.cxdx.events.JUserAuthorizationRequest, org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer)
   */
  @Override
  public void doUserAuthorizationAnswer(ClientCxDxSession appSession, JUserAuthorizationRequest request, JUserAuthorizationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter Cx/Dx Session Factory :: doUserAuthorizationAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Cx/Dx Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Cx/Dx Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

}
