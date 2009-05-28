package org.mobicents.slee.resource.diameter.base;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.common.api.app.auth.ServerAuthSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.auth.SessionTermAnswerImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

import net.java.slee.resource.diameter.base.AuthServerSessionActivity;
import net.java.slee.resource.diameter.base.AuthSessionState;
import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

public class AuthServerSessionActivityImpl extends AuthSessionActivityImpl
		implements AuthServerSessionActivity {

	protected ServerAuthSession serverSession=null;
	
	public AuthServerSessionActivityImpl(
			DiameterMessageFactoryImpl messageFactory,
			DiameterAvpFactoryImpl avpFactory, ServerAuthSession serverSession,
			 long timeout,
			DiameterIdentity destinationHost,
			DiameterIdentity destinationRealm,SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null, (EventListener<Request, Answer>) serverSession, timeout,
				destinationHost, destinationRealm,endpoint);

		this.serverSession=serverSession;
		super.setCurrentWorkingSession(this.serverSession.getSessions().get(0));
		//this.serverSession.addStateChangeNotification(this);
		
	}

	public void sendAbortSessionRequest(AbortSessionRequest request) throws IOException {
		try {
			
			//super.sendMessage(request);
			DiameterMessageImpl msg = (DiameterMessageImpl) request;
			this.serverSession.sendAbortSessionRequest(new AbortSessionRequestImpl(msg.getGenericData()));
			
			
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public void sendAuthAnswer(DiameterMessage answer) throws IOException {
		try {
			
			//super.sendMessage(answer);
			DiameterMessageImpl msg = (DiameterMessageImpl) answer;
			//FIXME: this needs to get right impl.??
			this.serverSession.sendAuthAnswer(new AppAnswerEventImpl(msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public void sendReAuthRequest(ReAuthRequest request) throws IOException {
		try {
			
			//super.sendMessage(request);
			DiameterMessageImpl msg = (DiameterMessageImpl) request;
			this.serverSession.sendReAuthRequest(new ReAuthRequestImpl(msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public void sendSessionTerminationAnswer(SessionTerminationAnswer request) throws IOException {
		try {
			
			//super.sendMessage(request);
			DiameterMessageImpl msg = (DiameterMessageImpl) request;
			this.serverSession.sendSessionTerminationAnswer(new SessionTermAnswerImpl(msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	public ServerAuthSession getSession()
	{
		return serverSession;
	}

	public void stateChanged(Enum oldState, Enum newState) {
		ServerAuthSessionState state=(ServerAuthSessionState) newState;
		
		switch(state)
		{
		case IDLE:
			this.state=AuthSessionState.Idle;
			break;
			
		case OPEN:
			this.state=AuthSessionState.Open;
			break;
		case DISCONNECTED:
			super.state=AuthSessionState.Disconnected;
			String sessionId = this.serverSession.getSessions().get(0)
			.getSessionId();

			this.serverSession.release();
			this.baseListener.sessionDestroyed(sessionId, this.serverSession);
			break;
		}
	}


}
