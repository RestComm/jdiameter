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

package org.mobicents.diameter.stack.functional.sy;

import org.jdiameter.api.*;
import org.jdiameter.api.sy.ClientSySession;
import org.jdiameter.api.sy.ClientSySessionListener;
import org.jdiameter.api.sy.ServerSySession;
import org.jdiameter.api.sy.events.SpendingLimitRequest;
import org.jdiameter.api.sy.events.SpendingStatusNotificationAnswer;
import org.jdiameter.common.api.app.sy.ClientSySessionState;
import org.jdiameter.common.impl.app.sy.SySessionFactoryImpl;
import org.mobicents.diameter.stack.functional.StateChange;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Policy and charging control, Spending Limit Report - Sy tests
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public abstract class AbstractClient extends TBase implements ClientSySessionListener {

  protected static final int SL_REQUEST_TYPE_INITIAL = 0;
  protected static final int SL_REQUEST_TYPE_INTERMEDIATE = 1;

  protected ClientSySession clientSySession;
  protected int slRequestNumber = 0;
  protected List<StateChange<ClientSySessionState>> stateChanges = new ArrayList<StateChange<ClientSySessionState>>(); // state changes

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777302));

      SySessionFactoryImpl spendingLimitSessionFactory = new SySessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFactory(ServerSySession.class, spendingLimitSessionFactory);
      sessionFactory.registerAppFactory(ClientSySession.class, spendingLimitSessionFactory);

      /*spendingLimitSessionFactory.setStateListener(this);
      spendingLimitSessionFactory.setClientSessionListener(this);
      spendingLimitSessionFactory.setClientContextListener(this);*/
      this.clientSySession = this.sessionFactory.getNewAppSession(this.sessionFactory.getSessionId("xxTESTxx"),
          getApplicationId(), ClientSySession.class, (Object) null);
    } finally {
      try {
        configStream.close();
      } catch (Exception e) {
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

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    fail("Received \"SuccessMessage\" event, request[" + request + "], answer[" + answer + "]", null);

  }

  @Override
  public Answer processRequest(Request request) {
    fail("Received \"Request\" event, request[" + request + "]", null);
    return null;
  }

  protected SpendingLimitRequest createSLR(int slRequestType, int requestNumber, ClientSySession sySession) throws Exception {
    SpendingLimitRequest slr = null; // new SpendingLimitRequestImpl(sySession.getSessions().get(0).createRequest(SpendingLimitRequest.code, getApplicationId(), getServerRealmName()));

    return slr;
  }

  protected SpendingStatusNotificationAnswer createSNR(int requestNumber, ClientSySession sySession) throws Exception {
    SpendingStatusNotificationAnswer sna = null; // new SpendingStatusNotificationAnswerImpl(sySession.getSessions().get(0).createRequest(StatusNotificationSpendingLimitRequestAnswer.code, getApplicationId(), getServerRealmName()));

    return sna;
  }

  public String getSessionId() {
    return this.clientSySession.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.clientSySession = stack.getSession(sessionId, ClientSySession.class);
  }

  public ClientSySession getSession() {
    return this.clientSySession;
  }

  public List<StateChange<ClientSySessionState>> getStateChanges() {
    return stateChanges;
  }

}
