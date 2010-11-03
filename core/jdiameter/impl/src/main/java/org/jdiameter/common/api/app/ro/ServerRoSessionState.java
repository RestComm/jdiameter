/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.jdiameter.common.api.app.ro;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * Diameter Credit-Control Application Server states
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public enum ServerRoSessionState implements IAppSessionState<ServerRoSessionState> {

  IDLE(0),
  OPEN(1);

  private int stateRepresentation = -1;

  ServerRoSessionState(int v) {
    this.stateRepresentation=v;
  }

  public ServerRoSessionState fromInt(int v) throws IllegalArgumentException {
    switch(v)
    {
    case 0:
      return IDLE;

    case 1:
      return OPEN;

    default:
      throw new IllegalArgumentException("Illegal value of int representation!!!!");
    }
  }

  public int getValue() {
    return stateRepresentation;
  }

}
