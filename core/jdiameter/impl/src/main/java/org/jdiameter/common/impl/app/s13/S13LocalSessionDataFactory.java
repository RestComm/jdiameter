package org.jdiameter.common.impl.app.s13;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.client.impl.app.s13.ClientS13SessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.server.impl.app.s13.ServerS13SessionDataLocalImpl;

public class S13LocalSessionDataFactory implements IAppSessionDataFactory<IS13SessionData> {

	public IS13SessionData getAppSessionData(Class<? extends AppSession> clazz,String sessionId) {
		if (clazz.equals(ClientS13Session.class)) {
			ClientS13SessionDataLocalImpl data = new ClientS13SessionDataLocalImpl();
			data.setSessionId(sessionId);
			return data;
		} else if (clazz.equals(ServerS13Session.class)) {
			ServerS13SessionDataLocalImpl data = new ServerS13SessionDataLocalImpl();
			data.setSessionId(sessionId);
			return data;
		} else {
			throw new IllegalArgumentException("Invalid Session Class: " + clazz.toString());
		}
	}
}
