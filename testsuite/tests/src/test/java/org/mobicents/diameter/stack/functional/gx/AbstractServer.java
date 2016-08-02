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
package org.mobicents.diameter.stack.functional.gx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.ServerGxSession;
import org.jdiameter.api.gx.ServerGxSessionListener;
import org.jdiameter.api.gx.events.GxReAuthRequest;
import org.jdiameter.common.api.app.gx.IServerGxSessionContext;
import org.jdiameter.common.api.app.gx.ServerGxSessionState;
import org.jdiameter.common.impl.app.gx.GxReAuthRequestImpl;
import org.jdiameter.common.impl.app.gx.GxSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.StateChange;
import org.mobicents.diameter.stack.functional.TBase;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class AbstractServer extends TBase implements ServerGxSessionListener, IServerGxSessionContext {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected static final int CC_REQUEST_TYPE_INITIAL = 1;
  protected static final int CC_REQUEST_TYPE_INTERIM = 2;
  protected static final int CC_REQUEST_TYPE_TERMINATE = 3;
  protected static final int CC_REQUEST_TYPE_EVENT = 4;

  protected ServerGxSession serverGxSession;
  protected int ccRequestNumber = 0;

  protected List<StateChange<ServerGxSessionState>> stateChanges = new ArrayList<StateChange<ServerGxSessionState>>(); // state changes

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777224));
      GxSessionFactoryImpl creditControlSessionFactory = new GxSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerGxSession.class, creditControlSessionFactory);
      sessionFactory.registerAppFacory(ClientGxSession.class, creditControlSessionFactory);

      creditControlSessionFactory.setStateListener(this);
      creditControlSessionFactory.setServerSessionListener(this);
      creditControlSessionFactory.setServerContextListener(this);
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

  @Override
  public void sessionSupervisionTimerExpired(ServerGxSession session) {
    // NOP
  }

  @Override
  public void sessionSupervisionTimerStarted(ServerGxSession session, ScheduledFuture future) {
    // NOP
  }

  @Override
  public void sessionSupervisionTimerReStarted(ServerGxSession session, ScheduledFuture future) {
    // NOP
  }

  @Override
  public void sessionSupervisionTimerStopped(ServerGxSession session, ScheduledFuture future) {
    // NOP
  }

  @Override
  public long getDefaultValidityTime() {
    return 120;
  }

  protected GxReAuthRequest createRAR(int reAuthRequestType, ServerGxSession gxSession) throws Exception {
    //  <RA-Request> ::= < Diameter Header: 258, REQ, PXY >
    GxReAuthRequest rar = new GxReAuthRequestImpl(gxSession.getSessions().get(0)
        .createRequest(ReAuthRequest.code, getApplicationId(), getClientRealmName()));

    // AVPs present by default: Origin-Host, Origin-Realm, Session-Id,
    // Vendor-Specific-Application-Id, Destination-Realm
    AvpSet rarAvps = rar.getMessage().getAvps();

    //  < Session-Id >
    //  { Auth-Application-Id }
    //  { Origin-Host }
    rarAvps.removeAvp(Avp.ORIGIN_HOST);
    rarAvps.addAvp(Avp.ORIGIN_HOST, getServerURI(), true);

    //  { Origin-Realm }
    //  { Destination-Realm }
    //  { Destination-Host }
    //  { Re-Auth-Request-Type }
    rarAvps.addAvp(Avp.RE_AUTH_REQUEST_TYPE, reAuthRequestType);
    //  [ Session-Release-Cause ]
    //  [ Origin-State-Id ]
    // *[ Event-Trigger ]
    //  [ Event-Report-Indication ]
    // *[ Charging-Rule-Remove ]
    // *[ Charging-Rule-Install ]
    //  [ Default-EPS-Bearer-QoS ]
    // *[ QoS-Information ]
    //  [ Revalidation-Time ]
    // *[ Usage-Monitoring-Information ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]
    // *[ AVP]

    return rar;
  }

  public String getSessionId() {
    return this.serverGxSession.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.serverGxSession = stack.getSession(sessionId, ServerGxSession.class);
  }

  public ServerGxSession getSession() {
    return this.serverGxSession;
  }

  public List<StateChange<ServerGxSessionState>> getStateChanges() {
    return stateChanges;
  }
}
