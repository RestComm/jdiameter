package org.jdiameter.api.ro;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.ro.events.RoCreditControlAnswer;
import org.jdiameter.api.ro.events.RoCreditControlRequest;

/**
 * This interface defines the possible actions for the different states in the client 
 * Credit-Control Application state machine.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ClientRoSessionListener {

  /**
   * Notifies this ClientRoSessionListener that the ClientRoSession has received a Ro message.
   * 
   * @param session parent application session (FSM)
   * @param request request object
   * @param answer answer object
   * @throws InternalException The InternalException signals that internal error has occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doCreditControlAnswer(ClientRoSession session, RoCreditControlRequest request, RoCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this ClientRoSessionListener that the ClientRoSession has received a RAR message.
   * 
   * @param session parent application session (FSM)
   * @param request request object
   * @throws InternalException The InternalException signals that internal error has occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doReAuthRequest(ClientRoSession session, ReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this ClientRoSessionListener that the ClientRoSession has received a non Ro message, usually some extension.
   * 
   * @param session parent application session (FSM)
   * @param request request object
   * @param answer answer object
   * @throws InternalException The InternalException signals that internal error has occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Provides with default value of DDFH AVP - this is used when AVP is not present or send
   * operation fails for some reason.<br>
   * DDFH is of type Enumerated - int32
   * 
   * @return
   */
  int getDefaultDDFHValue();

  /**
   * Provides with default value of CCFH AVP - this is used when AVP is not present or send
   * operation fails for some reason.<br>
   * CCFH is of type Enumerated - int32
   * 
   * @return
   */
  int getDefaultCCFHValue();

}
