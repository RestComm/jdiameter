/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
