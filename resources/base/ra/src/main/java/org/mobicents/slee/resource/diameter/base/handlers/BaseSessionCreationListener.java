package org.mobicents.slee.resource.diameter.base.handlers;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;

/**
 * This should be implemented by RA. It defines
 * some static values equal to event name part from event definition. This
 * should be passed as arg to
 * {@link #fireEvent(String sessionId, String name, Request request, Answer answer)}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface BaseSessionCreationListener {

	// Some static to improve perf, we have maps with it, but thats only for
	// setup.

	public static final String _AbortSessionRequest = "net.java.slee.resource.diameter.base.events.AbortSessionRequest";
	public static final String _AccountingRequest = "net.java.slee.resource.diameter.base.events.AccountingRequest";
	public static final String _CapabilitiesExchangeRequest = "net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest";
	public static final String _DeviceWatchdogRequest = "net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest";
	public static final String _DisconnectPeerRequest = "net.java.slee.resource.diameter.base.events.DisconnectPeerRequest";
	public static final String _ReAuthRequest = "net.java.slee.resource.diameter.base.events.ReAuthRequest";
	public static final String _SessionTerminationRequest = "net.java.slee.resource.diameter.base.events.SessionTerminationRequest";

	public static final String _AbortSessionAnswer = "net.java.slee.resource.diameter.base.events.AbortSessionAnswer";
	public static final String _AccountingAnswer = "net.java.slee.resource.diameter.base.events.AccountingAnswer";
	public static final String _CapabilitiesExchangeAnswer = "net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer";
	public static final String _DeviceWatchdogAnswer = "net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer";
	public static final String _DisconnectPeerAnswer = "net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer";
	public static final String _ReAuthAnswer = "net.java.slee.resource.diameter.base.events.ReAuthAnswer";
	public static final String _SessionTerminationAnswer = "net.java.slee.resource.diameter.base.events.SessionTerminationAnswer";

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
	public void sessionCreated(ServerAccSession session);

	/**
	 * 
	 * @param session
	 */
	public void sessionCreated(ServerAuthSession session);

	/**
	 * 
	 * @param session
	 */
	public void sessionCreated(ClientAuthSession session);

	/**
	 * 
	 * @param session
	 */
	public void sessionCreated(ClientAccSession session);

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
