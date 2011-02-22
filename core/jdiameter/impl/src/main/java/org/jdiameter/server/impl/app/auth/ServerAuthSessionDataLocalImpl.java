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
package org.jdiameter.server.impl.app.auth;

import java.io.Serializable;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.auth.ServerAuthSessionState;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServerAuthSessionDataLocalImpl extends AppSessionDataLocalImpl implements IServerAuthSessionData {

  protected ServerAuthSessionState state = ServerAuthSessionState.IDLE;
  protected Serializable tsTimerId;
  protected long tsTimeout;
  protected boolean stateless;

  /**
   * 
   */
  public ServerAuthSessionDataLocalImpl() {

  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#getServerAuthSessionState()
   */
  @Override
  public ServerAuthSessionState getServerAuthSessionState() {
    return this.state;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#setServerAuthSessionState(org.jdiameter.common.api.app.auth.ServerAuthSessionState)
   */
  @Override
  public void setServerAuthSessionState(ServerAuthSessionState state) {
    this.state = state;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#isStateless()
   */
  @Override
  public boolean isStateless() {
    return this.stateless;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#setStateless(boolean)
   */
  @Override
  public void setStateless(boolean stateless) {
    this.stateless = stateless;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#setTsTimeout(long)
   */
  @Override
  public void setTsTimeout(long tsTimeout) {
    this.tsTimeout = tsTimeout;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#getTsTimeout()
   */
  @Override
  public long getTsTimeout() {
    return this.tsTimeout;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#setTsTimerId(java.io.Serializable)
   */
  @Override
  public void setTsTimerId(Serializable tsTimerId) {
    this.tsTimerId = tsTimerId;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.server.impl.app.auth.IServerAuthSessionData#getTsTimerId()
   */
  @Override
  public Serializable getTsTimerId() {
    return this.tsTimerId;
  }

}
