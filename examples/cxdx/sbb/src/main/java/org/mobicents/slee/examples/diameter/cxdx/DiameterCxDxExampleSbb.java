package org.mobicents.slee.examples.diameter.cxdx;

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
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cxdx.CxDxClientSession;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.CxDxProvider;
import net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;

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
public abstract class DiameterCxDxExampleSbb implements javax.slee.Sbb {

	private static Logger logger = Logger.getLogger(DiameterCxDxExampleSbb.class);

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private CxDxProvider provider = null;

	private CxDxMessageFactory messageFactory = null;
	private CxDxAVPFactory avpFactory = null;
	private CxDxActivityContextInterfaceFactory acif = null;
	private TimerFacility timerFacility = null;

	public void setSbbContext(SbbContext context) {
		logger.info("sbbRolledBack invoked.");

		this.sbbContext = context;

		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");

			provider = (CxDxProvider) myEnv.lookup("slee/resources/diameter-cx-dx-ra-interface");
			logger.info("Got Provider:" + provider);

			messageFactory = provider.getCxDxMessageFactory();
			logger.info("Got Message Factory:" + messageFactory);

			avpFactory = provider.getCxDxAVPFactory();
			logger.info("Got AVP Factory:" + avpFactory);

			acif = (CxDxActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterCxDxResourceAdaptor/java.net/0.8.1/acif");

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
				logger.info("##   D I A M E T E R   Cx/Dx   E X A M P L E               ::   S T A R T E D ##");
				logger.info("################################################################################");

				messageFactory = provider.getCxDxMessageFactory();
				avpFactory = provider.getCxDxAVPFactory();

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

	}

	public void onPushProfileRequest(PushProfileRequest event, ActivityContextInterface aci) {
		CxDxClientSession activity = (CxDxClientSession) aci.getActivity();
		PushProfileAnswer answer = activity.createPushProfileAnswer();
		answer.setResultCode(2000);
		if(!answer.hasAuthSessionState())
		{
			answer.setAuthSessionState(event.getAuthSessionState());
		}
		
		//answer.setOriginHost(new DiameterIdentity("aaa://127.0.0.1:3868"));
		//answer.setOriginRealm(new DiameterIdentity("mobicents.org"));	
		
		try {
			activity.sendPushProfileAnswer(answer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onRegistrationTerminationRequest(RegistrationTerminationRequest event, ActivityContextInterface aci) {
		CxDxClientSession activity = (CxDxClientSession) aci.getActivity();
		RegistrationTerminationAnswer answer = activity.createRegistrationTerminationAnswer();
		answer.setResultCode(2000);
		if(!answer.hasAuthSessionState())
		{
			answer.setAuthSessionState(event.getAuthSessionState());
		}

		//answer.setOriginHost(new DiameterIdentity("aaa://127.0.0.1:3868"));
		//answer.setOriginRealm(new DiameterIdentity("mobicents.org"));	
		try {
			activity.sendRegistrationTerminationAnswer(answer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		logger.info(" Activity Ended[" + aci.getActivity() + "]");
	}
}
