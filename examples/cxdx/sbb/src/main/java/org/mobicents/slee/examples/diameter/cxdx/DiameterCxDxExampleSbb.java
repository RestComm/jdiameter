package org.mobicents.slee.examples.diameter.cxdx;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.nullactivity.NullActivity;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cxdx.CxDxClientSession;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.CxDxProvider;
import net.java.slee.resource.diameter.cxdx.CxDxServerSession;
import net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationAnswer;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.ServerAssignmentType;
import net.java.slee.resource.diameter.cxdx.events.avp.UserAuthorizationType;
import net.java.slee.resource.diameter.cxdx.events.avp.UserDataAlreadyAvailable;

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
	private String originIP = "127.0.0.1";
	private String originPort = "1812";
	private String originRealm = "mobicents.org";

	private String destinationIP = "127.0.0.1";
	private String destinationPort = "3868";
	private String destinationRealm = "mobicents.org";
	private Properties props = null;
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
				String propsMode = (String) getProperty("example.mode");
				boolean acAsServer = false;
				String mode = null;
				if(propsMode == null || propsMode.equals("server"))
				{
					acAsServer = true;
					mode = "Server";
				}else
				{
					mode = "Client";
				}
				
				logger.info("################################################################################");
				logger.info("##   D I A M E T E R   Cx/Dx   E X A M P L E               ::   S T A R T E D ##");
				logger.info("##                    A.4.3	UE initiated de-registration                       ##");
				logger.info("##                    Mode: "+mode+"                                            ##");
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
				
				if(!acAsServer)
				{
					TimerOptions options = new TimerOptions();

					timerFacility.setTimer(aci, null, System.currentTimeMillis() + 30000, options);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to handle service started event...", e);
		}
	}

	/////////////////
	// CLIENT SIDE //
	/////////////////
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		
		logger.info("Diameter Cx/Dx example: Acting as client(ICSCF after reguster)");
		this.timerFacility.cancelTimer(event.getTimerID());

		//We are client - ICSCF, we get REGISTER from UE, instead of this timer methods we should be in onRegister handler.
		DiameterIdentity destinationHost = new DiameterIdentity("aaa://" + getProperty("destination.ip") + ":" + getProperty("destination.port"));
		DiameterIdentity destinationRealm = new DiameterIdentity((String)getProperty("destination.realm"));
		CxDxClientSession clientSession;
		try {
		
			clientSession = this.provider.createCxDxClientSessionActivity(destinationHost, destinationRealm);
		
		ActivityContextInterface clientACI=this.acif.getActivityContextInterface(clientSession);
		clientACI.attach(this.sbbContext.getSbbLocalObject());
		
		UserAuthorizationRequest UAR = clientSession.createUserAuthorizationRequest();
//			< User-Authorization-Request> ::=			< Diameter Header: 300, REQ, PXY, 16777216 >
//			< Session-Id >
//			{ Vendor-Specific-Application-Id }
//			{ Auth-Session-State }
		UAR.setAuthSessionState(AuthSessionStateType.STATE_MAINTAINED);
//			{ Origin-Host }
//			{ Origin-Realm }
//			[ Destination-Host ]
//   		{ Destination-Realm }
		UAR.setUserName("sip:adam@example.domain.org");
//			{ User-Name }
//			*[ Supported-Features ]
//			{ Public-Identity }
		UAR.setPublicIdentity("sip:adam.b@travel.contanct.com");
//			{ Visited-Network-Identifier }
		UAR.setVisitedNetworkIdentifier("visit.airport.moscow.ru");
//			[ User-Authorization-Type ]
		UAR.setUserAuthorizationType(UserAuthorizationType.DE_REGISTRATION);
//			[ UAR-Flags ]
		
//			*[ AVP ]
//			*[ Proxy-Info ]
//			*[ Route-Record ]	
		
		//go
		logger.info("Diameter Cx/Dx example: Sending UAR:\n"+UAR);
		clientSession.sendUserAuthorizationRequest(UAR);
		logger.info("Diameter Cx/Dx example: Sent UAR:\n"+UAR);
		} catch (CreateActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void onUserAuthorizationAnswer(UserAuthorizationAnswer event, ActivityContextInterface aci)
	{
		logger.info("Diameter Cx/Dx example: receveid UAA:\n"+event);
		
		if(event.getResultCode()/1000!=2)
		{
			logger.info("Diameter Cx/Dx example: receveid UAA with wrong result code:\n"+event);
			return;
		}
		logger.info("Diameter Cx/Dx example: Acting as SCSCF after receiving REGISTER from ICSCF");
		//HSS responded to our call, here we would forward REGISTER to S-CSCF, however lets not do that, its easier to set up it like that
		//lets act as S-CSCF, which received REGISTER and sends SAR
		try{
		//Detach, this will die
		aci.detach(this.sbbContext.getSbbLocalObject());
		DiameterIdentity destinationHost = new DiameterIdentity("aaa://" + getProperty("destination.ip") + ":" + getProperty("destination.port"));
		DiameterIdentity destinationRealm = new DiameterIdentity((String)getProperty("destination.realm"));
		//create SAR/SAA activity
		CxDxClientSession clientSession=this.provider.createCxDxClientSessionActivity(destinationHost, destinationRealm);
		ActivityContextInterface clientACI=this.acif.getActivityContextInterface(clientSession);
		clientACI.attach(this.sbbContext.getSbbLocalObject());
		ServerAssignmentRequest SAR = clientSession.createServerAssignmentRequest();
		
		
//		<Server-Assignment-Request> ::=	< Diameter Header: 301, REQ, PXY, 16777216 >
//		< Session-Id >
//		{ Vendor-Specific-Application-Id }
//		{ Auth-Session-State }
		SAR.setAuthSessionState(AuthSessionStateType.STATE_MAINTAINED);
//		{ Origin-Host }
//		{ Origin-Realm }
//		[ Destination-Host ]
//		{ Destination-Realm }
//		[ User-Name ]
		SAR.setUserName("sip:adam@example.domain.org");
//		*[ Supported-Features ]
//		*[ Public-Identity ]
		SAR.setPublicIdentity("sip:adam.b@travel.contanct.com");
//		[ Wildcarded-PSI ]
//		[ Wildcarded-IMPU ]
//		{ Server-Name }
		SAR.setServerName("x123.s.cscf.local.domain.org");
//		{ Server-Assignment-Type }
		SAR.setServerAssignmentType(ServerAssignmentType.USER_DEREGISTRATION);
//		{ User-Data-Already-Available }
		SAR.setUserDataAlreadyAvailable(UserDataAlreadyAvailable.USER_DATA_NOT_AVAILABLE);
//		[ SCSCF-Restoration-Info ]
//		[ Multiple-Registration-Indication ]
//		*[ AVP ]
//		*[ Proxy-Info ]
//		*[ Route-Record ]
		
		//GO.
		logger.info("Diameter Cx/Dx example: Sending SAR:\n"+SAR);
		clientSession.sendServerAssignmentRequest(SAR);
		logger.info("Diameter Cx/Dx example: Sent SAR:\n"+SAR);
		} catch (CreateActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void onServerAssignmentAnswer(ServerAssignmentAnswer event, ActivityContextInterface aci)
	{
		
		//HERE HSS sent response, now we should send 200 to ICSCF, it should forward it back to UE, but we dont, its Cx/Dx example :)
	}
	
	/////////////////
	// SERVER SIDE //
	/////////////////
	public void onServerAssignmentRequest(ServerAssignmentRequest event, ActivityContextInterface aci)
	{
		logger.info("Diameter Cx/Dx example: received SAR:\n"+event);
		
		//we simply agree, doh
		CxDxServerSession serverSession = (CxDxServerSession) aci.getActivity();
		ServerAssignmentAnswer SAA = serverSession.createServerAssignmentAnswer();
//		<Server-Assignment-Answer> ::=	< Diameter Header: 301, PXY, 16777216 >
//		< Session-Id >
//		{ Vendor-Specific-Application-Id }
//		[ Result-Code ]
		SAA.setResultCode(2001);
//  	[ Experimental-Result ]
//	    { Auth-Session-State }
		SAA.setAuthSessionState(event.getAuthSessionState());
//		{ Origin-Host }
//		{ Origin-Realm }
//		[ User-Name ]
//		*[ Supported-Features ]
//		[ User-Data ]
//		[ Charging-Information ]
//		[ Associated-Identities ]
//		[ Loose-Route-Indication ]
//		*[ SCSCF-Restoration-Info ]
//		[ Associated-Registered-Identities ]
//		[ Server-Name ]
//		*[ AVP ]
//		*[ Failed-AVP ]
//		*[ Proxy-Info ]
//		*[ Route-Record ]
		
		//GO
		logger.info("Diameter Cx/Dx example: sending SAA:\n"+SAA);
		try {
			serverSession.sendServerAssignmentAnswer(SAA);
			logger.info("Diameter Cx/Dx example: sent SAA:\n"+SAA);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aci.detach(this.sbbContext.getSbbLocalObject());
		

	}
	public void onUserAuthorizationRequest(UserAuthorizationRequest event, ActivityContextInterface aci)
	{
		logger.info("Diameter Cx/Dx example: received UAR:\n"+event);
		CxDxServerSession serverSession = (CxDxServerSession) aci.getActivity();
		UserAuthorizationAnswer UAA = serverSession.createUserAuthorizationAnswer();
		
//		< User-Authorization-Answer> ::=			< Diameter Header: 300, PXY, 16777216 >
//		< Session-Id >
//		{ Vendor-Specific-Application-Id }
//		[ Result-Code ]
		UAA.setResultCode(2001);
//		[ Experimental-Result ]
//		{ Auth-Session-State }
		UAA.setAuthSessionState(event.getAuthSessionState());
//		{ Origin-Host }
//		{ Origin-Realm }
//		*[ Supported-Features ]
//		[ Server-Name ]
//		[ Server-Capabilities ]
//		[ Wildcarded-IMPU ]
//		*[ AVP ]
//		*[ Failed-AVP ]
//		*[ Proxy-Info ]
//		*[ Route-Record ]
		//GO
		logger.info("Diameter Cx/Dx example: sedning UAA:\n"+UAA);
		try {
			serverSession.sendUserAuthorizationAnswer(UAA);
			logger.info("Diameter Cx/Dx example: sent UAA:\n"+UAA);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aci.detach(this.sbbContext.getSbbLocalObject());
		
	}
	
	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		logger.info(" Activity Ended[" + aci.getActivity() + "]");
	}
	
	private Object getProperty(String name)
	{
		if(props == null)
		{
			props = new Properties();
			try {
				props.load(this.getClass().getClassLoader().getResourceAsStream("example.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return props.getProperty(name);
	}
}
