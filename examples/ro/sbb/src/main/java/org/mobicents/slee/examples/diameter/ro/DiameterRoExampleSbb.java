package org.mobicents.slee.examples.diameter.ro;

import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.SipUri;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.FactoryException;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.ro.RoActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.ro.RoAvpFactory;
import net.java.slee.resource.diameter.ro.RoClientSession;
import net.java.slee.resource.diameter.ro.RoMessageFactory;
import net.java.slee.resource.diameter.ro.RoProvider;
import net.java.slee.resource.sip.DialogActivity;
import net.java.slee.resource.sip.SipActivityContextInterfaceFactory;
import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;


/**
 * 
 * DiameterRfExampleSbb
 * 
 * <br>
 * Super project: mobicents <br>
 * 10:58:03 PM Dec 19, 2008 <br>
 * 
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class DiameterRoExampleSbb implements javax.slee.Sbb {

	private static Logger logger = Logger.getLogger(DiameterRoExampleSbb.class);
	private static long _FIRST_CHARGE_TIME = 10000;
	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	// DIAMETER RA
	private RoProvider provider;
	private RoMessageFactory roMessageFactory;
	private RoAvpFactory avpFactory;
	private RoActivityContextInterfaceFactory acif;
	
	//SIP RA
	private MessageFactory messageFactory;
	private SleeSipProvider sipProvider;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	private SipActivityContextInterfaceFactory sipAcif;
	
	private TimerFacility timerFacility = null;

	private static String originIP = "127.0.0.1";
	private static String originPort = "1812";
	private static String originRealm = "mobicents.org";

	private static String destinationIP = "127.0.0.1";
	private static String destinationPort = "3868";
	private static String destinationRealm = "mobicents.org";

	private NullActivityFactory nullActivityFactory;

	private NullActivityContextInterfaceFactory nullACIFactory;

	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;

		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");

			// Diameter Sh Stuff
			provider = (RoProvider) myEnv.lookup("slee/resources/diameter-ro-ra-interface");

			roMessageFactory = provider.getRoMessageFactory();
			avpFactory = provider.getRoAvpFactory();

			acif = (RoActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterRoResourceAdaptor/java.net/0.8.1/acif");

			// SLEE Facilities
			timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
			nullActivityFactory = (NullActivityFactory) myEnv.lookup("slee/nullactivity/factory");
			nullACIFactory = (NullActivityContextInterfaceFactory) myEnv.lookup("slee/nullactivity/activitycontextinterfacefactory");

			sipProvider = (SleeSipProvider) myEnv.lookup("slee/resources/jainsip/1.2/provider");
            addressFactory = sipProvider.getAddressFactory();
            headerFactory = sipProvider.getHeaderFactory();
            messageFactory = sipProvider.getMessageFactory(); 
            sipAcif = (SipActivityContextInterfaceFactory)myEnv.lookup("slee/resources/jainsip/1.2/acifactory");
			
		} catch (Exception e) {
			logger.error("Unable to set sbb context.", e);
		}
	}

	public void unsetSbbContext() {
		logger.info("unsetSbbContext invoked.");

		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
		logger.info("sbbCreate invoked.");
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
		logger.info("sbbPostCreate invoked.");
	}

	public void sbbActivate() {
		logger.info("sbbActivate invoked.");
	}

	public void sbbPassivate() {
		logger.info("sbbPassivate invoked.");
	}

	public void sbbRemove() {
		logger.info("sbbRemove invoked.");
	}

	public void sbbLoad() {
		logger.info("sbbLoad invoked.");
	}

	public void sbbStore() {
		logger.info("sbbStore invoked.");
	}

	public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {
		logger.info("sbbRolledBack invoked.");
	}

	public void sbbRolledBack(RolledBackContext context) {
		logger.info("sbbRolledBack invoked.");
	}

	protected SbbContext getSbbContext() {
		logger.info("getSbbContext invoked.");

		return sbbContext;
	}

	/**
	 * Generate a custom convergence name so that related events (with the same
	 * call identifier, or session) will go to the same root SBB entity.
	 */
	public InitialEventSelector myInitialEventSelector(InitialEventSelector ies) {

		return ies;
	}

  // ##########################################################################
  // ##                          EVENT HANDLERS                              ##
  // ##########################################################################

	public void onServiceStartedEvent(javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci) {
		try {

			// check if it's my service that is starting
			ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();
			if (sa.equals(aci.getActivity())) {
				Properties props = new Properties();
				props.load(this.getClass().getClassLoader().getResourceAsStream("example.properties"));

				originIP = props.getProperty("origin.ip") == null ? originIP : props.getProperty("origin.ip");
				originPort = props.getProperty("origin.port") == null ? originPort : props.getProperty("origin.port");
				originRealm = props.getProperty("origin.realm") == null ? originRealm : props.getProperty("origin.realm");

				destinationIP = props.getProperty("destination.ip") == null ? destinationIP : props.getProperty("destination.ip");
				destinationPort = props.getProperty("destination.port") == null ? destinationPort : props.getProperty("destination.port");
				destinationRealm = props.getProperty("destination.realm") == null ? destinationRealm : props.getProperty("destination.realm");
				
				logger.info("################################################################################");
				logger.info("###     Ro    E X A M P L E    A P P L I C A T I O N  :: S T A R T E D       ##");
				//logger.info("###      Mode: " + exampleMode + "    Acc Type: " + chargingMode + "                 ##");
				logger.info("################################################################################");

				roMessageFactory = provider.getRoMessageFactory();
				avpFactory = provider.getRoAvpFactory();

				logger.info("Performing sanity check...");
				logger.info("Provider [" + provider + "]");
				logger.info("Message Factory [" + roMessageFactory + "]");
				logger.info("AVP Factory [" + avpFactory + "]");
				logger.info("Check completed. Result: "
						+ ((provider != null ? 1 : 0) + (roMessageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3");

				logger.info("Connected to " + provider.getPeerCount() + " peers.");

				for (DiameterIdentity peer : provider.getConnectedPeers()) {
					logger.info("Connected to Peer[" + peer.toString() + "]");
				}

			}
		} catch (Exception e) {
			logger.error("Unable to handle service started event...", e);
		}
	}

	private void startTimer(long grantedTime) {

		try {
			NullActivity timerBus = this.nullActivityFactory.createNullActivity();

			ActivityContextInterface timerBusACI = this.nullACIFactory.getActivityContextInterface(timerBus);

			timerBusACI.attach(sbbContext.getSbbLocalObject());

			TimerOptions options = new TimerOptions();

			timerFacility.setTimer(timerBusACI, null, System.currentTimeMillis() + grantedTime, options);
		} catch (TransactionRequiredLocalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecognizedActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		logger.info(" Activity Ended[" + aci.getActivity() + "]");
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		RoClientSession roSession = getRoSession();
		CreditControlRequest ccr = roSession.createCreditControlRequest();
		DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
		DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
		ccr.setDestinationHost(destHost);
		ccr.setDestinationRealm(destRealm);
		ccr.setCcRequestNumber(1);
		ccr.setCcRequestType(CcRequestType.UPDATE_REQUEST);
		//ccr.setEventTimestamp(Calendar.getInstance().getTime());
		//Nto sure about this.
		//ccr.setRequestedAction(RequestedActionType.DIRECT_DEBITING);
		RequestedServiceUnitAvp RSU = avpFactory.createRequestedServiceUnit();
		//after initial charge we have some free time :)
		RSU.setCreditControlTime(_FIRST_CHARGE_TIME);
		ccr.setRequestedServiceUnit(RSU);
		
		UsedServiceUnitAvp USU = avpFactory.createUsedServiceUnit();
		USU.setCreditControlTime(getGrantedServiceTime());
		ccr.setUsedServiceUnit(USU);
		//?
		ccr.setAuthApplicationId(4);
		
		ccr.setServiceContextId("MOBICENTS");
		
		String name = getUserName();
		ccr.setUserName(name);
		try {
			roSession.sendCreditControlRequest(ccr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Sent UPDATE CCR: \n"+ccr);
	}

	  // ##########################################################################
	  // ##                      SIP EVENT HANDLERS                              ##
	  // ##########################################################################
	public void onInviteEvent(javax.sip.RequestEvent requestEvent, ActivityContextInterface aci) {
		try {
			ServerTransaction serverTransaction = requestEvent.getServerTransaction();
			// send 100
			//Here user calls, we propably would create second leg, and after testing avaialble credits, init second leg
			//and start charging from that point, but we are poor server, we have only one leg :)
			DialogActivity dialog = (DialogActivity) sipProvider.getNewDialog(serverTransaction);
			dialog.terminateOnBye(true);
			sipAcif.getActivityContextInterface(dialog).attach(this.sbbContext.getSbbLocalObject());
			// send 200 ok
			Response response = messageFactory
					.createResponse(Response.TRYING,requestEvent.getRequest());
			serverTransaction.sendResponse(response);
			DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
			DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
			RoClientSession roSession = provider.createRoClientSessionActivity(destHost, destRealm);
			ActivityContextInterface roACI = acif.getActivityContextInterface(roSession);
			roACI.attach(getSbbContext().getSbbLocalObject());
			
			
			//See TS 32.299 section 6.6 for bindings....
			CreditControlRequest ccr = roSession.createCreditControlRequest();
			ccr.setDestinationHost(destHost);
			ccr.setDestinationRealm(destRealm);
			ccr.setCcRequestNumber(0);
			ccr.setCcRequestType(CcRequestType.INITIAL_REQUEST);
			//ccr.setEventTimestamp(Calendar.getInstance().getTime());
			//Nto sure about this.
			//ccr.setRequestedAction(RequestedActionType.DIRECT_DEBITING);
			RequestedServiceUnitAvp RSU = avpFactory.createRequestedServiceUnit();
			//after initial charge we have some free time :)
			RSU.setCreditControlTime(_FIRST_CHARGE_TIME);
			ccr.setRequestedServiceUnit(RSU);
			//?
			ccr.setAuthApplicationId(4);
			
			ccr.setServiceContextId("MOBICENTS");
			FromHeader fromHeader = (FromHeader) requestEvent.getRequest().getHeader(FromHeader.NAME);
			String name = null;
			name = ((SipUri)fromHeader.getAddress().getURI()).getUser();
			ccr.setUserName(name);
			setUserName(name);
			roSession.sendCreditControlRequest(ccr);
			logger.info("Sent INITIAL CCR: \n"+ccr);
			//Operation Token
			//Operation Number
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void onAckEvent(RequestEvent requestEvent, ActivityContextInterface aci) {
	
		logger.info("Received ACK");
	}
	public void onByeEvent(RequestEvent requestEvent, ActivityContextInterface aci) {
		
		logger.info("Received BYE");

		try {
			Response response = messageFactory.createResponse(Response.OK, requestEvent.getServerTransaction().getRequest());
			response.addHeader(getContactHeader());
			((ToHeader) response.getHeader(ToHeader.NAME)).setTag(Utils.getInstance().generateTag());
			requestEvent.getServerTransaction().sendResponse(response);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		RoClientSession roSession = getRoSession();
		CreditControlRequest ccr = roSession.createCreditControlRequest();
		DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
		DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
		ccr.setDestinationHost(destHost);
		ccr.setDestinationRealm(destRealm);
		ccr.setCcRequestNumber(2);
		ccr.setCcRequestType(CcRequestType.TERMINATION_REQUEST);
		//ccr.setEventTimestamp(Calendar.getInstance().getTime());
		//Nto sure about this.
		//ccr.setRequestedAction(RequestedActionType.DIRECT_DEBITING);
		
		UsedServiceUnitAvp USU = avpFactory.createUsedServiceUnit();
		USU.setCreditControlTime(getGrantedServiceTime());
		ccr.setUsedServiceUnit(USU);
		//?
		ccr.setAuthApplicationId(4);
		
		ccr.setServiceContextId("MOBICENTS");
		
		String name = getUserName();
		ccr.setUserName(name);
		try {
			roSession.sendCreditControlRequest(ccr);
			logger.info("Sent TERMINATION CCR: \n"+ccr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//And thats it :)
	}
	  // ##########################################################################
	  // ##                   DIAMETER EVENT HANDLERS                            ##
	  // ##########################################################################
	public void onCreditControlRequest(CreditControlRequest ccr, ActivityContextInterface aci) {
		
	}

	public void onCreditControlAnswer(CreditControlAnswer cca, ActivityContextInterface aci) {
		RoClientSession roSession = (RoClientSession) aci.getActivity();
		if (cca.getResultCode() / 1000 == 2) {
			// ok
			switch(cca.getCcRequestType())
			{
			case INITIAL_REQUEST:
				
				GrantedServiceUnitAvp GSU = cca.getGrantedServiceUnit();
				long grantedTime = GSU.getCreditControlTime();
				setGrantedServiceTime(grantedTime);
				ServerTransaction stx = getStxSession();
				
				if (grantedTime > 0) {
					
					try {
						Response response = messageFactory.createResponse(Response.OK, stx.getRequest());
						response.addHeader(getContactHeader());
						((ToHeader) response.getHeader(ToHeader.NAME)).setTag(Utils.getInstance().generateTag());
						stx.sendResponse(response);
						startTimer(grantedTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else {
					try {
						sendResponse(stx, Response.DECLINE, "Failed, no time received from server");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				
				break;
			case UPDATE_REQUEST:
				
				//actually CC systems would CCR each time propably - to keep record, than biling subsystem would cut depending on criteria(user dependant
				//as in life). We stop after first update.
				break;
			case TERMINATION_REQUEST:
				break;
			}
			
		} else {
			try {
				sendResponse(getStxSession(), 500, "Failed CCR");
			} catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	 private ContactHeader getContactHeader() throws ParseException {
	    	
	    		ListeningPoint listeningPoint = sipProvider
				.getListeningPoint("udp");
				Address address = addressFactory.createAddress(
						"Mobicents SIP AS <sip:"+listeningPoint.getIPAddress()+">");
				((SipURI) address.getURI()).setPort(listeningPoint.getPort());
				ContactHeader contactHeader = headerFactory.createContactHeader(address);
	    	
	    	return contactHeader;
	    }
  // ##########################################################################
  // ##                           PRIVATE METHODS                            ##
  // ##########################################################################
   
	private void sendResponse(ServerTransaction tx, int responseCode, String body) throws SipException, InvalidArgumentException, ParseException {
		final Request request  = tx.getRequest();
		
		Response response = null;
		if(body!=null)
		{
			ContentTypeHeader cth = this.headerFactory.createContentTypeHeader("plain", "text");
			response = messageFactory.createResponse(responseCode, request,cth,body.getBytes());
		}else
		{
			response = messageFactory.createResponse(responseCode, request);
		}
		tx.sendResponse(response);
	}

	
	private RoClientSession getRoSession()
	{
		ActivityContextInterface[] acis = getSbbContext().getActivities();
		for(ActivityContextInterface aci:acis)
		{
			if(aci.getActivity() instanceof RoClientSession)
			{
				return (RoClientSession)aci.getActivity();
			}
		}
		return null;
	}

	
	private ServerTransaction getStxSession()
	{
		ActivityContextInterface[] acis = getSbbContext().getActivities();
		for(ActivityContextInterface aci:acis)
		{
			if(aci.getActivity() instanceof ServerTransaction)
			{
				return (ServerTransaction)aci.getActivity();
			}
		}
		return null;
	}

	public abstract void setGrantedServiceTime(long GSU);
	public abstract long getGrantedServiceTime();
	public abstract void setUserName(String GSU);
	public abstract String getUserName();
	
}
