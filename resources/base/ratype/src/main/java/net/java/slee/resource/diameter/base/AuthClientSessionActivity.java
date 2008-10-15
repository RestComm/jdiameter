package net.java.slee.resource.diameter.base;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;

public interface AuthClientSessionActivity extends AuthSessionActivity{

	/**
	 * Send abort session answer to server
	 * 
	 * @param answer
	 * @throws IOException 
	 */
	void sendAbortSessionAnswer(AbortSessionAnswer answer) throws IOException;

	/**
	 * Send authentication session request to server FIXME: baranowb; whats
	 * this?
	 * 
	 * @param request
	 * @throws IOException 
	 */
	void sendAuthRequest(DiameterMessage request) throws IOException;

	/**
	 * Send re-authentication session answer to server
	 * 
	 * @param answer
	 * @throws IOException 
	 */
	void sendReAuthAnswer(ReAuthAnswer answer) throws IOException;

	/**
	 * Send session termination request to server
	 * 
	 * @param request
	 * @throws IOException 
	 */
	void sendSessionTerminationRequest(SessionTerminationRequest request) throws IOException;
	
}
