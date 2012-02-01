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

package org.jdiameter.api.rx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;
import org.jdiameter.api.rx.events.RxReAuthAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;

/**
 * Basic class for Rx Client Interface specific session.
 * Listener must be injected from constructor of implementation class.
 *
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ClientRxSession extends AppSession, StateMachine {

  public void sendAARequest(final RxAARequest request) throws InternalException, IllegalDiameterStateException, RouteException,
  OverloadException;

  public void sendSessionTermRequest(final RxSessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException,
  OverloadException;

  public void sendReAuthAnswer(final RxReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  public void sendAbortSessionAnswer(final RxAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
