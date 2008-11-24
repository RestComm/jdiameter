package org.mobicents.slee.examples.diameter.sh.client;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.AccountingClientSessionActivity;
import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.DiameterProvider;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientProvider;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.ResultCode;

import org.mobicents.slee.resource.diameter.base.AccountingServerSessionActivityImpl;

/**
 * 
 * DiameterExampleSbb.java
 *
 * <br>Super project:  mobicents
 * <br>11:34:16 PM May 26, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 */
public abstract class DiameterExampleSbb implements javax.slee.Sbb {

  private static Logger logger = Logger.getLogger( DiameterExampleSbb.class );

  private SbbContext sbbContext = null; // This SBB's context

  private Context myEnv = null; // This SBB's environment

  private ShClientProvider provider = null;
  
  private ShClientMessageFactory messageFactory = null;
  private DiameterShAvpFactory avpFactory = null;
  private ShClientActivityContextInterfaceFactory acif=null;
  private TimerFacility timerFacility = null;
  
  private String originIP = "127.0.0.1";
  private String destinationIP = "127.0.0.1";
  
  public void setSbbContext( SbbContext context )
  {
    logger.info( "sbbRolledBack invoked." );

    this.sbbContext = context;

    try
    {
      myEnv = (Context) new InitialContext().lookup( "java:comp/env" );
      
      provider = (ShClientProvider) myEnv.lookup("slee/resources/diameter-sh-client-ra-interface");
      logger.info( "Got Provider:" + provider );
      
      messageFactory = provider.getClientMessageFactory();
      logger.info( "Got Message Factory:" + provider );
      
      avpFactory = provider.getClientAvpFactory();
      logger.info( "Got AVP Factory:" + provider );
      
      acif=(ShClientActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterShClientResourceAdaptor/java.net/0.8.1/acif");
      
      // Get the timer facility
      timerFacility  = (TimerFacility) myEnv.lookup("slee/facilities/timer");
    }
    catch ( Exception e )
    {
      logger.error( "Unable to set sbb context.", e );
    }
  }

  public void unsetSbbContext()
  {
    logger.info( "unsetSbbContext invoked." );

    this.sbbContext = null;
  }

  public void sbbCreate() throws javax.slee.CreateException
  {
    logger.info( "sbbCreate invoked." );
  }

  public void sbbPostCreate() throws javax.slee.CreateException
  {
    logger.info( "sbbPostCreate invoked." );
  }

  public void sbbActivate()
  {
    logger.info( "sbbActivate invoked." );
  }

  public void sbbPassivate()
  {
    logger.info( "sbbPassivate invoked." );
  }

  public void sbbRemove()
  {
    logger.info( "sbbRemove invoked." );
  }

  public void sbbLoad()
  {
    logger.info( "sbbLoad invoked." );
  }

  public void sbbStore()
  {
    logger.info( "sbbStore invoked." );
  }

  public void sbbExceptionThrown( Exception exception, Object event, ActivityContextInterface activity )
  {
    logger.info( "sbbRolledBack invoked." );
  }

  public void sbbRolledBack( RolledBackContext context )
  {
    logger.info( "sbbRolledBack invoked." );
  }

  protected SbbContext getSbbContext()
  {
    logger.info( "getSbbContext invoked." );

    return sbbContext;
  }

  // ##########################################################################
  // ##                          EVENT HANDLERS                              ##
  // ##########################################################################

  public void onServiceStartedEvent( javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci )
  {
    logger.info( "onServiceStartedEvent invoked." );

    try
    {
      // check if it's my service that is starting
      ServiceActivity sa = ( (ServiceActivityFactory) myEnv.lookup( "slee/serviceactivity/factory" ) ).getActivity();
      if( sa.equals( aci.getActivity() ) )
      {
        logger.info( "################################################################################" );
        logger.info( "### D I A M E T E R   E X A M P L E   A P P L I C A T I O N  :: S T A R T E D ##" );
        logger.info( "################################################################################" );
        
        messageFactory = provider.getClientMessageFactory();
        avpFactory = provider.getClientAvpFactory();
        
        logger.info( "Performing sanity check..." );
        logger.info( "Provider [" + provider + "]" );
        logger.info( "Message Factory [" + messageFactory + "]" );
        logger.info( "AVP Factory [" + avpFactory + "]" );
        logger.info( "Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3" );

        logger.info( "Connected to " + provider.getPeerCount() + " peers." );
        
        for(DiameterIdentityAvp peer : provider.getConnectedPeers())
          logger.info( "Connected to Peer[" +  peer.stringValue() + "]" );

        TimerOptions options = new TimerOptions();
        
        timerFacility.setTimer(aci, null, System.currentTimeMillis() + 5000, options);
        
        /* Uncomment for basic message sending testing (DWR/DWA)

        try
        {
          DiameterAvp avp_DestHost = avpFactory.createAvp( Avp.DESTINATION_HOST, "127.0.0.1".getBytes() );
          DiameterAvp avp_DestRealm = avpFactory.createAvp( Avp.DESTINATION_REALM, "mobicents.org".getBytes() );

          DiameterAvp avp_HostIPAddress = avpFactory.createAvp( Avp.HOST_IP_ADDRESS, ("0x0001" + "7f000001").getBytes() );
          
          DiameterAvp avp_VendorId = avpFactory.createAvp( Avp.VENDOR_ID, "193".getBytes() );
          DiameterAvp avp_ProductName = avpFactory.createAvp( Avp.PRODUCT_NAME, "jDiameter".getBytes() );
         
          DiameterAvp[] avps = new DiameterAvp[]{avp_DestHost, avp_DestRealm, avp_HostIPAddress, avp_VendorId, avp_ProductName};

          logger.info( "Creating Custom Message..." );
          DiameterMessage ms = messageFactory.createDeviceWatchdogRequest(avps);
          logger.info( "Created Custom Message[" + ms + "]" );
         
          logger.info( "Sending Custom Message..." );
          provider.createActivity().sendMessage( ms );
          logger.info( "Sent Custom Message[" + ms + "]" );
        }
        catch (Exception e) {
          logger.error( "Not working...", e );
        }
        
        */
      }
    }
    catch ( Exception e )
    {
      logger.error( "Unable to handle service started event...", e );
    }
  }
  
  public void onTimerEvent(TimerEvent event, ActivityContextInterface aci)
  {
	  doSimpleTestsSendUDR();
	
	  //doSimpleTestSendSNR();
  }
  
  private void doSimpleTestsSendUDR()
  {
	  logger.info(" On TimerEvent: performing basic creation tests.");
	  logger.info(" On TimerEvent: ShClient activity and messages creation.");
	  try {
			ShClientActivity basicClientActivity=this.provider.createShClientActivity();
			logger.info(" On TimerEvent: activity created");
			DiameterMessage msg=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createProfileUpdateRequest();
			logger.info(" On TimerEvent: PUR created.\n"+msg);
			msg=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createUserDataRequest();
			logger.info(" On TimerEvent: UDR created.\n"+msg);
			msg=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createPushNotificationAnswer();
			logger.info(" On TimerEvent: PUA created.\n"+msg);
			msg=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createSubscribeNotificationsRequest();
			logger.info(" On TimerEvent: SNR created.\n"+msg);
			ActivityContextInterface localACI=acif.getActivityContextInterface(basicClientActivity);
			logger.info(" On TimerEvent: ACI created for basicClientActivity");
			
			localACI.attach(getSbbContext().getSbbLocalObject());
			
			
			DiameterIdentityAvp[] peers=provider.getConnectedPeers();
			
			for(DiameterIdentityAvp peer: peers)
			{
				logger.info(" On TimerEvent: Connected Peer: "+peer.stringValue());
			}
			
			logger.info(" On TimerEvent: creating UDR");
			
			UserDataRequest udr=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createUserDataRequest();
			
			List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
		      
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.SESSION_ID, basicClientActivity.getSessionId().getBytes() ));
		  
		      DiameterAvp avpVendorId = avpFactory.getBaseFactory().createAvp( Avp.VENDOR_ID, MessageFactory._SH_VENDOR_ID );
		      DiameterAvp avpAcctApplicationId = avpFactory.getBaseFactory().createAvp( Avp.ACCT_APPLICATION_ID, MessageFactory._SH_APP_ID );
		      
		      avps.add( avpFactory.getBaseFactory().createAvp( Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[]{avpVendorId, avpAcctApplicationId} ) );
		      
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_HOST, "aaa://" + originIP + ":1812".getBytes() ));
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes() ));
		      
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, "aaa://" + destinationIP + ":3868".getBytes() ));
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
		      UserIdentityAvp ui=avpFactory.createUserIdentity();
		      ui.setPublicIdentity("sip:subscriber@mobicents.org");

		      avps.add(ui);
		      ui=avpFactory.createUserIdentity();
		      ui.setPublicIdentity("TEL:+64216543210");

		      avps.add(ui);
		      udr.setExtensionAvps(avps.toArray(new DiameterAvp[avps.size()]));
		      
		      logger.info(" On TimerEvent: Sending message:\n"+udr);
		      basicClientActivity.sendUserDataRequest(udr);
		      
			logger.info(" On TimerEvent: Message send");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  private void doSimpleTestSendSNR()
  {
	  try {
	  ShClientSubscriptionActivity shClientSubscriptionActivity=this.provider.createShClientSubscriptionActivity();
		logger.info(" On TimerEvent: Client Subscrition Activity created");
		
		shClientSubscriptionActivity.getDiameterAvpFactory();
		shClientSubscriptionActivity.getDiameterMessageFactory();
		
		logger.info(" On TimerEvent: Subscription activity methods tested");
		ActivityContextInterface localACI=this.acif.getActivityContextInterface(shClientSubscriptionActivity);
		localACI.attach(getSbbContext().getSbbLocalObject());
		logger.info(" On TimerEvent: Subscription activity acif created");
		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
		SubscribeNotificationsRequest snr=((ShClientMessageFactory)shClientSubscriptionActivity.getDiameterMessageFactory()).createSubscribeNotificationsRequest();
		//< Subscribe-Notifications-Request > ::=	< Diameter Header: 308, REQ, PXY, 16777217 >
		//				< Session-Id >
		 avps.add(avpFactory.getBaseFactory().createAvp(Avp.SESSION_ID, shClientSubscriptionActivity.getSessionId().getBytes() ));
		//				{ Vendor-Specific-Application-Id }
	      DiameterAvp avpVendorId = avpFactory.getBaseFactory().createAvp( Avp.VENDOR_ID, MessageFactory._SH_VENDOR_ID );
	      DiameterAvp avpAcctApplicationId = avpFactory.getBaseFactory().createAvp( Avp.ACCT_APPLICATION_ID, MessageFactory._SH_APP_ID );
	      
	      avps.add( avpFactory.getBaseFactory().createAvp( Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[]{avpVendorId, avpAcctApplicationId} ) );
	      
	   
	      
	     
		//				{ Auth-Session-State }
		//				{ Origin-Host }
	      avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_HOST, "aaa://" + originIP + ":1812".getBytes() ));
		//				{ Origin-Realm }
	      avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes() ));
		//				[ Destination-Host ]
	      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, "aaa://" + destinationIP + ":3868".getBytes() ));
		//				{ Destination-Realm }
	      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
		//				*[ Supported-Features ]
		//				{ User-Identity }
	      UserIdentityAvp ui=avpFactory.createUserIdentity();
	      ui.setPublicIdentity("sip:subscriber@mobicents.org");
	      avps.add(ui);
		//				[ Wildcarded-PSI ]
		//				[ Wildcarded-IMPU ]
		//				*[ Service-Indication ]
		//				[ Send-Data-Indication ]
		//				[ Server-Name ]
		//				{ Subs-Req-Type }
		//				*{ Data-Reference }
	      //Its enumerated: 0 == Whole data
	      DiameterAvp avp=avpFactory.getBaseFactory().createAvp(MessageFactory._SH_VENDOR_ID,DiameterShAvpCodes.DATA_REFERENCE, 0);
	      avps.add(avp);
		//				[ Identity-Set ]
		//				[ Expiry-Time ]
	      //We can user setters, but this is faster :)
	      snr.setExtensionAvps(avps.toArray(avps.toArray(new DiameterAvp[avps.size()])));
	      logger.info("---> Sending SNR");
	      shClientSubscriptionActivity.sendSubscriptionNotificationRequest(snr);
	      logger.info("---> Send SNR:\n"+snr);

	  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  // ###################################
  // #  REQEUSTS - PNR, this is client #
  // ###################################
 
  										
  public void onPushNotificationRequest(net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest pnr, ActivityContextInterface aci)
  {
	  logger.info( "Push-Notification-Request activity["+aci.getActivity()+"] received.\n"+pnr );
  }
  
  
  // ###################################
  // # ASNWERS - PUA, SNA, UDA         #
  // ###################################
  
  public void onProfileUpdateAnswer(net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer pua, ActivityContextInterface aci)
  {
	  logger.info( "Profile-Update-Answer activity["+aci.getActivity()+"] received.\n"+pua );
  }
  
  public void onSubscriptionNotificationsAnswer(net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer sna, ActivityContextInterface aci)
  {
	  logger.info( "Subscription-Notifications-Answer activity["+aci.getActivity()+"] received.\n"+sna );
	  logger.info( "Subscription-Notifications-Answer Result-Code["+sna.getResultCode()+"].");
  }
  
  public void onUserDataRequestAnswer(net.java.slee.resource.diameter.sh.client.events.UserDataAnswer uda, ActivityContextInterface aci)
  {
	  logger.info( "User-Data-Answer activity["+aci.getActivity()+"] received.\n"+uda );
  }
  
  
  //BCK
  public void onAccountingRequest(net.java.slee.resource.diameter.base.events.AccountingRequest acr, ActivityContextInterface aci)
  {
    long start = System.currentTimeMillis();
    logger.info( "Accounting-Request received. [" + acr + "]" );

    boolean actAsProxy = false;

    try
    {
      // Are we gonna act as a proxy?
      if(actAsProxy)
      {
        // In here we act as a "proxy". Just for testing we take the original message,
        // replace the Origin/Destination Host/Realm AVPs and send it to the emulator.
        
        boolean hasDestinationHost = false;
        boolean hasDestinationRealm = false;

        List<DiameterAvp> avps = new ArrayList<DiameterAvp>(); 
          
        for(DiameterAvp avp : acr.getAvps())
        {
          switch(avp.getCode())
          {
          case Avp.ORIGIN_HOST:
            avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_HOST, "aaa://" + originIP + ":1812".getBytes() ));
            break;
          case Avp.ORIGIN_REALM:
            avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes() ));
            break;
          case Avp.DESTINATION_HOST:
            avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, "aaa://" + destinationIP + ":21812".getBytes() ));
            hasDestinationHost = true;
            break;
          case Avp.DESTINATION_REALM:
            avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
            hasDestinationRealm = true;
            break;
          default:
            avps.add(avp);
          }
        }

        if(!hasDestinationHost)
          avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, destinationIP.getBytes() ));
        
        if(!hasDestinationRealm)
          avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
  
        logger.info( "AVPs ==> " + avps );
       
        DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
        avpArray = avps.toArray(avpArray);
        
        logger.info( "Creating Custom Message..." );
        //DiameterMessage ms = messageFactory.createAccountingRequest(avpArray);
        //logger.info( "Created Custom Message[" + ms + "]" );
       
        logger.info( "Sending Custom Message..." );
       // provider.createActivity().sendMessage( ms );
        //logger.info( "Sent Custom Message[" + ms + "]" );
      }
      else
      {
        // In here we act as a server and just say it's SUCCESS.
        
        if(aci.getActivity() instanceof AccountingServerSessionActivityImpl)
        {
          AccountingServerSessionActivityImpl assa = (AccountingServerSessionActivityImpl)aci.getActivity();
          
          AccountingAnswer ans = assa.createAccountAnswer( acr, ResultCode.SUCCESS );
          
          logger.info( "Sending Accounting-Answer [" + ans + "]" );
  
          assa.sendAccountAnswer( ans );
  
          logger.info( "Accounting-Answer sent." );          
        }
      }
    }
    catch (Exception e)
    {
      logger.error( "", e );
    }
    
    long end = System.currentTimeMillis();
    
    logger.info( "Accounting-Request proccessed. [" + (end-start) + "ms]" );
  }
	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		logger.info( " Activity Ended["+aci.getActivity()+"]" );
	}

  
  // ##########################################################################
  // ##                           PRIVATE METHODS                            ##
  // ##########################################################################
  
  private void sendAccountingRequest()
  {
    try
    {
    	/*
      AccountingClientSessionActivity activity = provider.createAccountingActivity();
      
      List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
      
      avps.add(avpFactory.createAvp(Avp.SESSION_ID, activity.getSessionId().getBytes() ));
  
      DiameterAvp avpVendorId = avpFactory.createAvp( Avp.VENDOR_ID, 193 );
      DiameterAvp avpAcctApplicationId = avpFactory.createAvp( Avp.ACCT_APPLICATION_ID, 193 );
      
      avps.add( avpFactory.createAvp( Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[]{avpVendorId, avpAcctApplicationId} ) );
      
      avps.add(avpFactory.createAvp(Avp.ORIGIN_HOST, "aaa://127.0.0.1:1812".getBytes() ));
      avps.add(avpFactory.createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes() ));
      
      avps.add(avpFactory.createAvp(Avp.DESTINATION_HOST, "aaa://127.0.0.1:21812".getBytes() ));
      avps.add(avpFactory.createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
  
      // Subscription ID
      DiameterAvp subscriptionIdType = avpFactory.createAvp( 193, 555, 0 );
      DiameterAvp subscriptionIdData = avpFactory.createAvp( 193, 554, "00001000" );
      avps.add( avpFactory.createAvp( 193, 553, new DiameterAvp[]{subscriptionIdType, subscriptionIdData} ) );
      
      // Requested Service Unit 
      DiameterAvp unitType = avpFactory.createAvp( 193, 611, 2 );
      DiameterAvp valueDigits = avpFactory.createAvp( 193, 617, 10L );
      DiameterAvp unitValue = avpFactory.createAvp( 193, 612, new DiameterAvp[]{valueDigits} );
      avps.add( avpFactory.createAvp( 193, 606, new DiameterAvp[]{unitType, unitValue} ) );

      // Record Number and Type 
      avps.add(avpFactory.createAvp(Avp.ACC_RECORD_NUMBER, 0 ));
      avps.add(avpFactory.createAvp(Avp.ACC_RECORD_TYPE, 1 ));

      // Requested action
      avps.add( avpFactory.createAvp( 193, 615, 0 ) );
      
      // Service Parameter Type
      DiameterAvp serviceParameterType = avpFactory.createAvp( 193, 608, 0 );
      DiameterAvp serviceParameterValue = avpFactory.createAvp( 193, 609, "510" );
      avps.add( avpFactory.createAvp( 193, 607, new DiameterAvp[]{serviceParameterType, serviceParameterValue} ) );
      
      // Service Parameter Type
      DiameterAvp serviceParameterType2 = avpFactory.createAvp( 193, 608, 14 );
      DiameterAvp serviceParameterValue2 = avpFactory.createAvp( 193, 609, "20" );
      avps.add( avpFactory.createAvp( 193, 607, new DiameterAvp[]{serviceParameterType2, serviceParameterValue2} ) );
  
      DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
      avpArray = avps.toArray(avpArray);
      
      logger.info( "Creating Custom Message..." );
      DiameterMessage ms = messageFactory.createAccountingRequest(avpArray);
      logger.info( "Created Custom Message[" + ms + "]" );
      
      logger.info( "Sending Custom Message..." );
      activity.sendAccountRequest((AccountingRequest) ms);
      logger.info( "Sent Custom Message[" + ms + "]" );
      */
    }
    catch (Exception e)
    {
      logger.error( "", e );
    }
  }

}
