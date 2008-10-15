package org.mobicents.slee.resource.diameter.base;

import javax.slee.resource.SleeEndpoint;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;
import org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener;

import net.java.slee.resource.diameter.base.AccountingSessionActivity;
import net.java.slee.resource.diameter.base.AccountingSessionState;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public abstract class AccountingSessionActivityImpl extends DiameterActivityImpl implements AccountingSessionActivity, StateChangeListener{


	public AccountingSessionActivityImpl(DiameterMessageFactoryImpl messageFactory, DiameterAvpFactoryImpl avpFactory, Session session,
			EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm, 
			SleeEndpoint endpoint)
	{
		
		super(messageFactory, avpFactory, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);
	}

	protected AccountingSessionState state = null;

	public AccountingSessionState getAccountingSessionState()
	{
		return this.state;
	}

	

}
