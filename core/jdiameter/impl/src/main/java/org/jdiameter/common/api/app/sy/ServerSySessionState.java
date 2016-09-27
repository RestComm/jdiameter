/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.common.api.app.sy;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * Policy and charging control - Sy session implementation
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

 public enum ServerSySessionState implements IAppSessionState<ServerSySessionState> {

   IDLE(0),
   OPEN(1);

   private int stateRepresentation = -1;

   ServerSySessionState(int v) {
     this.stateRepresentation = v;
   }

   @Override
   public ServerSySessionState fromInt(int v) throws IllegalArgumentException {
     switch (v) {
       case 0:
         return IDLE;

       case 1:
         return OPEN;

       default:
         throw new IllegalArgumentException("Illegal value of int representation!!!!");
     }
   }

   @Override
   public int getValue() {
     return stateRepresentation;
   }

 }
