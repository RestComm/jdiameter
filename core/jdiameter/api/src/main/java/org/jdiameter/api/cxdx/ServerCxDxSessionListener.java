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
 * Start time:14:17:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ServerCxDxSessionListener {

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

  void doUserAuthorizationRequest(ServerCxDxSession session,JUserAuthorizationRequest request,JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doServerAssignmentRequest(ServerCxDxSession session,JServerAssignmentRequest request,JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doRegistrationTerminationAnswer(ServerCxDxSession session,JRegistrationTerminationRequest request,JRegistrationTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doLocationInformationRequest(ServerCxDxSession session,JLocationInfoRequest request,JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doPushProfileAnswer(ServerCxDxSession session,JPushProfileRequest request,JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doMultimediaAuthRequest(ServerCxDxSession session,JMultimediaAuthRequest request,JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
