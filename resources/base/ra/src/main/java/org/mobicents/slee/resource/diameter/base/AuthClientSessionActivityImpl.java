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
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.SessionTermRequestImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

import net.java.slee.resource.diameter.base.AccountingSessionState;
import net.java.slee.resource.diameter.base.AuthClientSessionActivity;
import net.java.slee.resource.diameter.base.AuthSessionState;
import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
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

		try {
			// super.sendMessage(answer);
			DiameterMessageImpl asa = (DiameterMessageImpl) answer;
			this.clientSession.sendAbortSessionAnswer(new AbortSessionAnswerImpl((Answer) asa.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public void sendAuthRequest(DiameterMessage request) throws IOException {
		if(!request.getCommand().isRequest())
		{
			throw new IOException("Message is not a request.");
		}
		try {
			this.clientSession.sendAuthRequest(new AppRequestEventImpl(((DiameterMessageImpl)request).getGenericData()));
			//super.sendMessage(request);
			
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public void sendReAuthAnswer(ReAuthAnswer answer) throws IOException {
		try {
			//super.sendMessage(answer);
			DiameterMessageImpl msg = (DiameterMessageImpl) answer;
			this.clientSession.sendReAuthAnswer(new ReAuthAnswerImpl(msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public void sendSessionTerminationRequest(SessionTerminationRequest request) throws IOException {
		try {
			//super.sendMessage(request);
			DiameterMessageImpl msg = (DiameterMessageImpl) request;
			this.clientSession.sendSessionTerminationRequest(new SessionTermRequestImpl(msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
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
