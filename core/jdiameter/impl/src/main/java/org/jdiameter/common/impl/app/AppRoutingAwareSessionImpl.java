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
 */

package org.jdiameter.common.impl.app;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.api.data.IRoutingAwareSessionDatasource;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static org.jdiameter.client.impl.helpers.Parameters.SessionTimeOut;

/**
 * Routing aware extension of {@link AppSessionImpl} that enables proper diameter session
 * load balancing. It provides diameter session persistence which maps a single diameter
 * session to a single peer which is processing the session.
 */
public abstract class AppRoutingAwareSessionImpl extends AppSessionImpl {

  private static final Logger logger = LoggerFactory.getLogger(AppRoutingAwareSessionImpl.class);

  private transient IPeerTable peerTable = null;
  private transient IRoutingAwareSessionDatasource sessionPersistenceStorage = null;

  private final int sesInactivityTimerVal;
  private Serializable sesInactivityTimerId = null;

  /**
   * Parameterized constructor. If session persistence is supposed to be enabled, sessionStorage
   * argument should be of type {@link org.jdiameter.common.impl.data.RoutingAwareDataSource}.
   *
   * @param sessionStorage session datasource
   * @param sessionFactory session factory
   * @param appSessionData session data
   */
  public AppRoutingAwareSessionImpl(ISessionDatasource sessionStorage, ISessionFactory sessionFactory, IAppSessionData appSessionData) {
    super(sessionFactory, appSessionData);
    peerTable = sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(IPeerTable.class);
    //TODO [bk] to be removed - sesInactivityTimerVal
    sesInactivityTimerVal = sessionFactory.getContainer().getConfiguration().getIntValue(SessionTimeOut.ordinal(), (Integer)
        SessionTimeOut.defValue()) * 1000;
    if (sessionStorage instanceof IRoutingAwareSessionDatasource) {
      sessionPersistenceStorage = (IRoutingAwareSessionDatasource) sessionStorage;
    }
  }

  /**
   * Initiates session persistence record, i.e. assigns the current session to a peer which is
   * processing it. Session persistence record shall be created after a peer had answered the
   * first (initial) request for that session.
   *
   * @param reqEvent request that had been sent beforehand
   * @param ansEvent response that has been just received
   */
  protected void initSessionPersistenceContext(AppEvent reqEvent, AppEvent ansEvent) {
    try {
      IPeer peer = null;
      if (reqEvent.getMessage() instanceof IMessage) {
        sessionPersistenceStorage.clearUnanswerablePeers(this.getSessionId());
        peer = ((IMessage) reqEvent.getMessage()).getPeer();
      }
      else {
        logger.warn("Cannot retrieve message detailed context for Session-Id/activityId [{}]", this.getSessionId());
      }

      if (peer == null) {
        logger.warn("Taking peer from Origin-Host AVP as no peer is assigned yet to the following message in session [{}]: [{}]", this.getSessionId(),
            reqEvent.getMessage().getAvps());
        peer = peerTable.getPeer(ansEvent.getOriginHost());
      }

      sessionPersistenceStorage.setSessionPeer(this.getSessionId(), peer);
      if (logger.isDebugEnabled()) {
        logger.debug("Session persistent routing will be enforced for Session-Id [{}] with peer [{}]", this.getSessionId(), peer);
      }

    } catch (Exception ex) {
      logger.error("Cannot update session persistence data, default routing will be applied", ex);
    }
  }

  /**
   * Removes mapping between current session and the peer that has been assigned so far.
   *
   * @return peer name that has been assigned so far
   */
  protected String flushSessionPersistenceContext() {
    try {
      return sessionPersistenceStorage.removeSessionPeer(this.getSessionId());
    } catch (Exception ex) {
      logger.error("Cannot update session persistence data", ex);
      return null;
    }
  }

  /**
   * Starts maximum session inactivity timer which defines how much time the persistence record
   * should be kept if there is no request sent within a session.
   */
  //TODO [bk] obsolete IDLE_SESSION_TIMER_NAME is started in Base session impl
  protected void startSessionInactivityTimer() {
    logger.debug("Scheduling session inactivity timer equal to [{}] ms", sesInactivityTimerVal);
    stopSessionInactivityTimer();
    this.sesInactivityTimerId = this.timerFacility.schedule(this.getSessionId(), IDLE_SESSION_TIMER_NAME, sesInactivityTimerVal);
  }

  /**
   * Stops session inactivity timer.
   */
  protected void stopSessionInactivityTimer() {
    if (this.sesInactivityTimerId != null) {
      logger.debug("Stopping session inactivity timer [{}]", this.sesInactivityTimerId);
      timerFacility.cancel(this.sesInactivityTimerId);
      this.sesInactivityTimerId = null;
    }
  }

  /**
   * Handles expiry of session inactivity timer. Should be called by any subclasses which define
   * any additional timers.
   *
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
      //no need to interfere with session state machine (simply remove routing context used for sticky sessions based routing)
      String oldPeer = flushSessionPersistenceContext();
      logger.debug("Session inactivity timer expired so routing context for peer [{}] was removed from session [{}]", oldPeer, this.getSessionId());
    }
  }
}
