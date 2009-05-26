package org.mobicents.slee.resource.diameter.base;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.client.impl.app.auth.ClientAuthSessionImpl;
import org.jdiameter.common.api.app.auth.ClientAuthSessionState;

import net.java.slee.resource.diameter.base.AccountingSessionState;
import net.java.slee.resource.diameter.base.AuthClientSessionActivity;
import net.java.slee.resource.diameter.base.AuthSessionState;
import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

public class AuthClientSessionActivityImpl extends AuthSessionActivityImpl
		implements AuthClientSessionActivity {

	
	protected ClientAuthSession clientSession=null;
	
	public AuthClientSessionActivityImpl(
			DiameterMessageFactoryImpl messageFactory,
			DiameterAvpFactoryImpl avpFactory, ClientAuthSession clientSession,
			 long timeout,
			DiameterIdentity destinationHost,
			DiameterIdentity destinationRealm,SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null, (EventListener<Request, Answer>) clientSession, timeout,
				destinationHost, destinationRealm,endpoint);
		
		this.clientSession=clientSession;
		//this.clientSession.addStateChangeNotification(this);
		super.setCurrentWorkingSession(clientSession.getSessions().get(0));
		
	}

	public void sendAbortSessionAnswer(AbortSessionAnswer answer) throws IOException {
		super.sendMessage(answer);

	}

	public void sendAuthRequest(DiameterMessage request) throws IOException {
		super.sendMessage(request);

	}

	public void sendReAuthAnswer(ReAuthAnswer answer) throws IOException {
		super.sendMessage(answer);

	}

	public void sendSessionTerminationRequest(SessionTerminationRequest request) throws IOException {
		super.sendMessage(request);

	}

	public void stateChanged(Enum oldState, Enum newState) {
		
		ClientAuthSessionState state=(ClientAuthSessionState) newState;
		switch(state)
		{
		case IDLE:
			super.state=AuthSessionState.Idle;
			break;
		case OPEN:
			super.state=AuthSessionState.Open;
			break;
		case  PENDING:
			super.state=AuthSessionState.Pending;
			break;
		case DISCONNECTED:
			super.state=AuthSessionState.Disconnected;
			String sessionId = this.clientSession.getSessions().get(0)
			.getSessionId();

			this.clientSession.release();
			this.baseListener.sessionDestroyed(sessionId, this.clientSession);
			break;
		}

	}

	public ClientAuthSession getSession() {
		return this.clientSession;
	}

}
