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
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;

/**
 * Basic class for authentication application specific session
 * Listener must injection from constructor of implementation class
 * @version 1.5.1 Final
 */

public interface ClientAuthSession extends AppSession, StateMachine {

  /**
   * Send authentication session request to server
   * @param request Authentication-Request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendAuthRequest(AppRequestEvent request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  /**
   * Send re-authentication session answer to server
   * @param answer Re-Authentication-Request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendReAuthAnswer(ReAuthAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  /**
   * Send abort session answer to server
   * @param answer Abort-Session-Answer event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded
   */
  void sendAbortSessionAnswer(AbortSessionAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Send session termination request to server
   * @param request Session-Term-Request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   **/
  void sendSessionTerminationRequest(SessionTermRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}