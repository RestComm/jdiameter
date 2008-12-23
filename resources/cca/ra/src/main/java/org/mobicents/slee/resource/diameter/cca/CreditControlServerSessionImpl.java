/**
 * Start time:15:26:12 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;
import net.java.slee.resource.diameter.cca.CreditControlSessionState;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.handlers.CCASessionCreationListener;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.common.api.app.cca.ClientCCASessionState;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

/**
 * Start time:15:26:12 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlServerSessionImpl extends CreditControlSessionImpl
		implements CreditControlServerSession {

	protected ServerCCASession session = null;
	protected ArrayList<DiameterAvp> sessionAvps = new ArrayList<DiameterAvp>();
	protected CreditControlRequest lastRequest = null;

	/**
	 * @param messageFactory
	 * @param avpFactory
	 * @param session
	 * @param raEventListener
	 * @param timeout
	 * @param destinationHost
	 * @param destinationRealm
	 * @param endpoint
	 */
	public CreditControlServerSessionImpl(
			CreditControlMessageFactory messageFactory,
			CreditControlAVPFactory avpFactory, ServerCCASession session,
			long timeout, DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null,
				(EventListener<Request, Answer>) session, timeout,
				destinationHost, destinationRealm, endpoint);

		this.session = session;
		this.session.addStateChangeNotification(this);
		super.setCurrentWorkingSession(this.session.getSessions().get(0));

	}

	public void endActivity() {
		this.listener.sessionDestroyed(this.sessionId, this);
		this.session.release();
	}

	public Object getDiameterAvpFactory() {
		return this.ccaAvpFactory;
	}

	public Object getDiameterMessageFactory() {

		return this.ccaMessageFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlServerSession#
	 * createCreditControlAnswer()
	 */
	public CreditControlAnswer createCreditControlAnswer() {
		CreditControlAnswer answer = super.ccaMessageFactory
				.createCreditControlAnswer(lastRequest);

		// Fille extension avps if present
		if (sessionAvps.size() > 0) {
			try {
				answer.setExtensionAvps(sessionAvps
						.toArray(new DiameterAvp[sessionAvps.size()]));
			} catch (AvpNotAllowedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlServerSession#
	 * sendCreditControlAnswer
	 * (net.java.slee.resource.diameter.cca.events.CreditControlAnswer)
	 */
	public void sendCreditControlAnswer(CreditControlAnswer cca)
			throws IOException {
		fetchCurrentState(cca);
		DiameterMessageImpl msg = (DiameterMessageImpl) cca;
		try {
			session.sendCreditControlAnswer(new JCreditControlAnswerImpl(
					(Answer) msg.getGenericData()));
		} catch (InternalException e) {

			// throw new IOException(e);
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// throw new IOException(e);
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlServerSession#
	 * sendReAuthRequest
	 * (net.java.slee.resource.diameter.base.events.ReAuthRequest)
	 */
	public void sendReAuthRequest(ReAuthRequest rar) throws IOException {

		DiameterMessageImpl msg = (DiameterMessageImpl) rar;
		try {
			session.sendReAuthRequest(new ReAuthRequestImpl((Request) msg
					.getGenericData()));
		} catch (InternalException e) {

			// throw new IOException(e);
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// throw new IOException(e);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum,
	 * java.lang.Enum)
	 */
	public void stateChanged(Enum oldState, Enum newState) {

		ClientCCASessionState s = (ClientCCASessionState) newState;
		// IDLE(0),
		// PENDING_INITIAL(1),
		// OPEN(2),
		// PENDING_UPDATE(3),
		// PENDING_TERMINATION(4),
		// PENDING_BUFFERED(5),
		// PENDING_EVENT(6);
		// ints are faster :)
		switch (s.getValue()) {

		case 6:
			// FIXME: this should not happen?
			this.state = CreditControlSessionState.OPEN;
			break;

		case 0:
			
			this.state = CreditControlSessionState.IDLE;
			
			((CCASessionCreationListener) this.getSessionListener())
					.sessionDestroyed(sessionId, this);
			this.session.release();
			break;
		default:
			logger.error("GOT WRONG STATE NUMBER: " + s);
		}

	}

	public void fetchCurrentState(CreditControlRequest ccr) {

	}

	public void fetchCurrentState(CreditControlAnswer cca) {

	}

}
