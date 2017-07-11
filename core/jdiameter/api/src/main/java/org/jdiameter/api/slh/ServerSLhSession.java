/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.jdiameter.api.slh;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public interface ServerSLhSession extends AppSession, StateMachine {

  /**
    * Send LCS-Routing-Info-Answer to client
    *
    * @param answer LCS-Routing-Info-Answer event instance
    * @throws InternalException The InternalException signals that internal error is occurred.
    * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
    * @throws RouteException The NoRouteException signals that no route exist for a given realm.
    * @throws OverloadException The OverloadException signals that destination host is overloaded.
    */
  void sendLCSRoutingInfoAnswer(LCSRoutingInfoAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}