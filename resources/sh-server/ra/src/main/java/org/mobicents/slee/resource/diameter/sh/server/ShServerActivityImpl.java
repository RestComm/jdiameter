package org.mobicents.slee.resource.diameter.sh.server;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.handlers.ShServerSessionListener;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
/**
 * Start time:16:43:18 2009-01-06<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShServerActivityImpl extends DiameterActivityImpl implements
		net.java.slee.resource.diameter.sh.server.ShServerActivity,
		StateChangeListener {

	
	protected ServerShSession serverSession = null;
	protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
	protected ShServerSessionListener listener = null;
	protected DiameterShAvpFactory shAvpFactory = null;
	protected ShServerMessageFactoryImpl messageFactory = null;

	
	protected DiameterIdentityAvp clientOriginHost=null;
	protected DiameterIdentityAvp clientOriginRealm=null;
	
	public ShServerActivityImpl(
			ShServerMessageFactory shServerMessageFactory,
			DiameterShAvpFactory diameterShAvpFactory, ServerShSession session,
			long timeout, DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint) {
		super(null, null, null,
				(EventListener<Request, Answer>) session, timeout,
				destinationHost, destinationRealm, endpoint);
		this.serverSession = session;
		this.serverSession.addStateChangeNotification(this);
		super.setCurrentWorkingSession(this.serverSession.getSessions().get(0));
		this.shAvpFactory = diameterShAvpFactory;
		this.messageFactory = (ShServerMessageFactoryImpl) shServerMessageFactory;

	}
	
	public ProfileUpdateAnswer createProfileUpdateAnswer(long resultCode,
			boolean isExperimentalResult) {
		ProfileUpdateAnswer answer=this.messageFactory.createProfileUpdateAnswer(resultCode, isExperimentalResult);
		if(answer.getDestinationHost()==null && clientOriginHost!=null)
			answer.setDestinationHost(clientOriginHost);
		if(answer.getDestinationRealm()==null && clientOriginRealm!=null)
			answer.setDestinationRealm(clientOriginRealm);
		return answer;
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer() {
		
		ProfileUpdateAnswer answer=this.messageFactory.createProfileUpdateAnswer();
		if(answer.getDestinationHost()==null && clientOriginHost!=null)
			answer.setDestinationHost(clientOriginHost);
		if(answer.getDestinationRealm()==null && clientOriginRealm!=null)
			answer.setDestinationRealm(clientOriginRealm);
		return answer;
		
	}

	public UserDataAnswer createUserDataAnswer(byte[] userData) {
		
		UserDataAnswer answer=createUserDataAnswer(userData);
		if(answer.getDestinationHost()==null && clientOriginHost!=null)
			answer.setDestinationHost(clientOriginHost);
		if(answer.getDestinationRealm()==null && clientOriginRealm!=null)
			answer.setDestinationRealm(clientOriginRealm);
		return answer;
	}

	public UserDataAnswer createUserDataAnswer(long resultCode,
			boolean isExperimentalResult) {
		UserDataAnswer answer=this.messageFactory.createUserDataAnswer(resultCode, isExperimentalResult);
		if(answer.getDestinationHost()==null && clientOriginHost!=null)
			answer.setDestinationHost(clientOriginHost);
		if(answer.getDestinationRealm()==null && clientOriginRealm!=null)
			answer.setDestinationRealm(clientOriginRealm);
		return answer;
	}

	public UserDataAnswer createUserDataAnswer() {
		UserDataAnswer answer=this.messageFactory.createUserDataAnswer();
		if(answer.getDestinationHost()==null && clientOriginHost!=null)
			answer.setDestinationHost(clientOriginHost);
		if(answer.getDestinationRealm()==null && clientOriginRealm!=null)
			answer.setDestinationRealm(clientOriginRealm);
		return answer;
	}

	public void sendProfileUpdateAnswer(ProfileUpdateAnswer message)
			throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) message;
		fetchSessionData(msg,false);
		try {
			this.serverSession.sendProfileUpdateAnswer(new ProfileUpdateAnswerImpl((Answer) msg.getGenericData()));
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		} 

	}

	public void sendUserDataAnswer(UserDataAnswer message) throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) message;
		fetchSessionData(msg,false);
		try {
			this.serverSession.sendUserDataAnswer(new UserDataAnswerImpl((Answer) msg.getGenericData()));
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		} 

	}

	public void stateChanged(Enum oldState, Enum newState) {
		org.jdiameter.common.api.app.sh.ShSessionState _state=(org.jdiameter.common.api.app.sh.ShSessionState) newState;
		switch(_state)
		{
		case NOTSUBSCRIBED:
			break;
		case SUBSCRIBED:
			//FIXME: error?
			//This should not happen!!!
			break;
		case TERMINATED:
			state=ShSessionState.TERMINATED;
			listener.sessionDestroyed(getSessionId(),serverSession);
			break;
		}
	}
	
	public void fetchSessionData(DiameterMessage msg, boolean incoming)
	{
		if(msg.getHeader().isRequest())
		{
			//Well it should always be getting this on request and only once ?
			if(incoming)
			{
				if(this.clientOriginHost==null)
					this.clientOriginHost=msg.getOriginHost();
				if(this.clientOriginRealm==null)
					this.clientOriginRealm=msg.getOriginRealm();
			}else
			{
				//FIXME, do more :)
			}
		}
	}

	@Override
	public Object getDiameterAvpFactory() {
		
		return this.avpFactory;
	}

	@Override
	public Object getDiameterMessageFactory() {
		return this.messageFactory;
	}

	@Override
	public Object getSessionListener() {

		return this.listener;
	}

	@Override
	public void setSessionListener(Object ra) {
		this.listener = listener;
	}

}
