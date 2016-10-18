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

package org.jdiameter.common.impl.app.sy;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Request;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.sy.ClientSySession;
import org.jdiameter.api.sy.ClientSySessionListener;
import org.jdiameter.api.sy.ServerSySession;
import org.jdiameter.api.sy.ServerSySessionListener;
import org.jdiameter.api.sy.events.SpendingLimitAnswer;
import org.jdiameter.api.sy.events.SpendingLimitRequest;
import org.jdiameter.api.sy.events.SpendingStatusNotificationAnswer;
import org.jdiameter.api.sy.events.SpendingStatusNotificationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.sy.ISyMessageFactory;
import org.jdiameter.common.api.app.sy.ISySessionFactory;

/**
 * Policy and charging control - Sy session implementation
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public class SySessionFactoryImpl implements ISySessionFactory, ClientSySessionListener, ServerSySessionListener, StateChangeListener<AppSession>,
    ISyMessageFactory {

  public SySessionFactoryImpl(ISessionFactory sessionFactory) {

  }

  @Override
  public SpendingLimitRequest createSpendingLimitRequest(Request request) {
    return null;
  }

  @Override
  public SessionTermRequest createSessionTerminationRequest(Request request) {
    return null;
  }

  @Override
  public SpendingStatusNotificationRequest createSpendingStatusNotificationRequest(Request request) {
    return null;
  }

  @Override
  public void doSpendingLimitRequest(ServerSySession session, SpendingLimitRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doFinalSpendingLimitRequest(ServerSySession session, SessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doSpendingStatusNotificationAnswer(ServerSySession session, SpendingStatusNotificationRequest request, SpendingStatusNotificationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doSpendingLimitAnswer(ClientSySession session, SpendingLimitRequest request, SpendingLimitAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doFinalSpendingLimitAnswer(ClientSySession session, SessionTermRequest request, SessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doSpendingStatusNotificationRequest(ClientSySession session, SpendingStatusNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    return null;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    return null;
  }

  @Override
  public void stateChanged(Enum oldState, Enum newState) {

  }

  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {

  }
}
