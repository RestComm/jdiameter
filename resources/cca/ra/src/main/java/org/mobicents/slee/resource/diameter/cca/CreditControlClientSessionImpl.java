package org.mobicents.slee.resource.diameter.cca;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.DiameterException;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlSessionState;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.handlers.CCASessionCreationListener;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.common.api.app.cca.ClientCCASessionState;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

/**
 * Start time:15:00:53 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlClientSessionImpl extends CreditControlSessionImpl implements CreditControlClientSession {

	protected ClientCCASession session = null;
	protected ArrayList<DiameterAvp> sessionAvps = new ArrayList<DiameterAvp>();

	boolean terminateAfterAnswer = false;

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
	public CreditControlClientSessionImpl(CreditControlMessageFactory messageFactory, CreditControlAVPFactory avpFactory, ClientCCASession session, long timeout, DiameterIdentity destinationHost,
			DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null, (EventListener<Request, Answer>) session, timeout, destinationHost, destinationRealm, endpoint);

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
		// Create the request
		CreditControlRequest request = super.ccaMessageFactory.createCreditControlRequest(super.getSessionId());

		// If there's a Destination-Host, add the AVP
		if (destinationHost != null) {
			request.setDestinationHost(destinationHost);
		}

		if (destinationRealm != null) {
			request.setDestinationRealm(destinationRealm);
		}

		// Fill extension avps if present
		if (sessionAvps.size() > 0) {
			try {
				request.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
			} catch (AvpNotAllowedException e) {
				logger.error("Failed to add Session AVPs to request.", e);
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
	public void sendCreditControlRequest(CreditControlRequest ccr) throws IOException {
		// fetchCurrentState(ccr);

		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;
		validateState(ccr);
		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl((Request) msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendInitialCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendInitialCreditControlRequest(CreditControlRequest ccr) throws IOException {
		// FIXME: should this affect FSM ?
		ccr.setCcRequestType(CcRequestType.INITIAL_REQUEST);

		validateState(ccr);

		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;

		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl((Request) msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendUpdateCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendUpdateCreditControlRequest(CreditControlRequest ccr) throws IOException {
		// FIXME: Should this come already in the CCR?
		ccr.setCcRequestType(CcRequestType.UPDATE_REQUEST);

		validateState(ccr);

		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;

		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl((Request) msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlClientSession#
	 * sendTerminationCreditControlRequest
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public void sendTerminationCreditControlRequest(CreditControlRequest ccr) {
		// This should not be used to terminate sub-sessions!

		// FIXME: Should this come already in the CCR?
		ccr.setCcRequestType(CcRequestType.TERMINATION_REQUEST);

		validateState(ccr);

		DiameterMessageImpl msg = (DiameterMessageImpl) ccr;

		try {
			session.sendCreditControlRequest(new JCreditControlRequestImpl((Request) msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
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
			session.sendReAuthAnswer(new ReAuthAnswerImpl((Answer) msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
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

		// IDLE(0), PENDING_EVENT(1), PENDING_INITIAL(2), PENDING_UPDATE(3),
		// PENDING_TERMINATION(4), PENDING_BUFFERED(5), OPEN(6);
		switch (s) {
		case PENDING_EVENT:
			this.state = CreditControlSessionState.PENDING_EVENT;
			break;

		case PENDING_BUFFERED:
			this.state = CreditControlSessionState.PENDING_BUFFERED;
			break;

		case PENDING_TERMINATION:
			this.state = CreditControlSessionState.PENDING_TERMINATION;
			break;
		case PENDING_UPDATE:
			this.state = CreditControlSessionState.PENDING_UPDATE;
			break;

		case OPEN:
			// FIXME: this should not happen?
			this.state = CreditControlSessionState.OPEN;
			break;

		case PENDING_INITIAL:
			this.state = CreditControlSessionState.PENDING_INITIAL;
			break;

		case IDLE:
			this.state = CreditControlSessionState.IDLE;

			ClientCCASessionState old = (ClientCCASessionState) oldState;
			if (old == ClientCCASessionState.PENDING_EVENT) {
				terminateAfterAnswer = true;
			} else {
				((CCASessionCreationListener) this.getSessionListener()).sessionDestroyed(sessionId, this);
				this.session.release();
			}
			break;

		default:
			logger.error("Unexpected state in Credit-Control Client FSM: " + s);
		}
	}

	private void validateState(CreditControlRequest ccr) {
		//this is used for methods that send specific messages. should be done in jdiam, but there is not hook for it now.
		if(ccr.getCcRequestType()==null)
		{
			throw new DiameterException("No request type is present!!");
		}
		int t = ccr.getCcRequestType().getValue();
		CreditControlSessionState currentState = this.getState();
		if(t == CcRequestType._INITIAL_REQUEST)
		{
			
			if(currentState!=CreditControlSessionState.IDLE )
			{
				//FIXME: change all exception to DiameterException
				throw new DiameterException("Failed to validate, intial event, wrong state: "+currentState);
			}
				
		}else if(t == CcRequestType._UPDATE_REQUEST)
		{
			if(currentState!=CreditControlSessionState.OPEN )
			{
				//FIXME: change all exception to DiameterException
				throw new DiameterException("Failed to validate, intial event, wrong state: "+currentState);
			}
		}else if(t == CcRequestType._TERMINATION_REQUEST)
		{
			if(currentState!=CreditControlSessionState.OPEN )
			{
				//FIXME: change all exception to DiameterException
				throw new DiameterException("Failed to validate, intial event, wrong state: "+currentState);
			}
		}else if(t == CcRequestType._EVENT_REQUEST)
		{
			if(currentState!=CreditControlSessionState.IDLE )
			{
				//FIXME: change all exception to DiameterException
				throw new DiameterException("Failed to validate, intial event, wrong state: "+currentState);
			}
		}

	}

	public boolean getTerminateAfterAnswer() {
		return this.terminateAfterAnswer;
	}
}
