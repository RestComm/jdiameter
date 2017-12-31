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

package org.jdiameter.common.impl.app.s6a;

import org.jdiameter.api.Answer;
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
import org.jdiameter.api.s6a.ClientS6aSession;
import org.jdiameter.api.s6a.ClientS6aSessionListener;
import org.jdiameter.api.s6a.ServerS6aSession;
import org.jdiameter.api.s6a.ServerS6aSessionListener;
import org.jdiameter.api.s6a.events.JAuthenticationInformationAnswer;
import org.jdiameter.api.s6a.events.JAuthenticationInformationRequest;
import org.jdiameter.api.s6a.events.JCancelLocationAnswer;
import org.jdiameter.api.s6a.events.JCancelLocationRequest;
import org.jdiameter.api.s6a.events.JDeleteSubscriberDataAnswer;
import org.jdiameter.api.s6a.events.JDeleteSubscriberDataRequest;
import org.jdiameter.api.s6a.events.JInsertSubscriberDataAnswer;
import org.jdiameter.api.s6a.events.JInsertSubscriberDataRequest;
import org.jdiameter.api.s6a.events.JNotifyAnswer;
import org.jdiameter.api.s6a.events.JNotifyRequest;
import org.jdiameter.api.s6a.events.JPurgeUEAnswer;
import org.jdiameter.api.s6a.events.JPurgeUERequest;
import org.jdiameter.api.s6a.events.JResetAnswer;
import org.jdiameter.api.s6a.events.JResetRequest;
import org.jdiameter.api.s6a.events.JUpdateLocationAnswer;
import org.jdiameter.api.s6a.events.JUpdateLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.s6a.IClientS6aSessionData;
import org.jdiameter.client.impl.app.s6a.S6aClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6a.IS6aMessageFactory;
import org.jdiameter.common.api.app.s6a.IS6aSessionData;
import org.jdiameter.common.api.app.s6a.IS6aSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.s6a.IServerS6aSessionData;
import org.jdiameter.server.impl.app.s6a.S6aServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:paul.carter-brown@smilecoms.com"> Paul Carter-Brown </a>
 */
