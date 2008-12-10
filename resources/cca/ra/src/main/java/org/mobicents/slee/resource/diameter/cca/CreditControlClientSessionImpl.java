/**
 * Start time:15:00:53 2008-12-08<br>
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

import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;


/**
 * Start time:15:00:53 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlClientSessionImpl extends CreditControlSessionImpl
		implements CreditControlClientSession {

	protected ClientCCASession session = null;
	protected ArrayList<DiameterAvp> sessionAvps = new ArrayList<DiameterAvp>();

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
	public CreditControlClientSessionImpl(
			CreditControlMessageFactory messageFactory,
			CreditControlAVPFactory avpFactory, ClientCCASession session,
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
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * createCreditControlRequest()
	 */
	public CreditControlRequest createCreditControlRequest() {

		CreditControlRequest request = super.ccaMessageFactory
				.createCreditControlRequest(super.getSessionId());
		if (destinationHost != null) {
			request.setDestinationHost(destinationHost);
		}
		if (destinationRealm != null) {
			request.setDestinationRealm(destinationRealm);
		}

		// Fille extension avps if present
		if (sessionAvps.size() > 0) {
			try {
				request.setExtensionAvps(sessionAvps
						.toArray(new DiameterAvp[sessionAvps.size()]));
			} catch (AvpNotAllowedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendCreditControlRequest(CreditControlRequest ccr)
			throws IOException {

		fetchCurrentState(ccr);
		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;
		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl(
					(Request) msg.getGenericData()));
		} catch (InternalException e) {

			throw new IOException(e);
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			throw new IOException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendInitialCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendInitialCreditControlRequest(CreditControlRequest ccr)
			throws IOException {

		ccr.setCcRequestType(CcRequestType.INITIAL_REQUEST);
		fetchCurrentState(ccr);
		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;
		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl(
					(Request) msg.getGenericData()));
		} catch (InternalException e) {

			throw new IOException(e);
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendReAuthAnswer
	 * (net.java.slee.resource.diameter.base.events.ReAuthAnswer)
	 */
	public void sendReAuthAnswer(ReAuthAnswer rar) throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) rar;
		try {
			session.sendReAuthAnswer(new ReAuthAnswerImpl((Answer) msg
					.getGenericData()));
		} catch (InternalException e) {

			throw new IOException(e);
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			throw new IOException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendTerminationCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendTerminationCreditControlRequest(CreditControlRequest ccr)
			throws IOException {
		// This should not be used to terminate sub-sessions!!!!
		ccr.setCcRequestType(CcRequestType.TERMINATION_REQUEST);
		fetchCurrentState(ccr);
		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;
		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl(
					(Request) msg.getGenericData()));
		} catch (InternalException e) {

			throw new IOException(e);
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			throw new IOException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendUpdateCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendUpdateCreditControlRequest(CreditControlRequest ccr)
			throws IOException {

		ccr.setCcRequestType(CcRequestType.UPDATE_REQUEST);
		fetchCurrentState(ccr);
		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;
		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl(
					(Request) msg.getGenericData()));
		} catch (InternalException e) {

			throw new IOException(e);
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			throw new IOException(e);
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
		// TODO Auto-generated method stub

	}

	public void fetchCurrentState(CreditControlRequest ccr) {

	}

	public void fetchCurrentState(CreditControlAnswer cca) {

	}

	

	
	
	
}
