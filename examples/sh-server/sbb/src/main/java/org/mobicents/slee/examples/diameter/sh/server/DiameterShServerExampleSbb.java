package org.mobicents.slee.examples.diameter.sh.server;

import java.io.IOException;
import java.util.Arrays;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.server.ShServerActivity;
import net.java.slee.resource.diameter.sh.server.ShServerActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.ShServerProvider;
import net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.apache.log4j.Logger;

/**
 * 
 * DiameterShServerExampleSbb.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 11:34:16 PM Jan 13, 2009 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class DiameterShServerExampleSbb implements javax.slee.Sbb {

	private static Logger logger = Logger.getLogger(DiameterShServerExampleSbb.class);

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private ShServerProvider provider = null;

	private ShServerMessageFactory messageFactory = null;
	private DiameterShAvpFactory avpFactory = null;
	private ShServerActivityContextInterfaceFactory acif = null;
	private TimerFacility timerFacility = null;

	public void setSbbContext(SbbContext context) {
		logger.info("sbbRolledBack invoked.");

		this.sbbContext = context;

		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");

			provider = (ShServerProvider) myEnv.lookup("slee/resources/diameter-sh-server-ra-interface");
			logger.info("Got Provider:" + provider);

			messageFactory = provider.getServerMessageFactory();
			logger.info("Got Message Factory:" + messageFactory);

			avpFactory = provider.getAvpFactory();
			logger.info("Got AVP Factory:" + avpFactory);

			acif = (ShServerActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterShServerResourceAdaptor/java.net/0.8.1/acif");

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
				logger.info("################################################################################");
				logger.info("##   D I A M E T E R   S h   S E R V E R   E X A M P L E   ::   S T A R T E D ##");
				logger.info("################################################################################");

				messageFactory = provider.getServerMessageFactory();
				avpFactory = provider.getAvpFactory();

				logger.info("Performing sanity check...");
				logger.info("Provider [" + provider + "]");
				logger.info("Message Factory [" + messageFactory + "]");
				logger.info("AVP Factory [" + avpFactory + "]");
				logger.info("Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3");

				logger.info("Connected to " + provider.getPeerCount() + " peers.");

				for (DiameterIdentity peer : provider.getConnectedPeers()) {
					logger.info("Connected to Peer[" + peer.toString() + "]");
				}
			}
		} catch (Exception e) {
			logger.error("Unable to handle service started event...", e);
		}
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		ShServerSubscriptionActivity activity = null;

		for (ActivityContextInterface _aci : this.getSbbContext().getActivities()) {
			if (_aci.getActivity() instanceof ShServerSubscriptionActivity) {
				activity = (ShServerSubscriptionActivity) _aci.getActivity();
				break;
			}
		}

		if (activity == null) {
			logger.error("onTimerEvent :: Activity is null, with list: " + Arrays.toString(this.getSbbContext().getActivities()));
			return;
		}

		PushNotificationRequest request = activity.createPushNotificationRequest();
		logger.info("onTimerEvent :: Created PNR:\r\n" + request);

		try {
			request.setUserData("HEHE, some secrete user data.");
			activity.sendPushNotificationRequest(request);
		} catch (Exception e) {
			logger.error("Failed to send PNR.", e);
		}
	}

	public void onUserDataRequest(UserDataRequest event, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("onUserDataRequest :: " + event);

		UserDataAnswer answer = ((ShServerActivity) aci.getActivity()).createUserDataAnswer(2001, false);

		try {
			if (logger.isInfoEnabled())
				logger.info("onUserDataRequest :: Created UDA:\r\n" + answer);

			((ShServerActivity) aci.getActivity()).sendUserDataAnswer(answer);
		} catch (IOException e) {
			logger.error("Failed to send UDA.", e);
		}
	}

	public void onSubscribeNotificationsRequest(SubscribeNotificationsRequest event, ActivityContextInterface aci) {
		logger.info("onSubscribeNotificationsRequest :: " + event);

		SubscribeNotificationsAnswer answer = ((ShServerSubscriptionActivity) aci.getActivity()).createSubscribeNotificationsAnswer(2001, false);

		try {
			// This will be fixed in B2, we need more accessors
			DiameterAvp requestNumber = null;

			for (DiameterAvp a : event.getAvps()) {
				if (a.getCode() == DiameterShAvpCodes.SUBS_REQ_TYPE) {
					requestNumber = a;
					break;
				}
			}

			if (requestNumber != null) {
				answer.setExtensionAvps(requestNumber);
			}

			logger.info(" onSubscribeNotificationsRequest :: Created SNA:\r\n" + answer);

			((ShServerSubscriptionActivity) aci.getActivity()).sendSubscribeNotificationsAnswer(answer);

			if (event.getSubsReqType() == SubsReqType.SUBSCRIBE) {
				logger.info("Setting Timer for firing PNR in 15 seconds...");

				TimerOptions options = new TimerOptions();
				timerFacility.setTimer(aci, null, System.currentTimeMillis() + 3000, options);
			}
		} catch (Exception e) {
			logger.error("Failed to create/send SNA.", e);
		}
	}

	public void onProfileUpdateRequest(ProfileUpdateRequest event, ActivityContextInterface aci) {
		try {
			logger.info("onProfileUpdateRequest :: " + event);

			ProfileUpdateAnswer answer = ((ShServerActivity) aci.getActivity()).createProfileUpdateAnswer(2001, false);

			logger.info("Created Profile-Update-Answer:\r\n" + answer);

			((ShServerActivity) aci.getActivity()).sendProfileUpdateAnswer(answer);
		} catch (Exception e) {
			logger.error("Failed to create/send PUA.", e);
		}
	}

	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		logger.info(" Activity Ended[" + aci.getActivity() + "]");
	}
}
