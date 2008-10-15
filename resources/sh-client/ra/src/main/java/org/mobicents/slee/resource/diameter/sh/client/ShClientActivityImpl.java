package org.mobicents.slee.resource.diameter.sh.client;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.handlers.ShClientSessionListener;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

/**
 * 
 * <br><br>Super project:  mobicents-jainslee-server
 * <br>16:46:45 2008-09-10	
 * <br>
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public class ShClientActivityImpl extends DiameterActivityImpl implements ShClientActivity , StateChangeListener{

	
	protected ClientShSession clientSession=null;
	protected ShSessionState state=ShSessionState.NOTSUBSCRIBED;
	protected ShClientSessionListener listener=null;
	protected DiameterShAvpFactory shAvpFactory=null;
	protected ShClientMessageFactory messageFactory=null;
	public ShClientActivityImpl(DiameterMessageFactoryImpl messageFactory, ShClientMessageFactory shClientMessageFactory, DiameterAvpFactoryImpl avpFactory, DiameterShAvpFactory diameterShAvpFactory, ClientShSession session,
			long timeout, DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null, (EventListener<Request, Answer>) session, timeout,
				destinationHost, destinationRealm, endpoint);
		this.clientSession = session;
		this.clientSession.addStateChangeNotification(this);	
		super.setCurrentWorkingSession(this.clientSession.getSessions().get(0));
		this.shAvpFactory=diameterShAvpFactory;
		this.messageFactory=shClientMessageFactory;
		
	}

	public void sendProfileUpdateRequest(ProfileUpdateRequest message) throws IOException {
		
		DiameterMessageImpl msg = (DiameterMessageImpl) message;
		try {
			clientSession.sendProfileUpdateRequest(new ProfileUpdateRequestImpl((Request) msg.getGenericData()));
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void sendUserDataRequest(UserDataRequest message) throws IOException {
		
		DiameterMessageImpl msg = (DiameterMessageImpl) message;
		try {
			this.clientSession.sendUserDataRequest(new UserDataRequestImpl((Request) msg.getGenericData()));
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	
	@Override
	public Object getSessionListener() {
		return this.listener;
	}

	@Override
	public void setSessionListener(Object ra) {
		this.listener=(ShClientSessionListener) ra;
	}

	public void endActivity() {
		this.clientSession.release();

	}

	public Object getDiameterAvpFactory() {
		return this.shAvpFactory;
	}

	public Object getDiameterMessageFactory() {
		
		return this.messageFactory;
	}

	public String getSessionId() {
		return super.getSessionId();
	}

	public void sendMessage(DiameterMessage message) throws IOException {
		super.sendMessage(message);
	}

	public void stateChanged(Enum oldState, Enum newState) {
		org.jdiameter.common.api.app.sh.ShSessionState _state=(org.jdiameter.common.api.app.sh.ShSessionState) newState;
		switch(_state)
		{
		case NOTSUBSCRIBED:
			break;
		case SUBSCRIBED:
			//FIXME: error?
			break;
		case TERMINATED:
			state=ShSessionState.TERMINATED;
			listener.sessionDestroyed(getSessionId(),clientSession);
			break;
		}
	}

	ClientShSession getClientSession()
	{
		return this.clientSession;
	}
	
}
