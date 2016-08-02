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
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.client.impl.app.auth;

import java.io.Serializable;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.auth.ClientAuthSessionState;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientAuthSessionDataLocalImpl extends AppSessionDataLocalImpl implements IClientAuthSessionData {

  protected ClientAuthSessionState state = ClientAuthSessionState.IDLE;
  protected boolean stateless = true;
  protected String destinationHost;
  protected String destinationRealm;
  protected Serializable tsTimerId;

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setClientAuthSessionState(org.jdiameter.common.api.app.auth.ClientAuthSessionState)
   */
  @Override
  public void setClientAuthSessionState(ClientAuthSessionState state) {
    this.state = state;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getClientAuthSessionState()
   */
  @Override
  public ClientAuthSessionState getClientAuthSessionState() {
    return this.state;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#isStateless()
   */
  @Override
  public boolean isStateless() {
    return this.stateless;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setStateless(boolean)
   */
  @Override
  public void setStateless(boolean b) {
    this.stateless = b;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getDestinationHost()
   */
  @Override
  public String getDestinationHost() {
    return this.destinationHost;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setDestinationHost(java.lang.String)
   */
  @Override
  public void setDestinationHost(String host) {
    this.destinationHost = host;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getDestinationRealm()
   */
  @Override
  public String getDestinationRealm() {
    return this.destinationRealm;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setDestinationRealm(java.lang.String)
   */
  @Override
  public void setDestinationRealm(String realm) {
    this.destinationRealm = realm;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getTsTimerId()
   */
  @Override
  public Serializable getTsTimerId() {
    return this.tsTimerId;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setTsTimerId(java.io.Serializable)
   */
  @Override
  public void setTsTimerId(Serializable tid) {
    this.tsTimerId = tid;
  }

}
