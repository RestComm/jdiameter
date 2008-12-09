package net.java.slee.resource.diameter.cca.handlers;

import javax.slee.resource.ActivityHandle;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;

import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;

public interface CCASessionCreationListener {

	
	public void sessionCreated(ClientCCASession ccClientSession);
	public void sessionCreated(ServerCCASession ccServerSession);
	public boolean sessionExists(String sessionId);
	public void sessionDestroyed(String sessionId, Object appSession);
	public void fireEvent(String sessionId, String name, Request request, Answer answer);
	public void fireEvent(ActivityHandle handle, String name, Request request, Answer answer);
	
}
