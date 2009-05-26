package org.mobicents.slee.resource.diameter.base;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.AuthSessionActivity;
import net.java.slee.resource.diameter.base.AuthSessionState;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;

public abstract class AuthSessionActivityImpl extends DiameterActivityImpl
		implements AuthSessionActivity , StateChangeListener{

	

	public AuthSessionActivityImpl(DiameterMessageFactoryImpl messageFactory,
			DiameterAvpFactoryImpl avpFactory, Session session,
			EventListener<Request, Answer> raEventListener, long timeout,
			DiameterIdentity destinationHost,
			DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, session, raEventListener, timeout,
				destinationHost, destinationRealm, endpoint);
		// TODO Auto-generated constructor stub
	}

	protected AuthSessionState state=null;
	
	public AuthSessionState getSessionState() {

		return state;
	}

	

}
