package org.jdiameter.api.cxdx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;

/**
 * Start time:13:39:01 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ClientCxDxSessionListener {

  /**
   * Notifies this ClientCxDxSessionListener that the ClientCxDxSession has recived not CxDx message, usually some extension.
   * @param session parent application session (FSM)
   * @param request request object
   * @param answer answer object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doUserAuthorizationAnswer(ClientCxDxSession session,JUserAuthorizationRequest request,JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doServerAssignmentAnswer(ClientCxDxSession session,JServerAssignmentRequest request,JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doRegistrationTerminationRequest(ClientCxDxSession session,JRegistrationTerminationRequest request,JRegistrationTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doLocationInformationAnswer(ClientCxDxSession session,JLocationInfoRequest request,JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doPushProfileRequest(ClientCxDxSession session,JPushProfileRequest request,JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doMultimediaAuthAnswer(ClientCxDxSession session,JMultimediaAuthRequest request,JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
