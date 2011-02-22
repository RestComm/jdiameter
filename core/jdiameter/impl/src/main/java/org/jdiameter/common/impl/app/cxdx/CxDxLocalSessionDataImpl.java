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
package org.jdiameter.common.impl.app.cxdx;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxSessionData;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CxDxLocalSessionDataImpl extends AppSessionDataLocalImpl implements ICxDxSessionData{

  protected CxDxSessionState state = CxDxSessionState.IDLE;
  protected Request buffer;
  protected Serializable tsTimerId;

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxSessionData#setCxDxSessionState(org.jdiameter.common.api.app.cxdx.CxDxSessionState)
   */
  @Override
  public void setCxDxSessionState(CxDxSessionState state) {
    this.state = state;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxSessionData#getCxDxSessionState()
   */
  @Override
  public CxDxSessionState getCxDxSessionState() {
    return this.state;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxSessionData#getTsTimerId()
   */
  @Override
  public Serializable getTsTimerId() {
    return this.tsTimerId;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxSessionData#setTsTimerId(java.io.Serializable)
   */
  @Override
  public void setTsTimerId(Serializable tid) {
    this.tsTimerId = tid;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxSessionData#setBuffer(org.jdiameter.api.Message)
   */
  @Override
  public void setBuffer(Request buffer) {
    this.buffer = buffer;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxSessionData#getBuffer()
   */
  @Override
  public Request getBuffer() {
    return this.buffer;
  }
}