public class S6aSessionFactoryImpl implements IS6aSessionFactory, ServerS6aSessionListener, ClientS6aSessionListener, IS6aMessageFactory,
    StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(S6aSessionFactoryImpl.class);

  protected ISessionFactory sessionFactory;

  protected ServerS6aSessionListener serverSessionListener;
  protected ClientS6aSessionListener clientSessionListener;

  protected IS6aMessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateListener;
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<IS6aSessionData> sessionDataFactory;

  public S6aSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IS6aSessionData>) this.iss.getDataFactory(IS6aSessionData.class);
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerS6aSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerS6aSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ClientS6aSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientS6aSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IS6aMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  @Override
  public void setMessageFactory(IS6aMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
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
      if (aClass == ServerS6aSession.class) {
        IServerS6aSessionData sessionData = (IServerS6aSessionData) this.sessionDataFactory.getAppSessionData(ServerS6aSession.class, sessionId);
        S6aServerSessionImpl serverSession = new S6aServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else if (aClass == ClientS6aSession.class) {
        IClientS6aSessionData sessionData = (IClientS6aSessionData) this.sessionDataFactory.getAppSessionData(ClientS6aSession.class, sessionId);
        S6aClientSessionImpl clientSession = new S6aClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerS6aSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new S6a Session.", e);
    }

    return appSession;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.IAppSessionFactory#getNewSession(java.lang.String, java.lang.Class, org.jdiameter.api.ApplicationId, java.lang.Object[])
   */
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;

    if (aClass == ServerS6aSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IServerS6aSessionData sessionData = (IServerS6aSessionData) this.sessionDataFactory.getAppSessionData(ServerS6aSession.class, sessionId);
      sessionData.setApplicationId(applicationId);
      S6aServerSessionImpl serverSession = new S6aServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());

      iss.addSession(serverSession);
      serverSession.getSessions().get(0).setRequestListener(serverSession);
      appSession = serverSession;
    }
    else if (aClass == ClientS6aSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IClientS6aSessionData sessionData = (IClientS6aSessionData) this.sessionDataFactory.getAppSessionData(ClientS6aSession.class, sessionId);
      sessionData.setApplicationId(applicationId);
      S6aClientSessionImpl clientSession = new S6aClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());

      iss.addSession(clientSession);
      clientSession.getSessions().get(0).setRequestListener(clientSession);
      appSession = clientSession;
    }
    else {
      throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerS6aSession.class + "]");
    }

    return appSession;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum, java.lang.Enum)
   */
  @Override
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter S6a Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  @Override
  public long getApplicationId() {
    return 16777251;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter S6a Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  @Override
  public JAuthenticationInformationAnswer createAuthenticationInformationAnswer(Answer answer) {
    return new JAuthenticationInformationAnswerImpl(answer);
  }

  @Override
  public JAuthenticationInformationRequest createAuthenticationInformationRequest(Request request) {
    return new JAuthenticationInformationRequestImpl(request);
  }

  @Override
  public JUpdateLocationRequest createUpdateLocationRequest(Request request) {
    return new JUpdateLocationRequestImpl(request);
  }

  @Override
  public JUpdateLocationAnswer createUpdateLocationAnswer(Answer answer) {
    return new JUpdateLocationAnswerImpl(answer);
  }

  @Override
  public JPurgeUERequest createPurgeUERequest(Request request) {
    return new JPurgeUERequestImpl(request);
  }

  @Override
  public JPurgeUEAnswer createPurgeUEAnswer(Answer answer) {
    return new JPurgeUEAnswerImpl(answer);
  }

  @Override
  public JCancelLocationRequest createCancelLocationRequest(Request request) {
    return new JCancelLocationRequestImpl(request);
  }

  @Override
  public JCancelLocationAnswer createCancelLocationAnswer(Answer answer) {
    return new JCancelLocationAnswerImpl(answer);
  }

  @Override
  public JInsertSubscriberDataRequest createInsertSubscriberDataRequest(Request request) {
    return new JInsertSubscriberDataRequestImpl(request);
  }

  @Override
  public JInsertSubscriberDataAnswer createInsertSubscriberDataAnswer(Answer answer) {
    return new JInsertSubscriberDataAnswerImpl(answer);
  }

  @Override
  public JDeleteSubscriberDataRequest createDeleteSubscriberDataRequest(Request request) {
    return new JDeleteSubscriberDataRequestImpl(request);
  }

  @Override
  public JDeleteSubscriberDataAnswer createDeleteSubscriberDataAnswer(Answer answer) {
    return new JDeleteSubscriberDataAnswerImpl(answer);
  }

  @Override
  public JResetRequest createResetRequest(Request request) {
    return new JResetRequestImpl(request);
  }

  @Override
  public JResetAnswer createResetAnswer(Answer answer) {
    return new JResetAnswerImpl(answer);
  }

  @Override
  public JNotifyRequest createNotifyRequest(Request request) {
    return new JNotifyRequestImpl(request);
  }

  @Override
  public JNotifyAnswer createNotifyAnswer(Answer answer) {
    return new JNotifyAnswerImpl(answer);
  }

  @Override
  public void doAuthenticationInformationRequestEvent(ServerS6aSession appSession, JAuthenticationInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doAuthenticationInformationRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  @Override
  public void doPurgeUERequestEvent(ServerS6aSession appSession, JPurgeUERequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doPurgeUERequest :: appSession[{}], Request[{}]", appSession, request);
  }

  @Override
  public void doUpdateLocationRequestEvent(ServerS6aSession appSession, JUpdateLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doUpdateLocationRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  @Override
  public void doNotifyRequestEvent(ServerS6aSession appSession, JNotifyRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doNotifyRequest :: appSession[{}], Request[{}]", appSession, request);
  }

  @Override
  public void doCancelLocationAnswerEvent(ServerS6aSession appSession, JCancelLocationRequest request, JCancelLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doCancelLocationAnswer :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doInsertSubscriberDataAnswerEvent(ServerS6aSession appSession, JInsertSubscriberDataRequest request, JInsertSubscriberDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doInsertSubscriberDataAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  @Override
  public void doDeleteSubscriberDataAnswerEvent(ServerS6aSession appSession, JDeleteSubscriberDataRequest request, JDeleteSubscriberDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doDeleteSubscriberDataAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  @Override
  public void doResetAnswerEvent(ServerS6aSession appSession, JResetRequest request, JResetAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doResetAnswer :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doCancelLocationRequestEvent(ClientS6aSession appSession, JCancelLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doCancelLocationRequest :: appSession[{}], Request[{}]", new Object[]{appSession, request});
  }

  @Override
  public void doInsertSubscriberDataRequestEvent(ClientS6aSession appSession, JInsertSubscriberDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doInsertSubscriberDataRequest :: appSession[{}], Request[{}]", new Object[]{appSession, request});
  }

  @Override
  public void doDeleteSubscriberDataRequestEvent(ClientS6aSession appSession, JDeleteSubscriberDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doDeleteSubscriberDataRequest :: appSession[{}], Request[{}]", new Object[]{appSession, request});
  }

  @Override
  public void doResetRequestEvent(ClientS6aSession appSession, JResetRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doResetRequest :: appSession[{}], Request[{}]", new Object[]{appSession, request});
  }

  @Override
  public void doAuthenticationInformationAnswerEvent(ClientS6aSession appSession, JAuthenticationInformationRequest request,
      JAuthenticationInformationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doAuthenticationInformationAnswer :: appSession[{}], Request[{}], Answer[{}]",
        new Object[]{appSession, request, answer});
  }

  @Override
  public void doPurgeUEAnswerEvent(ClientS6aSession appSession, JPurgeUERequest request, JPurgeUEAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doPurgeUEAnswer :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doUpdateLocationAnswerEvent(ClientS6aSession appSession, JUpdateLocationRequest request, JUpdateLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doUpdateLocationAnswer :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doNotifyAnswerEvent(ClientS6aSession appSession, JNotifyRequest request, JNotifyAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6a Session Factory :: doNotifyAnswer :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

}
