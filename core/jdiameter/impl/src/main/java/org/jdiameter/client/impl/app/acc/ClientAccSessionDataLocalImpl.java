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
