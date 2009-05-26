package org.mobicents.slee.resource.diameter.base;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.AccountingClientSessionActivity;
import net.java.slee.resource.diameter.base.AccountingSessionState;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.impl.app.acc.ClientAccSessionImpl;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

public class AccountingClientSessionActivityImpl extends
		AccountingSessionActivityImpl implements
		AccountingClientSessionActivity {

	protected AccountingSessionState state = null;
	protected ClientAccSession clientSession = null;

	public AccountingClientSessionActivityImpl(
			DiameterMessageFactoryImpl messageFactory,
			DiameterAvpFactoryImpl avpFactory, ClientAccSession clientSession,
			long timeout, DiameterIdentity destinationHost,
			DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null,
				(EventListener<Request, Answer>) clientSession, timeout,
				destinationHost, destinationRealm, endpoint);

		this.clientSession = clientSession;
		this.clientSession.addStateChangeNotification(this);
		this.state=AccountingSessionState.Idle;
		super.setCurrentWorkingSession(this.clientSession.getSessions().get(0));
		// this.clientSession.addStateChangeNotification(this);
		// TODO Auto-generated constructor stub
	}

	public void sendAccountRequest(AccountingRequest request)
			throws IOException {

		// FIXME: baranowb - add here magic to pick up and set in super correct
		// Session in case of Relly agent
		DiameterMessageImpl msg = (DiameterMessageImpl) request;
		try {
			this.clientSession.sendAccountRequest(new AccountRequestImpl(
					(Request) msg.getGenericData()));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalException e) {
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

	public ClientAccSession getSession() {
		return this.clientSession;
	}

	public void stateChanged(Enum oldState, Enum newState) {

		ClientAccSessionState state = (ClientAccSessionState) newState;

		//FIXME: baranowb: PendingL - where does this fit?
		switch (state) {
		case IDLE:
			if(oldState!=state)
			{
				String sessionId = this.clientSession.getSessions().get(0)
					.getSessionId();
				this.state=AccountingSessionState.Idle;
				//this.clientSession.release();
				this.baseListener.sessionDestroyed(sessionId, this.clientSession);
			}
			break;
		case OPEN:
			this.state=AccountingSessionState.Open;
			break;
		case PENDING_EVENT:
			this.state=AccountingSessionState.PendingE;
			break;
		case PENDING_START:
			this.state=AccountingSessionState.PendingS;
			break;
		case PENDING_INTERIM:
			this.state=AccountingSessionState.PendingI;
			break;
		case PENDING_CLOSE:
			this.state=AccountingSessionState.PendingS;
			break;
		case PENDING_BUFFERED:
			this.state=AccountingSessionState.PendingB;
			break;
		}
	

	}

}
