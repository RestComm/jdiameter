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
package org.jdiameter.common.impl.data;

import java.util.concurrent.ConcurrentHashMap;

import org.jdiameter.api.BaseSession;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local implementation of session datasource for {@link ISessionDatasource}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LocalDataSource implements ISessionDatasource {

  private ConcurrentHashMap<String, BaseSession> sessionData = new ConcurrentHashMap<String, BaseSession>();
  private ConcurrentHashMap<String, NetworkReqListener> sessionListeners = new ConcurrentHashMap<String, NetworkReqListener>();

  private static final Logger logger = LoggerFactory.getLogger(LocalDataSource.class);

  public LocalDataSource() {
    // NOP
  }

  public LocalDataSource(IContainer container) {
    // NOP
  }

  public void setSessionListener(String sessionId, NetworkReqListener data) {
    if(logger.isDebugEnabled()) {
      logger.debug("setSessionListener({}, {})", sessionId, data);
    }
    sessionListeners.put(sessionId, data);
  }

  public NetworkReqListener getSessionListener(String sessionId) {
    if(logger.isDebugEnabled()) {
      logger.debug("getSessionListener({}) => {}", sessionId, sessionListeners.get(sessionId));
    }
    return sessionListeners.get(sessionId);
  }

  public NetworkReqListener removeSessionListener(String sessionId) {
    if(logger.isDebugEnabled()) {
      logger.debug("removeSessionListener({}) => {}", sessionId, sessionListeners.get(sessionId));
    }
    return sessionListeners.remove(sessionId);
  }

  public void addSession(BaseSession session) {
    if(logger.isDebugEnabled()) {
      logger.debug("addSession({})", session);
    }
    this.sessionData.put(session.getSessionId(), session);
  }

  public BaseSession getSession(String sessionId) {
    if(logger.isDebugEnabled()) {
      logger.debug("getSession({}) => {}", sessionId, sessionData.get(sessionId));
    }
    return this.sessionData.get(sessionId);
  }

  public void removeSession(String sessionId) {
    if(logger.isDebugEnabled()) {
      logger.debug("removeSession({}) => {}", sessionId, sessionData.get(sessionId));
    }
    this.sessionData.remove(sessionId);
    removeSessionListener(sessionId);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#updateSession(org.jdiameter.api.BaseSession)
   */
  public void updateSession(BaseSession session) {
    // NOP, it's local
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#start()
   */
  public void start() {
    // NOP
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#stop()
   */
  public void stop() {
    // NOP
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#isClustered()
   */
  public boolean isClustered() {
    return false;
  }

}
