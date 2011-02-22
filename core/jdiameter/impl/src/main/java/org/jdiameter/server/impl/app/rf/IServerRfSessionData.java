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
package org.jdiameter.server.impl.app.rf;

import java.io.Serializable;

import org.jdiameter.common.api.app.rf.IRfSessionData;
import org.jdiameter.common.api.app.rf.ServerRfSessionState;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IServerRfSessionData extends IRfSessionData{

  public ServerRfSessionState getServerRfSessionState();
  public void setServerRfSessionState(ServerRfSessionState state);

  public void setTsTimerId(Serializable tsTimerId);
  public Serializable getTsTimerId();

  public long getTsTimeout();
  public void setTsTimeout(long tsTimeout);

  public boolean isStateless();
  public void setStateless(boolean stateless);

}
