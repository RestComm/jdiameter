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
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;

/**
 * Basic class for server authentication application specific session.
 * Listener must injection from constructor of implementation class.
 * @version 1.5.1 Final
 */

public interface ServerAuthSession extends AppSession, StateMachine {

  /**
   * Send authenticate answer to client
   * @param answer Authentication answer event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendAuthAnswer(AppAnswerEvent answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Send re-authenticate request to client
   * @param request Re-Authentication request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendReAuthRequest(ReAuthRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  /**
   * Send session abort session request to client
   * @param request Abort-Session-Request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendAbortSessionRequest(AbortSessionRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Send session termination answer to client
   * @param request Session-Term-Request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendSessionTerminationAnswer(SessionTermAnswer request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
