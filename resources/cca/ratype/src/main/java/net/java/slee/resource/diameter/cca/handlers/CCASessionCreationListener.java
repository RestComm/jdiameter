package net.java.slee.resource.diameter.cca.handlers;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;

import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;

public interface CCASessionCreationListener {

	
	public void sessionCreated(CreditControlClientSession ccClientSession);
	public void sessionCreated(CreditControlServerSession ccServerSession);
	public boolean sessionExists(String sessionId);
	public void fireEvent(String sessionId, String name, Request request, Answer answer);
	
	
}
