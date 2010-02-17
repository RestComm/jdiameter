package org.mobicents.slee.examples.diameter.rf;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
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

import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.rf.RfActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.rf.RfAvpFactory;
import net.java.slee.resource.diameter.rf.RfClientSession;
import net.java.slee.resource.diameter.rf.RfMessageFactory;
import net.java.slee.resource.diameter.rf.RfProvider;
import net.java.slee.resource.diameter.rf.RfServerSession;
import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.mobicents.slee.examples.diameter.ro.DiameterRoExampleSbb;

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
public abstract class DiameterRfExampleSbb implements javax.slee.Sbb {

	private static Logger logger = Logger.getLogger(DiameterRoExampleSbb.class);

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	// DIAMETER RA
	private RfProvider provider = null;
	private RfMessageFactory rfMessageFactory = null;
	private RfAvpFactory avpFactory = null;
	private RfActivityContextInterfaceFactory acif = null;
	
	//SIP RA
	private MessageFactory messageFactory;
	private SleeSipProvider sipProvider;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	
	private TimerFacility timerFacility = null;

	private static String originIP = "127.0.0.1";
	private static String originPort = "1812";
	private static String originRealm = "mobicents.org";

	private static String destinationIP = "127.0.0.1";
	private static String destinationPort = "3868";
	private static String destinationRealm = "mobicents.org";

	private static ExampleMode exampleMode = ExampleMode.Client;
	private static ChargingMode chargingMode = ChargingMode.Event;

	private NullActivityFactory nullActivityFactory;

	private NullActivityContextInterfaceFactory nullACIFactory;

	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;

		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");

			// Diameter Sh Stuff
			provider = (RfProvider) myEnv.lookup("slee/resources/diameter-rf-ra-interface");

			rfMessageFactory = provider.getRfMessageFactory();
			avpFactory = provider.getRfAvpFactory();

