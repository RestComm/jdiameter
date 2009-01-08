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
		return this.messageFactory.createProfileUpdateAnswer(resultCode, isExperimentalResult);
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer() {
		return this.messageFactory.createProfileUpdateAnswer();
	}

	public UserDataAnswer createUserDataAnswer(byte[] userData) {
		return this.messageFactory.createUserDataAnswer(userData);
	}

	public UserDataAnswer createUserDataAnswer(long resultCode,
			boolean isExperimentalResult) {
		return this.messageFactory.createUserDataAnswer(resultCode, isExperimentalResult);
	}

	public UserDataAnswer createUserDataAnswer() {
		return this.messageFactory.createUserDataAnswer();
	}

	public void sendProfileUpdateAnswer(ProfileUpdateAnswer message)
			throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) message;
		fetchSessionData(msg);
		try {
			this.serverSession.sendProfileUpdateAnswer(new ProfileUpdateAnswerImpl((Answer) msg.getGenericData()));
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		} 

	}

	public void sendUserDataAnswer(UserDataAnswer message) throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) message;
		fetchSessionData(msg);
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
	
	private void fetchSessionData(DiameterMessage msg)
	{
		
	}

	@Override
	public Object getDiameterAvpFactory() {
		
		return this.avpFactory;
	}

	@Override
	public Object getDiameterMessageFactory() {
		return this.messageFactory;
	}

}
