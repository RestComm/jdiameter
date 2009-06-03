package org.mobicents.slee.examples.diameter.cca;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlProvider;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;

/**
 * Start time:21:45:41 2008-12-14<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class CCAExampleSBB implements javax.slee.Sbb {

	private static Logger logger = Logger.getLogger(CCAExampleSBB.class);

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private CreditControlActivityContextInterfaceFactory acif = null;

	private CreditControlProvider provider = null;

	private CreditControlMessageFactory messageFactory = null;

	private CreditControlAVPFactory avpFactory = null;

	private TimerFacility timerFacility = null;

	private String originIP = "127.0.0.1";
	private String originPort = "1812";
	private String originRealm = "mobicents.org";

	private String destinationIP = "127.0.0.1";
	private String destinationPort = "3868";
	private String destinationRealm = "mobicents.org";

	private boolean actAsServer = false;

	// protected boolean isEventBased = true;
	// protected boolean sentInitialAnswer = false;
	// protected boolean sentUpdateAnswer = false;
	// protected boolean sentTerminationAnswer = false;

	public void setSbbContext(SbbContext context) {
		logger.info("sbbRolledBack invoked.");

		this.sbbContext = context;

		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");
			provider = (CreditControlProvider) myEnv.lookup("slee/resources/diameter-cca-ra-interface");
			logger.info("Got Provider:" + provider);

			messageFactory = provider.getCreditControlMessageFactory();
			logger.info("Got Message Factory:" + messageFactory);
			avpFactory = provider.getCreditControlAVPFactory();
			logger.info("Got AVP Factory:" + avpFactory);
			acif = (CreditControlActivityContextInterfaceFactory) myEnv.lookup("slee/resources/CCAResourceAdaptor/java.net/0.8.1/acif");

			// Get the timer facility
			timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
		} catch (Exception e) {
			logger.error("Unable to set sbb context.", e);
		}
	}

	public void unsetSbbContext() {
		if (logger.isInfoEnabled())
			logger.info("unsetSbbContext invoked.");

		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
		if (logger.isInfoEnabled())
			logger.info("sbbCreate invoked.");
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
		if (logger.isInfoEnabled())
			logger.info("sbbPostCreate invoked.");
	}

	public void sbbActivate() {
		if (logger.isInfoEnabled())
			logger.info("sbbActivate invoked.");
	}

	public void sbbPassivate() {
		if (logger.isInfoEnabled())
			logger.info("sbbPassivate invoked.");
	}

	public void sbbRemove() {
		if (logger.isInfoEnabled())
			logger.info("sbbRemove invoked.");
	}

	public void sbbLoad() {
		if (logger.isInfoEnabled())
			logger.info("sbbLoad invoked.");
	}

	public void sbbStore() {
		if (logger.isInfoEnabled())
			logger.info("sbbStore invoked.");
	}

	public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {
		if (logger.isInfoEnabled())
			logger.info("sbbRolledBack invoked.");
	}

	public void sbbRolledBack(RolledBackContext context) {
		if (logger.isInfoEnabled())
			logger.info("sbbRolledBack invoked.");
	}

	protected SbbContext getSbbContext() {
		if (logger.isInfoEnabled())
			logger.info("getSbbContext invoked.");

		return sbbContext;
	}

	// ##########################################################################
	// ## EVENT HANDLERS ##
	// ##########################################################################

	public void onServiceStartedEvent(javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci) {
		logger.info("onServiceStartedEvent invoked.");

		try {
			// check if it's my service that is starting
			ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();
			if (sa.equals(aci.getActivity())) {
				logger.info("Performing sanity check...");
				logger.info("Provider [" + provider + "]");
				logger.info("Message Factory [" + messageFactory + "]");
				logger.info("AVP Factory [" + avpFactory + "]");

				logger.info("Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3");

				logger.info("Connected to " + provider.getPeerCount() + " peers.");

				for (DiameterIdentity peer : provider.getConnectedPeers()) {
					logger.info("Connected to Peer[" + peer.toString() + "]");
				}

				// Initialize properties
				Properties props = new Properties();
				props.load(this.getClass().getClassLoader().getResourceAsStream("example.properties"));

				this.originIP = props.getProperty("origin.ip") == null ? this.originIP : props.getProperty("origin.ip");
				this.originPort = props.getProperty("origin.port") == null ? this.originPort : props.getProperty("origin.port");
				this.originRealm = props.getProperty("origin.realm") == null ? this.originRealm : props.getProperty("origin.realm");

				this.destinationIP = props.getProperty("destination.ip") == null ? this.destinationIP : props.getProperty("destination.ip");
				this.destinationPort = props.getProperty("destination.port") == null ? this.destinationPort : props.getProperty("destination.port");
				this.destinationRealm = props.getProperty("destination.realm") == null ? this.destinationRealm : props.getProperty("destination.realm");

				this.actAsServer = props.getProperty("example.mode") == null ? this.actAsServer : !props.getProperty("example.mode").trim().equals("client");

				logger.info("Diameter CCA Example :: Initialized in " + (actAsServer ? "SERVER" : "CLIENT") + " mode.");

				if (actAsServer) {
					TimerOptions options = new TimerOptions();
					timerFacility.setTimer(aci, null, System.currentTimeMillis() + 30000, options);
				}
			}
		} catch (Exception e) {
			logger.error("Failure initializing Service.", e);
		}
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		doSendEventCCR();
	}

	public void onCreditControlRequest(CreditControlRequest request, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Received Credit-Control-Request (Application-Id[" + request.getHeader().getApplicationId() + "].");

		// INITIAL_REQUEST(1), UPDATE_REQUEST(2), TERMINATION_REQUEST(3),
		// EVENT_REQUEST(4)
		CreditControlServerSession session = (CreditControlServerSession) aci.getActivity();
		CreditControlAnswer answer = null;

		switch (request.getCcRequestType().getValue()) {
		case 1:
			try {
				if (logger.isInfoEnabled())
					logger.info("Got INITIAL_REQUEST(1).");

				if (getSentInitialAnswer()) {
					logger.error("Error. Initial answer already sent! Aborting.");
					return;
				}

				answer = session.createCreditControlAnswer();
				answer.setResultCode(2001);
				if (logger.isInfoEnabled()) {
					logger.info("Processed Credit-Control-Request:\n" + request);
					logger.info("Sending Credit-Control-Answer:\n" + answer);
				}

				session.sendCreditControlAnswer(answer);
				this.setSentInitialAnswer(true);
			} catch (Exception e) {
				logger.error("Failed to create/send Credit-Control-Answer to reply INITIAL_REQUEST(1).", e);
			}
			break;
		case 2:
			try {
				if (logger.isInfoEnabled())
					logger.info("Got UPDATE_REQUEST(2).");

				if (getSentUpdateAnswer()) {
					logger.error("Error. Update answer already sent! Aborting.");
					return;
				}

				answer = session.createCreditControlAnswer();
				answer.setResultCode(2001);
				if (logger.isInfoEnabled()) {
					logger.info("Processed Credit-Control-Request:\n" + request);
					logger.info("Sending Credit-Control-Answer:\n" + answer);
				}
				session.sendCreditControlAnswer(answer);
				setSentUpdateAnswer(true);
			} catch (Exception e) {
				logger.error("Failed to create/send Credit-Control-Answer to reply UPDATE_REQUEST(2).", e);
			}
			break;
		case 3:
			try {
				if (logger.isInfoEnabled())
					logger.info("Got TERMINATION_REQUEST(3).");

				if (getSentTerminationAnswer()) {
					logger.error("Error. Termination answer already sent! Aborting.");
					return;
				}

				answer = session.createCreditControlAnswer();
				answer.setResultCode(2001);
				if (logger.isInfoEnabled()) {
					logger.info("Processed Credit-Control-Request:\n" + request);
					logger.info("Sending Credit-Control-Answer:\n" + answer);
				}
				session.sendCreditControlAnswer(answer);
				setSentTerminationAnswer(true);
			} catch (Exception e) {
				logger.error("Failed to create/send Credit-Control-Answer to reply TERMINATION_REQUEST(3).", e);
			}
			break;
		case 4:
			try {
				if (logger.isInfoEnabled())
					logger.info("Got EVENT_REQUEST(4).");

				answer = session.createCreditControlAnswer();
				answer.setResultCode(2001);
				if (logger.isInfoEnabled())
					logger.info("Sending Credit-Control-Answer:\n" + answer);

				session.sendCreditControlAnswer(answer);
			} catch (Exception e) {
				logger.error("Failed to create/send Credit-Control-Answer to reply EVENT_REQUEST(4).", e);
			}
			break;

		default:
			logger.error("Unexpected CC-Request-Type in message: " + request.getCcRequestType() + ". Aborting...");
		}
	}

	public void onCreditControlAnswer(CreditControlAnswer answer, ActivityContextInterface aci) {
		logger.info("Received CCA with Result-Code[" + answer.getResultCode() + "].");
	}

	// ##########################################################################
	// ## PRIVATE METHODS ##
	// ##########################################################################

	private void doSendEventCCR() {
		try {
			CreditControlClientSession session = this.provider.createClientSession();
			ActivityContextInterface localACI = this.acif.getActivityContextInterface(session);
			localACI.attach(this.getSbbContext().getSbbLocalObject());

			CreditControlRequest request = session.createCreditControlRequest();

			List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

			avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_HOST, ("aaa://" + originIP + ":" + originPort).getBytes()));
			avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_REALM, originRealm.getBytes()));

			avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, ("aaa://" + destinationIP + ":" + destinationPort).getBytes()));
			avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, destinationRealm.getBytes()));

			avps.add(avpFactory.getBaseFactory().createAvp(CreditControlAVPCodes.CC_Request_Type, 4l));
			avps.add(avpFactory.getBaseFactory().createAvp(CreditControlAVPCodes.CC_Request_Number, 0l));

			RequestedServiceUnitAvp rsu = this.avpFactory.createRequestedServiceUnit();

			CcMoneyAvp ccMoney = this.avpFactory.createCcMoney();
			ccMoney.setCurrencyCode(100);
			ccMoney.setUnitValue(this.avpFactory.createUnitValue());

			rsu.setCreditControlInputOctets(10);
			rsu.setCreditControlMoneyAvp(ccMoney);
			rsu.setCreditControlServiceSpecificUnits(1000);
			rsu.setCreditControlTime(100);
			rsu.setCreditControlTotalOctets(5000);

			avps.add(rsu);

			avps.add(avpFactory.getBaseFactory().createAvp(CreditControlAVPCodes.Requested_Action, 0));

			// Now create & send
			request.setExtensionAvps(avps.toArray(new DiameterAvp[avps.size()]));
			if (logger.isInfoEnabled())
				logger.info("About to send:\n" + request);

			session.sendCreditControlRequest(request);
		} catch (Exception e) {
			logger.error("Failed to create/send Credit-Control-Request.", e);
		}
	}

	// CMPS:
	public abstract void setSentInitialAnswer(boolean b);

	public abstract boolean getSentInitialAnswer();

	public abstract void setSentUpdateAnswer(boolean b);

	public abstract boolean getSentUpdateAnswer();

	public abstract void setSentTerminationAnswer(boolean b);

	public abstract boolean getSentTerminationAnswer();

}
