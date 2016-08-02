/*
 * JBoss, Home of Professional Open Source
 * Copyright XXXX, Red Hat Middleware LLC, and individual contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional.cca;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.api.app.cca.ServerCCASessionState;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.mobicents.diameter.stack.functional.StateChange;
import org.mobicents.diameter.stack.functional.TBase;

/**
 * @author baranowb
 *
 */
public abstract class AbstractServer extends TBase implements ServerCCASessionListener, IServerCCASessionContext {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected static final int CC_REQUEST_TYPE_INITIAL = 1;
  protected static final int CC_REQUEST_TYPE_INTERIM = 2;
  protected static final int CC_REQUEST_TYPE_TERMINATE = 3;
  protected static final int CC_REQUEST_TYPE_EVENT = 4;

  protected ServerCCASession serverCCASession;
  protected int ccRequestNumber = 0;

  protected List<StateChange<ServerCCASessionState>> stateChanges = new ArrayList<StateChange<ServerCCASessionState>>(); // state changes

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(0, 4));
      CCASessionFactoryImpl creditControlSessionFactory = new CCASessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerCCASession.class, creditControlSessionFactory);
      sessionFactory.registerAppFacory(ClientCCASession.class, creditControlSessionFactory);

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
  }
}
