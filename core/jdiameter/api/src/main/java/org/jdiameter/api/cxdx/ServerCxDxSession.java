/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and individual contributors
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

package org.jdiameter.api.cxdx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ServerCxDxSession extends AppSession, StateMachine {

  void sendUserAuthorizationAnswer(JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendServerAssignmentAnswer(JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendLocationInformationAnswer(JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendMultimediaAuthAnswer(JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendRegistrationTerminationRequest(JRegistrationTerminationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendPushProfileRequest(JPushProfileRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
