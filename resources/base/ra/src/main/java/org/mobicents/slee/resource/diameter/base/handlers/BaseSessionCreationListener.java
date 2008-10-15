package org.mobicents.slee.resource.diameter.base.handlers;




import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;

public interface BaseSessionCreationListener {

	
	public void sessionDestroyed(String sessionId, Object appSession);
	
	public void sessionCreated(ServerAccSession session);
	public void sessionCreated(ServerAuthSession session);
	public void sessionCreated(ClientAuthSession session);
	public void sessionCreated(ClientAccSession session);
	public void sessionCreated(Session session);
	public void fireEvent(String sessionId, String name, Request request, Answer answer);
	public boolean sessionExists(String sessionId);
	
}
