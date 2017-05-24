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

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.sy.ClientSySession;
import org.jdiameter.api.sy.ServerSySession;
import org.jdiameter.api.sy.ServerSySessionListener;
import org.jdiameter.common.api.app.sy.ServerSySessionState;
import org.jdiameter.common.impl.app.sy.SySessionFactoryImpl;
import org.mobicents.diameter.stack.functional.StateChange;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Policy and charging control, Spending Limit Report - Sy tests
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public abstract class AbstractServer extends TBase implements ServerSySessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected static final int SL_REQUEST_TYPE_INITIAL = 0;
  protected static final int SL_REQUEST_TYPE_INTERMEDIATE = 1;

  protected ServerSySession serverSySession;
  protected int ccRequestNumber = 0;

  protected List<StateChange<ServerSySessionState>> stateChanges = new ArrayList<StateChange<ServerSySessionState>>(); // state changes

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(0, 4));
      SySessionFactoryImpl creditControlSessionFactory = new SySessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFactory(ServerSySession.class, creditControlSessionFactory);
      sessionFactory.registerAppFactory(ClientSySession.class, creditControlSessionFactory);

      /*creditControlSessionFactory.setStateListener(this);
      creditControlSessionFactory.setServerSessionListener(this);
      creditControlSessionFactory.setServerContextListener(this);*/

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

  /*@Override
  public void sessionSupervisionTimerExpired(ServerCCASession session) {
    // NOP
  }

  @Override
  public void sessionSupervisionTimerStarted(ServerCCASession session, ScheduledFuture future) {
    // NOP
  }

  @Override
  public void sessionSupervisionTimerReStarted(ServerCCASession session, ScheduledFuture future) {
    // NOP
  }

  @Override
  public void sessionSupervisionTimerStopped(ServerCCASession session, ScheduledFuture future) {
    // NOP
  }

  @Override
  public long getDefaultValidityTime() {
    return 120;
  }

  public String getSessionId() {
    return this.serverCCASession.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.serverCCASession = stack.getSession(sessionId, ServerCCASession.class);
  }

  public ServerCCASession getSession() {
    return this.serverCCASession;
  }

  public List<StateChange<ServerCCASessionState>> getStateChanges() {
    return stateChanges;
  }*/
}
