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

import org.jdiameter.common.api.app.acc.IAccSessionData;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IServerAccSessionData extends IAccSessionData {

  public void setServerAccSessionState(ServerAccSessionState value);
  public ServerAccSessionState getServerAccSessionState();

  public void setStateless(boolean value);
  public boolean isStateless();

  /**
   * Seconds value, its taken from either request or answer. Contained in Acct-Interim-Interval AVP
   * @param value
   */
  public void setTsTimeout(long value);
  public long getTsTimeout();

  public void setTsTimerId(Serializable value);
  public Serializable getTsTimerId();

}
