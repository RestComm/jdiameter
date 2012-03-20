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

package org.jdiameter.server.impl.app.ro;

import java.io.Serializable;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.ro.ServerRoSessionState;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServerRoSessionDataLocalImpl extends AppSessionDataLocalImpl implements IServerRoSessionData {

  protected boolean stateless = true;
  protected ServerRoSessionState state = ServerRoSessionState.IDLE;
  protected Serializable tccTimerId;

  /**
   * 
   */
  public ServerRoSessionDataLocalImpl() {

  }

  public boolean isStateless() {
    return stateless;
  }

  public void setStateless(boolean stateless) {
    this.stateless = stateless;
  }

  public ServerRoSessionState getServerRoSessionState() {
    return state;
  }

  public void setServerRoSessionState(ServerRoSessionState state) {
    this.state = state;
  }

  public Serializable getTccTimerId() {
    return tccTimerId;
  }

  public void setTccTimerId(Serializable tccTimerId) {
    this.tccTimerId = tccTimerId;
  }

}
