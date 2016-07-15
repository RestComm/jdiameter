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

package org.jdiameter.client.impl.app.acc;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientAccSessionDataLocalImpl extends AppSessionDataLocalImpl implements IClientAccSessionData {

  protected ClientAccSessionState state = ClientAccSessionState.IDLE;
  protected Request buffer;
  protected String destRealm;
  protected String destHost;
  protected Serializable tid;

  /**
   *
   */
  public ClientAccSessionDataLocalImpl() {
  }

  @Override
  public void setClientAccSessionState(ClientAccSessionState state) {
    this.state = state;
  }

  @Override
  public ClientAccSessionState getClientAccSessionState() {
    return this.state;
  }

  @Override
  public void setInterimTimerId(Serializable tid) {
    this.tid = tid;
  }

  @Override
  public Serializable getInterimTimerId() {
    return this.tid;
  }

  @Override
  public void setDestinationHost(String destHost) {
    this.destHost = destHost;
  }

  @Override
  public String getDestinationHost() {
    return this.destHost;
  }

  @Override
  public void setDestinationRealm(String destRealm) {
    this.destRealm = destRealm;
  }

  @Override
  public String getDestinationRealm() {
    return this.destRealm;
  }

  @Override
  public void setBuffer(Request event) {
    this.buffer = event;
  }

  @Override
  public Request getBuffer() {
    return this.buffer;
  }

}
