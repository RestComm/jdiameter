package org.mobicents.slee.resource.diameter.base;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.AccountingSessionActivity;
import net.java.slee.resource.diameter.base.AccountingSessionState;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;

/**
 * 
 * AccountingSessionActivityImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class AccountingSessionActivityImpl extends DiameterActivityImpl implements AccountingSessionActivity, StateChangeListener {

	public AccountingSessionActivityImpl(DiameterMessageFactoryImpl messageFactory, DiameterAvpFactoryImpl avpFactory, Session session,
			EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, 
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
