package org.mobicents.slee.resource.diameter.sh.client.handlers;

import org.jdiameter.api.sh.ClientShSession;

public interface ShClientSessionListener {

	
	public void sessionDestroyed(String sessionId,ClientShSession session);
}