			acif = (RfActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterRfResourceAdaptor/java.net/0.8.1/acif");

			// SLEE Facilities
			timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
			nullActivityFactory = (NullActivityFactory) myEnv.lookup("slee/nullactivity/factory");
			nullACIFactory = (NullActivityContextInterfaceFactory) myEnv.lookup("slee/nullactivity/activitycontextinterfacefactory");

			sipProvider = (SleeSipProvider) myEnv.lookup("slee/resources/jainsip/1.2/provider");
            addressFactory = sipProvider.getAddressFactory();
            headerFactory = sipProvider.getHeaderFactory();
            messageFactory = sipProvider.getMessageFactory(); 
			
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
				exampleMode = ExampleMode.Client.fromString(props.getProperty("example.mode", exampleMode.name().toLowerCase()));
				chargingMode = ChargingMode.Event.fromString(props.getProperty("charging.mode", chargingMode.name().toLowerCase()));
				logger.info("################################################################################");
				logger.info("###     RF    E X A M P L E    A P P L I C A T I O N  :: S T A R T E D       ##");
				logger.info("###      Mode: " + exampleMode + "    Acc Type: " + chargingMode + "                 ##");
				logger.info("################################################################################");

				rfMessageFactory = provider.getRfMessageFactory();
				avpFactory = provider.getRfAvpFactory();

				logger.info("Performing sanity check...");
				logger.info("Provider [" + provider + "]");
				logger.info("Message Factory [" + rfMessageFactory + "]");
				logger.info("AVP Factory [" + avpFactory + "]");
				logger.info("Check completed. Result: "
						+ ((provider != null ? 1 : 0) + (rfMessageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3");

				logger.info("Connected to " + provider.getPeerCount() + " peers.");

				for (DiameterIdentity peer : provider.getConnectedPeers()) {
					logger.info("Connected to Peer[" + peer.toString() + "]");
				}

				startTimer();
			}
		} catch (Exception e) {
			logger.error("Unable to handle service started event...", e);
		}
	}

	private void startTimer() {

		try {
			NullActivity timerBus = this.nullActivityFactory.createNullActivity();

			ActivityContextInterface timerBusACI = this.nullACIFactory.getActivityContextInterface(timerBus);

			timerBusACI.attach(sbbContext.getSbbLocalObject());

			TimerOptions options = new TimerOptions();

			timerFacility.setTimer(timerBusACI, null, System.currentTimeMillis() + 5000, options);
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
		try {
			aci.detach(this.getSbbContext().getSbbLocalObject());
			if(getExampleMode()==null)
			{
				this.setExampleMode(exampleMode);
				this.setChargingMode(chargingMode);
			}
			switch (getExampleMode()) {
			case Client:
				switch (getChargingMode()) {
				case Event:
					sendEvenCharge("dummy");
					break;
				case Session:
					if (getLastRecordType() == null || getLastRecordType() == AccountingRecordType.STOP_RECORD) {
						setLastRecordType(null);
						setRequestNumber(0);
						sendSessionCharge();
					} else if (getLastRecordType() == AccountingRecordType.START_RECORD) {
						sendInterim(getRequestNumber());
					} else if (getLastRecordType() == AccountingRecordType.INTERIM_RECORD) {
						sendStop(getRequestNumber());
					}
					break;
				}
				break;
			case Server:
				logger.info("Example is in server mode, awaiting messages.");
				break;
			}

		} catch (Exception e) {
			logger.error("Failure reading properties file.", e);
		}
	}

	  // ##########################################################################
	  // ##                      SIP EVENT HANDLERS                              ##
	  // ##########################################################################
	
	
	private static final String FIRST_TOKEN = "Send SMS:";
	private static final String MIDDLE_TOKEN = ". To:";
	private static final String LAST_TOKEN = "!";
	private static final int FIRST_TOKEN_LENGTH = FIRST_TOKEN.length();
	private static final int MIDDLE_TOKEN_LENGTH = MIDDLE_TOKEN.length();

	public void onMessageEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {

		this.setSipEnabled(true);
		//this overrides general setting
		this.setChargingMode(ChargingMode.Event);
		this.setExampleMode(ExampleMode.Client);
		final Request request = event.getRequest();
		try {
			// message body should be *FIRST_TOKEN<timer value in
			// seconds>MIDDLE_TOKEN<msg to send back to UA>LAST_TOKEN*
			final String body = new String(request.getRawContent());
			final int firstTokenStart = body.indexOf(FIRST_TOKEN);
			final int msgPartStart = firstTokenStart + FIRST_TOKEN_LENGTH;
			final int middleTokenStart = body.indexOf(MIDDLE_TOKEN, msgPartStart);
			final int addressStart = middleTokenStart + MIDDLE_TOKEN_LENGTH;
			final int lastTokenStart = body.indexOf(LAST_TOKEN, addressStart);
			String msg = body.substring(msgPartStart, middleTokenStart);
			String address = body.substring(addressStart, lastTokenStart);
			//here we would send to SMS gateway, but since its not here, just charge poor guy :)

			setMessage(msg);
			setAddress(address);
			FromHeader fromHeader = (FromHeader) event.getRequest().getHeader(FromHeader.NAME);
			String userName=((SipURI)fromHeader.getAddress().getURI()).getUser();
			
			sendEvenCharge(userName);
			

		} catch (Throwable e) {
			// oh oh something wrong happened
			logger.error("Exception while processing MESSAGE", e);
			try {
				sendResponse(event.getServerTransaction(), Response.SERVER_INTERNAL_ERROR,e.getMessage());
			} catch (Exception f) {
				logger.error("Exception while sending SERVER INTERNAL ERROR", f);
			}
		}
	}
	  // ##########################################################################
	  // ##                   DIAMETER EVENT HANDLERS                            ##
	  // ##########################################################################
	public void onAccountingRequest(net.java.slee.resource.diameter.base.events.AccountingRequest acr, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Accounting-Request received.\n" + acr);

		RfServerSession session = (RfServerSession) aci.getActivity();
		//duno, lets just reply;
		AccountingAnswer aca=session.createRfAccountingAnswer(acr);
		aca.setResultCode(2001);
		setExampleMode(ExampleMode.Server);
		try {
			session.sendAccountAnswer(aca);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isInfoEnabled())
			logger.info("Accounting-Answer send.\n" + aca);
	}
	public void onAccountingAnswer(net.java.slee.resource.diameter.base.events.AccountingAnswer aca, ActivityContextInterface aci) {
		if (logger.isInfoEnabled())
			logger.info("Accounting-Answer received.\n" + aca);

		switch (exampleMode) {
		case Client:
			switch (chargingMode) {
			case Event:
				if(!getSipEnabled())
				{
					logger.info("Received ACA for Client.Event charging, setting timer once again.");
					startTimer();
				}else
				{
					logger.info("Received ACA for Client.Event charging for sip enabled charge.");
					logger.info("Sending SMS message: "+getMessage()+"\nRecipent: "+getAddress());
					// finally reply to the SIP message request
					try {
						sendResponse(getStxSession(), Response.OK,"SMS sent");
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
				break;
			case Session:
				if(aca.getAccountingRecordType()!=AccountingRecordType.STOP_RECORD)
				{
					logger.info("Received ACA for Client.Session charging, setting timer once again.");
					startTimer();
					this.setRequestNumber(aca.getAccountingRecordNumber());
				}else
				{
					logger.info("Received ACA for Client.Session charging, with STOP_RECORD, starting timer again.");
					startTimer();
				}
				break;
			}
			break;
		case Server:
			logger.info("Received ACA in server mode.");
			break;
		}

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

	private void sendSessionCharge() {
		try {
			DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
			DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
			RfClientSession session = provider.createRfClientSessionActivity(destHost, destRealm);
			ActivityContextInterface aci = this.acif.getActivityContextInterface(session);
			aci.attach(this.getSbbContext().getSbbLocalObject());
			AccountingRequest eventRequest = this.rfMessageFactory.createRfAccountingRequest(AccountingRecordType.START_RECORD);
			setLastRecordType(AccountingRecordType.START_RECORD);
			// set session id, it generic factory, so it does not fill it.
			eventRequest.setSessionId(session.getSessionId());

			eventRequest.setDestinationHost(destHost);
			eventRequest.setDestinationRealm(destRealm);
			
			// ACC_RECORD_NUMBER = 485
			eventRequest.setAccountingRecordNumber(0);

			eventRequest.setUserName("bobmasters");

			DiameterAvp eventTimestamp = avpFactory.getCreditControlAVPFactory().getBaseFactory().createAvp(55, new Date());
			eventRequest.setExtensionAvps(eventTimestamp);
			logger.info("Sending session START ACR:\n" + eventRequest);
			setRequestNumber(0);
			session.sendAccountingRequest(eventRequest);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	private void sendInterim(long lastRequestNumber) {
		try {
			setRequestNumber(lastRequestNumber+1);
			DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
			DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
			RfClientSession session = getRfSession();
			ActivityContextInterface aci = this.acif.getActivityContextInterface(session);
			aci.attach(this.getSbbContext().getSbbLocalObject());
			AccountingRequest eventRequest = this.rfMessageFactory.createRfAccountingRequest(AccountingRecordType.INTERIM_RECORD);
			setLastRecordType(AccountingRecordType.INTERIM_RECORD);
			// set session id, it generic factory, so it does not fill it.
			eventRequest.setSessionId(session.getSessionId());

			eventRequest.setDestinationHost(destHost);
			eventRequest.setDestinationRealm(destRealm);

			// ACC_RECORD_NUMBER = 485
			eventRequest.setAccountingRecordNumber(lastRequestNumber+1);

			eventRequest.setUserName("bobmasters");

			DiameterAvp eventTimestamp = avpFactory.getCreditControlAVPFactory().getBaseFactory().createAvp(55, new Date());
			eventRequest.setExtensionAvps(eventTimestamp);
			logger.info("Sending INTERIM ACR:\n" + eventRequest);
			session.sendAccountingRequest(eventRequest);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void sendStop(long lastRequestNumber) {
		try {
		
			DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
			DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
			RfClientSession session = getRfSession();
			ActivityContextInterface aci = this.acif.getActivityContextInterface(session);
			aci.attach(this.getSbbContext().getSbbLocalObject());
			AccountingRequest eventRequest = this.rfMessageFactory.createRfAccountingRequest(AccountingRecordType.STOP_RECORD);
			setLastRecordType(AccountingRecordType.STOP_RECORD);
			// set session id, it generic factory, so it does not fill it.
			eventRequest.setSessionId(session.getSessionId());

			eventRequest.setDestinationHost(destHost);
			eventRequest.setDestinationRealm(destRealm);

			// ACC_RECORD_NUMBER = 485
			eventRequest.setAccountingRecordNumber(lastRequestNumber+1);

			eventRequest.setUserName("bobmasters");

			DiameterAvp eventTimestamp = avpFactory.getCreditControlAVPFactory().getBaseFactory().createAvp(55, new Date());
			eventRequest.setExtensionAvps(eventTimestamp);
			logger.info("Sending STOP ACR:\n" + eventRequest);
			session.sendAccountingRequest(eventRequest);
			setRequestNumber(0);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	private void sendEvenCharge(String userName) {

		try {
			DiameterIdentity destRealm = new DiameterIdentity(destinationRealm);
			DiameterIdentity destHost = new DiameterIdentity("aaa://" + destinationIP + ":" + destinationPort);
			RfClientSession session = provider.createRfClientSessionActivity(destHost, destRealm);
			ActivityContextInterface aci = this.acif.getActivityContextInterface(session);
			aci.attach(this.getSbbContext().getSbbLocalObject());
			AccountingRequest eventRequest = this.rfMessageFactory.createRfAccountingRequest(AccountingRecordType.EVENT_RECORD);
			// set session id, it generic factory, so it does not fill it.
			eventRequest.setSessionId(session.getSessionId());

			eventRequest.setDestinationHost(destHost);
			eventRequest.setDestinationRealm(destRealm);

			// ACC_RECORD_NUMBER = 485
			eventRequest.setAccountingRecordNumber(0);
			
			eventRequest.setUserName(userName);

			DiameterAvp eventTimestamp = avpFactory.getCreditControlAVPFactory().getBaseFactory().createAvp(55, new Date());
			eventRequest.setExtensionAvps(eventTimestamp);
			logger.info("Sending ACR:\n" + eventRequest);

			session.sendAccountingRequest(eventRequest);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private RfClientSession getRfSession()
	{
		ActivityContextInterface[] acis = getSbbContext().getActivities();
		for(ActivityContextInterface aci:acis)
		{
			if(aci.getActivity() instanceof RfClientSession)
			{
				return (RfClientSession)aci.getActivity();
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
	public abstract void setAddress(String mode);
	public abstract String getAddress();
	public abstract void setMessage(String mode);
	public abstract String getMessage();
	public abstract void setExampleMode(ExampleMode mode);
	public abstract ExampleMode getExampleMode();
	public abstract void setChargingMode(ChargingMode mode);
	public abstract ChargingMode getChargingMode();
	public abstract void setSipEnabled(boolean b);

	public abstract boolean getSipEnabled();

	public abstract void setLastRecordType(AccountingRecordType l);

	public abstract AccountingRecordType getLastRecordType();

	public abstract void setRequestNumber(long l);

	public abstract long getRequestNumber();

//	enum ExampleMode {
//		Server, Client;
//		public ExampleMode fromString(String s) {
//			if (s == null || s.toLowerCase().equals("client")) {
//				return Client;
//			} else if (s.toLowerCase().equals("server")) {
//				return Server;
//			} else {
//				throw new IllegalArgumentException("There is no mode for: " + s);
//			}
//
//		}
//	}

//	// FIXME: make this scur/ecur etc?
//	enum ChargingMode {
//		Event, Session;
//
//		public ChargingMode fromString(String s) {
//			if (s == null || s.toLowerCase().equals("event")) {
//				return Event;
//			} else if (s.toLowerCase().equals("session")) {
//				return Session;
//			} else {
//				throw new IllegalArgumentException("There is no mode for: " + s);
//			}
//		}
//	}

	
}
