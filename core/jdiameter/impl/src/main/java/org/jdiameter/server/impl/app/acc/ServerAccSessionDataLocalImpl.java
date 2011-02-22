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
package org.jdiameter.server.impl.app.acc;

import java.io.Serializable;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServerAccSessionDataLocalImpl extends AppSessionDataLocalImpl implements IServerAccSessionData {

  protected ServerAccSessionState state = ServerAccSessionState.IDLE;
  protected boolean stateles = true;
  protected long tsTimeout;
  protected Serializable tsTimerId;

  /**
   * 
   */
  public ServerAccSessionDataLocalImpl() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.server.impl.app.acc.IServerAccSessionData#
   * setServerAccSessionState
   * (org.jdiameter.common.api.app.acc.ServerAccSessionState)
   */
  @Override
  public void setServerAccSessionState(ServerAccSessionState value) {
    this.state = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.server.impl.app.acc.IServerAccSessionData#
   * getServerAccSessionState()
   */
  @Override
  public ServerAccSessionState getServerAccSessionState() {
    return this.state;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.server.impl.app.acc.IServerAccSessionData#setStateles(boolean
   * )
   */
  @Override
  public void setStateless(boolean value) {
    this.stateles = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.server.impl.app.acc.IServerAccSessionData#isStateles()
   */
  @Override
  public boolean isStateless() {
    return this.stateles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.server.impl.app.acc.IServerAccSessionData#setTsTimeout(
   * long)
   */
  @Override
  public void setTsTimeout(long value) {
    this.tsTimeout = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.server.impl.app.acc.IServerAccSessionData#getTsTimeout()
   */
  @Override
  public long getTsTimeout() {
    return this.tsTimeout;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.server.impl.app.acc.IServerAccSessionData#setTsTimerId(
   * java.io.Serializable)
   */
  @Override
  public void setTsTimerId(Serializable value) {
    this.tsTimerId = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.server.impl.app.acc.IServerAccSessionData#getTsTimerId()
   */
  @Override
  public Serializable getTsTimerId() {
    return this.tsTimerId;
  }

}
