package org.mobicents.slee.examples.diameter;

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

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.DiameterProvider;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.ResultCode;
import org.mobicents.slee.resource.diameter.base.AccountingServerSessionActivityImpl;

/**
 * 
 * DiameterExampleSbb.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 11:34:16 PM May 26, 2008 <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Erick Svenson
 */
public abstract class DiameterExampleSbb implements javax.slee.Sbb {

	private static Logger logger = Logger.getLogger(DiameterExampleSbb.class);

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private DiameterProvider provider = null;

	private DiameterMessageFactory messageFactory = null;
	private DiameterAvpFactory avpFactory = null;

	private TimerFacility timerFacility = null;
	private boolean actAsServer = false;
	private String originIP = "127.0.0.1";
	private String originPort = "1812";
	private String originRealm = "mobicents.org";

	private String destinationIP = "127.0.0.1";
	private String destinationPort = "21812";
	private String destinationRealm = "mobicents.org";
	public void setSbbContext(SbbContext context) {
		logger.info("sbbRolledBack invoked.");

		this.sbbContext = context;

		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");

			provider = (DiameterProvider) myEnv.lookup("slee/resources/diameter-base-ra-acif");
			logger.info("Got Provider:" + provider);

			messageFactory = provider.getDiameterMessageFactory();
			logger.info("Got Message Factory:" + provider);

			avpFactory = provider.getDiameterAvpFactory();
			logger.info("Got AVP Factory:" + provider);

			// Get the timer facility
			timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
		} catch (Exception e) {
			logger.error("Unable to set sbb context.", e);
		}
	}

	public void unsetSbbContext() {
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
				logger.info("################################################################################");
				logger.info("### D I A M E T E R   E X A M P L E   A P P L I C A T I O N  :: S T A R T E D ##");
				logger.info("################################################################################");

				messageFactory = provider.getDiameterMessageFactory();
				avpFactory = provider.getDiameterAvpFactory();

				logger.info("Performing sanity check...");
				logger.info("Provider [" + provider + "]");
				logger.info("Message Factory [" + messageFactory + "]");
				logger.info("AVP Factory [" + avpFactory + "]");
				logger.info("Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3");

				logger.info("Connected to " + provider.getPeerCount() + " peers.");

				for (DiameterIdentity peer : provider.getConnectedPeers())
					logger.info("Connected to Peer[" + peer.toString() + "]");
				
				
				Properties props = new Properties();
				props.load(this.getClass().getClassLoader().getResourceAsStream("example.properties"));
				this.actAsServer = props.getProperty("example.mode") == null ? this.actAsServer : !props.getProperty("example.mode").trim().equals("client");
				this.originIP = props.getProperty("origin.ip") == null ? this.originIP : props.getProperty("origin.ip");
				this.originPort = props.getProperty("origin.port") == null ? this.originPort : props.getProperty("origin.port");
				this.originRealm = props.getProperty("origin.realm") == null ? this.originRealm : props.getProperty("origin.realm");

				this.destinationIP = props.getProperty("destination.ip") == null ? this.destinationIP : props.getProperty("destination.ip");
				this.destinationPort = props.getProperty("destination.port") == null ? this.destinationPort : props.getProperty("destination.port");
				this.destinationRealm = props.getProperty("destination.realm") == null ? this.destinationRealm : props.getProperty("destination.realm");
				
				
				logger.info("Diameter Base Example :: Initialized in " + (actAsServer ? "SERVER" : "CLIENT") + " mode.");

				if (actAsServer) {
					TimerOptions options = new TimerOptions();
					timerFacility.setTimer(aci, null, System.currentTimeMillis() + 30000, options);
				}

				/*
				 * Basic message sending testing (DWR/DWA)
				 * 
				 * try { DiameterAvp avp_DestHost = avpFactory.createAvp(
				 * Avp.DESTINATION_HOST, "127.0.0.1".getBytes() ); DiameterAvp
				 * avp_DestRealm = avpFactory.createAvp( Avp.DESTINATION_REALM,
				 * "mobicents.org".getBytes() );
				 * 
				 * DiameterAvp avp_HostIPAddress = avpFactory.createAvp(
				 * Avp.HOST_IP_ADDRESS, ("0x0001" + "7f000001").getBytes() );
				 * 
				 * DiameterAvp avp_VendorId = avpFactory.createAvp(
				 * Avp.VENDOR_ID, "193".getBytes() ); DiameterAvp
				 * avp_ProductName = avpFactory.createAvp( Avp.PRODUCT_NAME,
				 * "jDiameter".getBytes() );
				 * 
				 * DiameterAvp[] avps = new DiameterAvp[]{avp_DestHost,
				 * avp_DestRealm, avp_HostIPAddress, avp_VendorId,
				 * avp_ProductName};
				 * 
				 * logger.info( "Creating Custom Message..." ); DiameterMessage
				 * ms = messageFactory.createDeviceWatchdogRequest(avps);
				 * logger.info( "Created Custom Message[" + ms + "]" );
				 * 
				 * logger.info( "Sending Custom Message..." );
				 * provider.createActivity().sendMessage( ms ); logger.info(
				 * "Sent Custom Message[" + ms + "]" ); } catch (Exception e) {
				 * logger.error( "Not working...", e ); }
				 */
			}
		} catch (Exception e) {
			logger.error("Unable to handle service started event...", e);
		}
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		sendAccountingRequest();
	}

	public void onAbortSessionRequest(net.java.slee.resource.diameter.base.events.AbortSessionRequest asr, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Abort-Session-Request received.");
	}

	public void onAbortSessionAnswer(net.java.slee.resource.diameter.base.events.AbortSessionAnswer asa, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Abort-Session-Answer received.");
	}

	public void onAccountingRequest(net.java.slee.resource.diameter.base.events.AccountingRequest acr, ActivityContextInterface aci) {
		long start = System.currentTimeMillis();
		if (logger.isInfoEnabled())
			logger.info("Accounting-Request received. [" + acr + "]");

		boolean actAsProxy = false;

		try {
			// Are we gonna act as a proxy?
			if (actAsProxy) {
				// In here we act as a "proxy". Just for testing we take the
				// original message,
				// replace the Origin/Destination Host/Realm AVPs and send it to
				// the emulator.

				boolean hasDestinationHost = false;
				boolean hasDestinationRealm = false;

				List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

				for (DiameterAvp avp : acr.getAvps()) {
					switch (avp.getCode()) {
					case Avp.ORIGIN_HOST:
						avps.add(avpFactory.createAvp(Avp.ORIGIN_HOST, "aaa://"+originIP+":"+originPort.getBytes()));
						break;
					case Avp.ORIGIN_REALM:
						avps.add(avpFactory.createAvp(Avp.ORIGIN_REALM, originRealm.getBytes()));
						break;
					case Avp.DESTINATION_HOST:
						avps.add(avpFactory.createAvp(Avp.DESTINATION_HOST, "aaa://"+destinationIP+":"+destinationPort.getBytes()));
						hasDestinationHost = true;
						break;
					case Avp.DESTINATION_REALM:
						avps.add(avpFactory.createAvp(Avp.DESTINATION_REALM, destinationRealm.getBytes()));
						hasDestinationRealm = true;
						break;
					default:
						avps.add(avp);
					}
				}

				if (!hasDestinationHost)
					avps.add(avpFactory.createAvp(Avp.DESTINATION_HOST, "127.0.0.1".getBytes()));

				if (!hasDestinationRealm)
					avps.add(avpFactory.createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes()));
				if (logger.isInfoEnabled())
					logger.info("AVPs ==> " + avps);

				DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
				avpArray = avps.toArray(avpArray);
				if (logger.isInfoEnabled())
					logger.info("Creating Custom Message...");
				DiameterMessage ms = messageFactory.createAccountingRequest(avpArray);
				if (logger.isInfoEnabled()) {
					logger.info("Created Custom Message[" + ms + "]");

					logger.info("Sending Custom Message...");
				}
				provider.createActivity().sendMessage(ms);
				if (logger.isInfoEnabled())
					logger.info("Sent Custom Message[" + ms + "]");
			} else {
				// In here we act as a server and just say it's SUCCESS.

				if (aci.getActivity() instanceof AccountingServerSessionActivityImpl) {
					AccountingServerSessionActivityImpl assa = (AccountingServerSessionActivityImpl) aci.getActivity();

					AccountingAnswer ans = assa.createAccountAnswer(acr, ResultCode.SUCCESS);
					if (logger.isInfoEnabled())
						logger.info("Sending Accounting-Answer [" + ans + "]");

					assa.sendAccountAnswer(ans);
					if (logger.isInfoEnabled())
						logger.info("Accounting-Answer sent.");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		long end = System.currentTimeMillis();

		if (logger.isInfoEnabled())
			logger.info("Accounting-Request proccessed. [" + (end - start) + "ms]");
	}

	public void onAccountingAnswer(net.java.slee.resource.diameter.base.events.AccountingAnswer aca, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Accounting-Answer received.");
	}

	public void onCapabilitiesExchangeRequest(net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest cer, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Capabilities-Exchange-Request received.");
	}

	public void onCapabilitiesExchangeAnswer(net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer cea, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Capabilities-Exchange-Answer received.");
	}

	public void onDeviceWatchdogRequest(net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest dwr, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Device-Watchdog-Request received.");
	}

	public void onDeviceWatchdogAnswer(net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer dwa, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Device-Watchdog-Answer received.");
	}

	public void onDisconnectPeerRequest(net.java.slee.resource.diameter.base.events.DisconnectPeerRequest dpr, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Disconnect-Peer-Request received.");
	}

	public void onDisconnectPeerAnswer(net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer dpa, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Disconnect-Peer-Answer received.");
	}

	public void onReAuthRequest(net.java.slee.resource.diameter.base.events.ReAuthRequest rar, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Re-Auth-Request received.");
	}

	public void onReAuthAnswer(net.java.slee.resource.diameter.base.events.ReAuthAnswer raa, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Re-Auth-Answer received.");
	}

	public void onSessionTerminationRequest(net.java.slee.resource.diameter.base.events.SessionTerminationRequest rar, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Session-Termination-Request received.");
	}

	public void onSessionTerminationAnswer(net.java.slee.resource.diameter.base.events.SessionTerminationAnswer raa, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Session-Termination-Answer received.");
	}

	public void onErrorAnswer(net.java.slee.resource.diameter.base.events.ErrorAnswer era, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Error-Answer received.");
	}

	// ##########################################################################
	// ## PRIVATE METHODS ##
	// ##########################################################################

	private void sendAccountingRequest() {
		try {
			DiameterActivity activity = provider.createActivity();

			List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

			avps.add(avpFactory.createAvp(Avp.SESSION_ID, activity.getSessionId().getBytes()));

			DiameterAvp avpVendorId = avpFactory.createAvp(Avp.VENDOR_ID, 193);
			DiameterAvp avpAcctApplicationId = avpFactory.createAvp(Avp.ACCT_APPLICATION_ID, 19302);

			avps.add(avpFactory.createAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[] { avpVendorId, avpAcctApplicationId }));

			avps.add(avpFactory.createAvp(Avp.ORIGIN_HOST, "aaa://127.0.0.1:1812".getBytes()));
			avps.add(avpFactory.createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes()));

			avps.add(avpFactory.createAvp(Avp.DESTINATION_HOST, "aaa://127.0.0.1:21812".getBytes()));
			avps.add(avpFactory.createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes()));

			// Subscription ID
			DiameterAvp subscriptionIdType = avpFactory.createAvp(193, 555, 0);
			DiameterAvp subscriptionIdData = avpFactory.createAvp(193, 554, "00001000");
			avps.add(avpFactory.createAvp(193, 553, new DiameterAvp[] { subscriptionIdType, subscriptionIdData }));

			// Requested Service Unit
			DiameterAvp unitType = avpFactory.createAvp(193, 611, 2);
			DiameterAvp valueDigits = avpFactory.createAvp(193, 617, 10L);
			DiameterAvp unitValue = avpFactory.createAvp(193, 612, new DiameterAvp[] { valueDigits });
			avps.add(avpFactory.createAvp(193, 606, new DiameterAvp[] { unitType, unitValue }));

			// Record Number and Type
			avps.add(avpFactory.createAvp(Avp.ACC_RECORD_NUMBER, 0));
			avps.add(avpFactory.createAvp(Avp.ACC_RECORD_TYPE, 1));

			// Requested action
			avps.add(avpFactory.createAvp(193, 615, 0));

			// Service Parameter Type
			DiameterAvp serviceParameterType = avpFactory.createAvp(193, 608, 0);
			DiameterAvp serviceParameterValue = avpFactory.createAvp(193, 609, "510");
			avps.add(avpFactory.createAvp(193, 607, new DiameterAvp[] { serviceParameterType, serviceParameterValue }));

			// Service Parameter Type
			DiameterAvp serviceParameterType2 = avpFactory.createAvp(193, 608, 14);
			DiameterAvp serviceParameterValue2 = avpFactory.createAvp(193, 609, "20");
			avps.add(avpFactory.createAvp(193, 607, new DiameterAvp[] { serviceParameterType2, serviceParameterValue2 }));

			DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
			avpArray = avps.toArray(avpArray);
			if (logger.isInfoEnabled())
				logger.info("Creating Custom Message...");
			DiameterMessage ms = messageFactory.createAccountingRequest(avpArray);
			if (logger.isInfoEnabled()) {
				logger.info("Created Custom Message[" + ms + "]");

				logger.info("Sending Custom Message...");
			}
			activity.sendMessage(ms);
			if (logger.isInfoEnabled())
				logger.info("Sent Custom Message[" + ms + "]");
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
