package org.mobicents.slee.resource.diameter.cca.handlers;

import javax.slee.resource.ActivityHandle;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;

/**
 * 
 * CCASessionCreationListener.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 11:18:42 AM Dec 30, 2008 <br>
 * This should be implemented by RA. It defines some static values equal to
 * event name part from event definition. This should be passed as arg to
 * {@link #fireEvent(String sessionId, String name, Request request, Answer answer)}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface CCASessionCreationListener {

	public static final String _AbortSessionRequest = "net.java.slee.resource.diameter.base.events.AbortSessionRequest";
	public static final String _AccountingRequest = "net.java.slee.resource.diameter.base.events.AccountingRequest";
	public static final String _CapabilitiesExchangeRequest = "net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest";
	public static final String _DeviceWatchdogRequest = "net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest";
	public static final String _DisconnectPeerRequest = "net.java.slee.resource.diameter.base.events.DisconnectPeerRequest";
	public static final String _ReAuthRequest = "net.java.slee.resource.diameter.base.events.ReAuthRequest";
	public static final String _SessionTerminationRequest = "net.java.slee.resource.diameter.base.events.SessionTerminationRequest";
	public static final String _CreditControlRequest = "net.java.slee.resource.diameter.cca.events.CreditControlRequest";

	public static final String _AbortSessionAnswer = "net.java.slee.resource.diameter.base.events.AbortSessionAnswer";
	public static final String _AccountingAnswer = "net.java.slee.resource.diameter.base.events.AccountingAnswer";
	public static final String _CapabilitiesExchangeAnswer = "net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer";
	public static final String _DeviceWatchdogAnswer = "net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer";
	public static final String _DisconnectPeerAnswer = "net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer";
	public static final String _ReAuthAnswer = "net.java.slee.resource.diameter.base.events.ReAuthAnswer";
	public static final String _SessionTerminationAnswer = "net.java.slee.resource.diameter.base.events.SessionTerminationAnswer";
	public static final String _CreditControlAnswer = "net.java.slee.resource.diameter.cca.events.CreditControlAnswer";

	public static final String _ExtensionDiameterMessage = "net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage";
	public static final String _ErrorAnswer = "net.java.slee.resource.diameter.base.events.ErrorAnswer";

	/**
	 * Listener for Client Session creation.
	 * 
	 * @param ccClientSession
	 *            the newly created Client Session
	 */
	public void sessionCreated(ClientCCASession ccClientSession);

	/**
	 * Listener for Server Session creation.
	 * 
	 * @param ccServerSession
	 *            the newly created Client Session
	 */
	public void sessionCreated(ServerCCASession ccServerSession);

	/**
	 * Method for verifying if some session (with a given Session-Id) exists.
	 * 
	 * @param sessionId
	 *            the Id to verify
	 * @return false if there's no session with the id, true otherwise
	 */
	public boolean sessionExists(String sessionId);

	/**
	 * Listener for Server Session destruction.
	 * 
	 * @param sessionId
	 *            the id of the session destroyed
	 * @param appSession
	 *            the session object itself
	 */
	public void sessionDestroyed(String sessionId, Object appSession);

	/**
	 * Helper method to fire events to SLEE.
	 * 
	 * @param sessionId
	 *            the id of the session for this event
	 * @param name
	 *            the event name (without suffix Request/Answer)
	 * @param request
	 *            the request object (if it is a request)
	 * @param answer
	 *            the answer object (if it is a answer)
	 */
	public void fireEvent(String sessionId, String name, Request request, Answer answer);

	/**
	 * Helper method to fire events to SLEE.
	 * 
	 * @param handle
	 *            the activity handle where to fire it
	 * @param name
	 *            the event name (without suffix Request/Answer)
	 * @param request
	 *            the request object (if it is a request)
	 * @param answer
	 *            the answer object (if it is a answer)
	 */
	public void fireEvent(ActivityHandle handle, String name, Request request, Answer answer);

}
