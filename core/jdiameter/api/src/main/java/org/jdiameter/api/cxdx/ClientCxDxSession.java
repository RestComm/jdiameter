package org.jdiameter.api.cxdx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;

/**
 * Start time:13:23:06 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ClientCxDxSession extends AppSession, StateMachine {

  void sendUserAuthorizationRequest(JUserAuthorizationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendServerAssignmentRequest(JServerAssignmentRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendLocationInformationRequest(JLocationInfoRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendMultimediaAuthRequest(JMultimediaAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendRegistrationTerminationAnswer(JRegistrationTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendPushProfileAnswer(JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
