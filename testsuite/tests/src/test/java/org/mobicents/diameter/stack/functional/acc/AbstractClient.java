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
package org.mobicents.diameter.stack.functional.acc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Request;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;
import org.jdiameter.common.api.app.acc.IClientAccActionContext;
import org.jdiameter.common.impl.app.acc.AccSessionFactoryImpl;
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.mobicents.diameter.stack.functional.StateChange;
import org.mobicents.diameter.stack.functional.TBase;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class AbstractClient extends TBase implements ClientAccSessionListener, IClientAccActionContext {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected static final int ACC_REQUEST_TYPE_INITIAL = 2;
  protected static final int ACC_REQUEST_TYPE_INTERIM = 3;
  protected static final int ACC_REQUEST_TYPE_TERMINATE = 4;
  protected static final int ACC_REQUEST_TYPE_EVENT = 1;

  // this is custom id.
  protected ClientAccSession clientAccSession;
  protected int ccRequestNumber = 0;
  protected List<StateChange<ClientAccSessionState>> stateChanges = new ArrayList<StateChange<ClientAccSessionState>>(); // state changes

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAccAppId(0, 300));
      AccSessionFactoryImpl creditControlSessionFactory = new AccSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerAccSession.class, creditControlSessionFactory);
      sessionFactory.registerAppFacory(ClientAccSession.class, creditControlSessionFactory);

      creditControlSessionFactory.setStateListener(this);
      creditControlSessionFactory.setClientSessionListener(this);
      creditControlSessionFactory.setClientContextListener(this);
      this.clientAccSession = this.sessionFactory.getNewAppSession(
          this.sessionFactory.getSessionId("xxTESTxx"), getApplicationId(), ClientAccSession.class, (Object) null);
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

  // ----------- conf parts

  // ----------- should not be called..

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    fail("Received \"SuccessMessage\" event, request[" + request + "], answer[" + answer + "]", null);

  }

  @Override
  public void timeoutExpired(Request request) {
    fail("Received \"Timoeout\" event, request[" + request + "]", null);

  }

  @Override
  public Answer processRequest(Request request) {
    fail("Received \"Request\" event, request[" + request + "]", null);
    return null;
  }

  // ------------ leave those

  @Override
  public void interimIntervalElapses(ClientAccSession appSession, Request interimRequest) throws InternalException {
    // NOP
  }

  @Override
  public boolean failedSendRecord(ClientAccSession appSession, Request accRequest) throws InternalException {
    // NOP
    return false;
  }

  @Override
  public void disconnectUserOrDev(ClientAccSession appSession, Request sessionTermRequest) throws InternalException {
    // NOP
  }

  // ---------- some helper methods.
  protected AccountRequest createAcc(int ccRequestType, int requestNumber, ClientAccSession ccaSession) throws Exception {

    // Create Credit-Control-Request
    AccountRequest ccr = new AccountRequestImpl(ccaSession.getSessions().get(0).createRequest(AccountRequest.code, getApplicationId(), getServerRealmName()));

    // AVPs present by default: Origin-Host, Origin-Realm, Session-Id,
    // Vendor-Specific-Application-Id, Destination-Realm
    AvpSet ccrAvps = ccr.getMessage().getAvps();
    // <ACR> ::= < Diameter Header: 271, REQ, PXY >
    // < Session-Id >
    // { Origin-Host }
    ccrAvps.removeAvp(Avp.ORIGIN_HOST);
    ccrAvps.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);

    // { Origin-Realm }
    // { Destination-Realm } - set in constructor.
    // { Accounting-Record-Type }
    ccr.setAccountingRecordType(ccRequestType);
    // EVENT_RECORD 1
    // An Accounting Event Record is used to indicate that a one-time
    // event has occurred (meaning that the start and end of the event
    // are simultaneous). This record contains all information relevant
    // to the service, and is the only record of the service.
    //
    // START_RECORD 2
    // An Accounting Start, Interim, and Stop Records are used to
    // indicate that a service of a measurable length has been given. An
    // Accounting Start Record is used to initiate an accounting session,
    // and contains accounting information that is relevant to the
    // initiation of the session.
    //
    // INTERIM_RECORD 3
    // An Interim Accounting Record contains cumulative accounting
    // information for an existing accounting session. Interim
    // Accounting Records SHOULD be sent every time a re-authentication
    // or re-authorization occurs. Further, additional interim record
    // triggers MAY be defined by application-specific Diameter
    // applications. The selection of whether to use INTERIM_RECORD
    // records is done by the Acct-Interim-Interval AVP.
    //
    // STOP_RECORD 4
    // An Accounting Stop Record is sent to terminate an accounting
    // session and contains cumulative accounting information relevant to
    // the existing session.

    // { Accounting-Record-Number }
    ccr.setAccountingRecordNumber(requestNumber);
    // [ Acct-Application-Id ]
    if (ccrAvps.getAvp(Avp.ACCT_APPLICATION_ID) == null) {
      ccrAvps.addAvp(Avp.ACCT_APPLICATION_ID, getApplicationId().getAcctAppId());
    }
    // [ Vendor-Specific-Application-Id ]
    // [ User-Name ] - just to have it, its almost always mandatory
    ccrAvps.addAvp(Avp.USER_NAME, "ala@kota.ma.bez.siersci", false);
    // [ Accounting-Sub-Session-Id ]
    // [ Acct-Session-Id ]
    // [ Acct-Multi-Session-Id ]
    // [ Acct-Interim-Interval ]
    // [ Accounting-Realtime-Required ]
    // [ Origin-State-Id ]
    // [ Event-Timestamp ]
    // * [ Proxy-Info ]
    // * [ Route-Record ]
    // * [ AVP ]

    return ccr;
  }

  public String getSessionId() {
    return this.clientAccSession.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.clientAccSession = stack.getSession(sessionId, ClientAccSession.class);
  }

  public ClientAccSession getSession() {
    return this.clientAccSession;
  }

  public List<StateChange<ClientAccSessionState>> getStateChanges() {
    return stateChanges;
  }

  protected abstract int getChargingUnitsTime();

  protected abstract String getServiceContextId();
}
