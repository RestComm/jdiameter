/**
 * Start time:11:17:31 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.slee.resource.diameter.cxdx.handlers;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSession;

/**
 * Start time:11:17:31 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface CxDxSessionCreationListener {
	// Some static to improve perf, we have maps with it, but thats only for
	// setup.

	public static final String _UserAuthorizationRequest = "net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest";
	public static final String _ServerAssignmentRequest = "net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest";
	public static final String _LocationInfoRequest = "net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest";
	public static final String _MultimediaAuthenticationRequest = "net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest";
	public static final String _RegistrationTerminationRequest = "net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest";
	public static final String _PushProfileRequest = "net.java.slee.resource.diameter.cxdx.events.PushProfileRequest";
	

	public static final String _UserAuthorizationAnswer = "net.java.slee.resource.diameter.cxdx.events.UserAuthorizationAnswer";
	public static final String _ServerAssignmentAnswer = "net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer";
	public static final String _LocationInfoAnswer = "net.java.slee.resource.diameter.cxdx.events.LocationInfoAnswer";
	public static final String _MultimediaAuthenticationAnswer = "net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer";
	public static final String _RegistrationTerminationAnswer = "net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer";
	public static final String _PushProfileAnswer = "net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer";
	

	public static final String _ExtensionDiameterMessage = "net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage";
	public static final String _ErrorAnswer = "net.java.slee.resource.diameter.base.events.ErrorAnswer";
	/**
	 * 
	 * @param sessionId
	 * @param appSession
	 */
	public void sessionDestroyed(String sessionId, Object appSession);

	/**
	 * 
	 * @param session
	 */
	public void sessionCreated(ServerCxDxSession session);

	/**
	 * 
	 * @param session
	 */
	public void sessionCreated(ClientCxDxSession session);

	/**
	 * 
	 * @param session
	 */
	public void sessionCreated(Session session);

	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public boolean sessionExists(String sessionId);

	/**
	 * Makes RA fire event with certain name.
	 * 
	 * @param sessionId
	 * @param name
	 * @param request
	 * @param answer
	 */
	public void fireEvent(String sessionId, String name, Request request, Answer answer);

	/**
	 * 
	 * @return
	 */
	public ApplicationId[] getSupportedApplications();
}
