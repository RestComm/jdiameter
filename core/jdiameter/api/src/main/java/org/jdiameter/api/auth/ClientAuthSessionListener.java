/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
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
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;

/**
 * This interface defines the possible actions that the different states in the
 * Authentication state machine
 * @version 1.5.1 Final
 */

public interface ClientAuthSessionListener {

  /**
   * Notifies this AuthSessionEventListener that the ClientAuthSesssion has recived AuthAnswer message.
   * @param session parent application session (FSM)
   * @param request authentication request object
   * @param answer authentication answer object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doAuthAnswerEvent(ClientAuthSession session, AppRequestEvent request, AppAnswerEvent answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ClientAuthSesssion has recived ReAuthRequest message.
   * @param session parent application session (FSM)
   * @param request re-authentication request object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doReAuthRequestEvent(ClientAuthSession session, ReAuthRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ClientAuthSesssion has recived AbortSessionRequest message.
   * @param session parent application session (FSM)
   * @param request abort session request object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doAbortSessionRequestEvent(ClientAuthSession session, AbortSessionRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ClientAuthSesssion has recived SessionTerminationAnswer message.
   * @param session parent application session (FSM)
   * @param answer abort session request object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doSessionTerminationAnswerEvent(ClientAuthSession session, SessionTermAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ClientAuthSesssion has recived not authentication message.
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
