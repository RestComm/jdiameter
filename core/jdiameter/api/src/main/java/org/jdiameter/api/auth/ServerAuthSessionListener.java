/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.auth;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermRequest;

/**
 * This interface defines the possible actions that the different states in the
 * Authentication state machine
 * @version 1.5.1 Final
 */

public interface ServerAuthSessionListener {

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived AuthRequest message.
   * @param session parent application session (FSM)
   * @param request authentication request object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doAuthRequestEvent(ServerAuthSession session, AppRequestEvent request)
  throws InternalException, org.jdiameter.api.IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived ReAuthAnswer message.
   * @param  session parent application session (FSM)
   * @param  request re-authentication request object
   * @param  answer re-authentication answer object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doReAuthAnswerEvent(ServerAuthSession session,  ReAuthRequest request, ReAuthAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived AbortSessionRequest message.
   * @param session parent application session (FSM)
   * @param answer abort session event event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doAbortSessionAnswerEvent(ServerAuthSession session, AbortSessionAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived SessionTerminationRequest message.
   * @param session parent application session (FSM)
   * @param request session termination request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */    
  void doSessionTerminationRequestEvent(ServerAuthSession session, SessionTermRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived not authentication message.
   * @param session parent application session (FSM)
   * @param request request object
   * @param answer answer object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
