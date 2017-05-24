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

package org.jdiameter.api.sy;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;

import org.jdiameter.api.auth.events.SessionTermAnswer;

import org.jdiameter.api.sy.events.SpendingLimitAnswer;
import org.jdiameter.api.sy.events.SpendingStatusNotificationRequest;

/**
 * Basic class for Sy Server Interface specific session.
 * Listener must be injected from constructor of implementation class
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public interface ServerSySession extends AppSession, StateMachine {

  void sendSpendingLimitAnswer(SpendingLimitAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendSessionTerminationAnswer(SessionTermAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendSpendingStatusNotificationRequest(SpendingStatusNotificationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
