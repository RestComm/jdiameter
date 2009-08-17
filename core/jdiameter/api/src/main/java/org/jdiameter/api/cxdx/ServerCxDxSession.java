/**
 * Start time:13:23:41 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.api.cxdx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;

/**
 * Start time:13:23:41 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ServerCxDxSession extends AppSession, StateMachine {

	void sendRegistrationTerminationRequest(JRegistrationTerminationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

	void sendPushProfileRequest(JPushProfileRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
