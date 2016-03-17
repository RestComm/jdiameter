/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.jdiameter.client.impl.app.cca;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.cca.ClientCCASessionState;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientCCASessionDataLocalImpl extends AppSessionDataLocalImpl implements IClientCCASessionData {

  protected boolean isEventBased = true;
  protected boolean requestTypeSet = false;
  protected ClientCCASessionState state = ClientCCASessionState.IDLE;
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
  public ClientCCASessionDataLocalImpl() {
  }

  public boolean isEventBased() {
    return isEventBased;
  }

  public void setEventBased(boolean isEventBased) {
    this.isEventBased = isEventBased;
  }

  public boolean isRequestTypeSet() {
    return requestTypeSet;
  }

  public void setRequestTypeSet(boolean requestTypeSet) {
    this.requestTypeSet = requestTypeSet;
  }

  public ClientCCASessionState getClientCCASessionState() {
    return state;
  }

  public void setClientCCASessionState(ClientCCASessionState state) {
    this.state = state;
  }

  public Serializable getTxTimerId() {
    return txTimerId;
  }

  public void setTxTimerId(Serializable txTimerId) {
    this.txTimerId = txTimerId;
  }

  public Request getTxTimerRequest() {
    return txTimerRequest;
  }

  public void setTxTimerRequest(Request txTimerRequest) {
    this.txTimerRequest = txTimerRequest;
  }

  public Request getBuffer() {
    return buffer;
  }

  public void setBuffer(Request buffer) {
    this.buffer = buffer;
  }

  public int getGatheredRequestedAction() {
    return gatheredRequestedAction;
  }

  public void setGatheredRequestedAction(int gatheredRequestedAction) {
    this.gatheredRequestedAction = gatheredRequestedAction;
  }

  public int getGatheredCCFH() {
    return gatheredCCFH;
  }

  public void setGatheredCCFH(int gatheredCCFH) {
    this.gatheredCCFH = gatheredCCFH;
  }

  public int getGatheredDDFH() {
    return gatheredDDFH;
  }

  public void setGatheredDDFH(int gatheredDDFH) {
    this.gatheredDDFH = gatheredDDFH;
  }

}
