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

package org.jdiameter.client.impl.app.ro;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.ro.ClientRoSessionState;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientRoSessionDataLocalImpl extends AppSessionDataLocalImpl implements IClientRoSessionData {

  protected boolean isEventBased = true;
  protected boolean requestTypeSet = false;
  protected ClientRoSessionState state = ClientRoSessionState.IDLE;
  protected Serializable txTimerId;
  //protected JCreditControlRequest txTimerRequest;
  protected Request txTimerRequest;

  // Event Based Buffer
  //protected Message buffer = null;
  protected Request buffer;

  protected int gatheredRequestedAction = NON_INITIALIZED;

  protected int gatheredCCFH = NON_INITIALIZED;
  protected int gatheredDDFH = NON_INITIALIZED;

  /**
   *
   */
  public ClientRoSessionDataLocalImpl() {
  }

  @Override
  public boolean isEventBased() {
    return isEventBased;
  }

  @Override
  public void setEventBased(boolean isEventBased) {
    this.isEventBased = isEventBased;
  }

  @Override
  public boolean isRequestTypeSet() {
    return requestTypeSet;
  }

  @Override
  public void setRequestTypeSet(boolean requestTypeSet) {
    this.requestTypeSet = requestTypeSet;
  }

  @Override
  public ClientRoSessionState getClientRoSessionState() {
    return state;
  }

  @Override
  public void setClientRoSessionState(ClientRoSessionState state) {
    this.state = state;
  }

  @Override
  public Serializable getTxTimerId() {
    return txTimerId;
  }

  @Override
  public void setTxTimerId(Serializable txTimerId) {
    this.txTimerId = txTimerId;
  }

  @Override
  public Request getTxTimerRequest() {
    return txTimerRequest;
  }

  @Override
  public void setTxTimerRequest(Request txTimerRequest) {
    this.txTimerRequest = txTimerRequest;
  }

  @Override
  public Request getBuffer() {
    return buffer;
  }

  @Override
  public void setBuffer(Request buffer) {
    this.buffer = buffer;
  }

  @Override
  public int getGatheredRequestedAction() {
    return gatheredRequestedAction;
  }

  @Override
  public void setGatheredRequestedAction(int gatheredRequestedAction) {
    this.gatheredRequestedAction = gatheredRequestedAction;
  }

  @Override
  public int getGatheredCCFH() {
    return gatheredCCFH;
  }

  @Override
  public void setGatheredCCFH(int gatheredCCFH) {
    this.gatheredCCFH = gatheredCCFH;
  }

  @Override
  public int getGatheredDDFH() {
    return gatheredDDFH;
  }

  @Override
  public void setGatheredDDFH(int gatheredDDFH) {
    this.gatheredDDFH = gatheredDDFH;
  }

}
