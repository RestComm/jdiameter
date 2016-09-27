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

package org.jdiameter.server.impl.sy;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Session;
import org.jdiameter.api.Request;
import org.jdiameter.api.Answer;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.sy.ServerSySession;
import org.jdiameter.api.sy.events.SpendingLimitAnswer;
import org.jdiameter.api.sy.events.SpendingStatusNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Policy Control Application Server session implementation
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public class ServerSySessionImpl implements ServerSySession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ServerSySessionImpl.class);

  @Override
  public Answer processRequest(Request request) {
    return null;
  }

  @Override
  public void sendSpendingLimitAnswer(SpendingLimitAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void sendFinalSpendingLimitAnswer(SessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void sendSpendingStatusNotificationRequest(SpendingStatusNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {

  }

  @Override
  public void timeoutExpired(Request request) {

  }

  @Override
  public boolean isStateless() {
    return false;
  }

  @Override
  public ApplicationId getSessionAppId() {
    return null;
  }

  @Override
  public List<Session> getSessions() {
    return null;
  }

  @Override
  public void addStateChangeNotification(StateChangeListener listener) {

  }

  @Override
  public void removeStateChangeNotification(StateChangeListener listener) {

  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return false;
  }

  @Override
  public <E> E getState(Class<E> stateType) {
    return null;
  }

  @Override
  public long getCreationTime() {
    return 0;
  }

  @Override
  public long getLastAccessedTime() {
    return 0;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public void release() {

  }

  @Override
  public boolean isAppSession() {
    return false;
  }

  @Override
  public boolean isReplicable() {
    return false;
  }

  @Override
  public String getSessionId() {
    return null;
  }
}
