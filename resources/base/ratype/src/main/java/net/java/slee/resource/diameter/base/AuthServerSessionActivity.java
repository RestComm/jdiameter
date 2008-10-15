package net.java.slee.resource.diameter.base;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;

public interface AuthServerSessionActivity extends AuthSessionActivity {

	/**
	 * Send session abort session request to client
	 * 
	 * @param request
	 * @throws IOException 
	 */
	void sendAbortSessionRequest(AbortSessionRequest request) throws IOException;

	/**
	 * Send authenticate answer to client
	 * 
	 * @param answer
	 * @throws IOException 
	 */
	void sendAuthAnswer(DiameterMessage answer) throws IOException;

	/**
	 * Send re-authenticate request to client
	 * 
	 * @param request
	 * @throws IOException 
	 */
	void sendReAuthRequest(ReAuthRequest request) throws IOException;

	/**
	 * Send session termination answer to client
	 * 
	 * @param request
	 * @throws IOException 
	 */
	void sendSessionTerminationAnswer(SessionTerminationAnswer request) throws IOException;

}
