/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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
package org.mobicents.diameter.stack.functional.cxdx;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 * @author baranowb
 *
 */
public abstract class AbstractClient extends TBase implements ClientCxDxSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ClientCxDxSession clientCxDxSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777216));
      CxDxSessionFactoryImpl shSessionFactory = new CxDxSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerCxDxSession.class, shSessionFactory);
      sessionFactory.registerAppFacory(ClientCxDxSession.class, shSessionFactory);

      shSessionFactory.setClientSessionListener(this);

      this.clientCxDxSession = this.sessionFactory.getNewAppSession(this.sessionFactory.getSessionId("xxTESTxx"), getApplicationId(), ClientCxDxSession.class,
          null); // true...
    }
    finally {
      try {
        configStream.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  // ----------- delegate methods so

  public void start() throws IllegalDiameterStateException, InternalException {
    stack.start();
  }

  public void start(Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    stack.start(mode, timeOut, timeUnit);
  }

  public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
    stack.stop(timeOut, timeUnit, disconnectCause);
  }

  public void stop(int disconnectCause) {
    stack.stop(disconnectCause);
  }

  // ------- def methods, to fail :)

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException,
  OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doUserAuthorizationAnswer(ClientCxDxSession session, JUserAuthorizationRequest request, JUserAuthorizationAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"UAA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doServerAssignmentAnswer(ClientCxDxSession session, JServerAssignmentRequest request, JServerAssignmentAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"SAA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doRegistrationTerminationRequest(ClientCxDxSession session, JRegistrationTerminationRequest request)
      throws InternalException, IllegalDiameterStateException,
  RouteException, OverloadException {
    fail("Received \"RTR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doLocationInformationAnswer(ClientCxDxSession session, JLocationInfoRequest request, JLocationInfoAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"LIA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doPushProfileRequest(ClientCxDxSession session, JPushProfileRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"PPR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doMultimediaAuthAnswer(ClientCxDxSession session, JMultimediaAuthRequest request, JMultimediaAuthAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"MAA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  // ----------- conf parts

  public String getSessionId() {
    return this.clientCxDxSession.getSessionId();
  }

  public ClientCxDxSession getSession() {
    return this.clientCxDxSession;
  }

  // ----------- helper
  protected Request createRequest(AppSession session, int code) {
    Request r = session.getSessions().get(0).createRequest(code, getApplicationId(), getServerRealmName());

    AvpSet reqSet = r.getAvps();
    // { Auth-Session-State }
    reqSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    reqSet.removeAvp(Avp.ORIGIN_HOST);
    reqSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    return r;
  }
}
