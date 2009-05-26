package org.mobicents.slee.examples.diameter.openims;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.ClientTransaction;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.java.slee.resource.diameter.base.AuthSessionState;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientProvider;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.ResultCode;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * 
 * DiameterOpenIMSExampleSbb.java
 *
 * <br>Super project:  mobicents
 * <br>10:58:03 PM Dec 19, 2008 
 * <br>
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public abstract class DiameterOpenIMSExampleSbb implements javax.slee.Sbb {

  private static Logger logger = Logger.getLogger( DiameterOpenIMSExampleSbb.class );

  private SbbContext sbbContext = null; // This SBB's context

  private Context myEnv = null; // This SBB's environment

  private ShClientProvider provider = null;

  private ShClientMessageFactory shMessageFactory = null;
  private DiameterShAvpFactory avpFactory = null;
  private ShClientActivityContextInterfaceFactory acif = null;
  private TimerFacility timerFacility = null;

  private String originIP = "127.0.0.1";
  private String originPort = "1812";
  private String originRealm = "mobicents.org";

  private String destinationIP = "127.0.0.1";
  private String destinationPort = "3868";
  private String destinationRealm = "mobicents.org";

  private NullActivityFactory nullActivityFactory;

  private NullActivityContextInterfaceFactory nullACIFactory;

  private static HashMap<String, Collection<MissedCall>> missedCalls = new HashMap<String, Collection<MissedCall>>();  

  // SIP Stuff

  private AddressFactory sipAddressFactory;
  private HeaderFactory sipHeaderFactory;
  private javax.sip.message.MessageFactory sipMessageFactory;

  private SleeSipProvider sipProvider;


  public void setSbbContext( SbbContext context )
  {
    this.sbbContext = context;

    try
    {
      myEnv = (Context) new InitialContext().lookup( "java:comp/env" );

      // Diameter Sh Stuff
      provider = (ShClientProvider) myEnv.lookup("slee/resources/diameter-sh-client-ra-interface");

      shMessageFactory = provider.getClientMessageFactory();
      avpFactory = provider.getClientAvpFactory();

      acif = (ShClientActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterShClientResourceAdaptor/java.net/0.8.1/acif");

      // SIP Stuff
      sipProvider = (SleeSipProvider) myEnv.lookup("slee/resources/jainsip/1.2/provider");

      sipAddressFactory = sipProvider.getAddressFactory();
      sipHeaderFactory = sipProvider.getHeaderFactory();
      sipMessageFactory = sipProvider.getMessageFactory();

      // SLEE Facilities
      timerFacility  = (TimerFacility) myEnv.lookup("slee/facilities/timer");
      nullActivityFactory = (NullActivityFactory)myEnv.lookup("slee/nullactivity/factory");
      nullACIFactory = (NullActivityContextInterfaceFactory)myEnv.lookup("slee/nullactivity/activitycontextinterfacefactory");
      
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

  /**
   * Generate a custom convergence name so that related events (with the same call
   * identifier, or session) will go to the same root SBB entity.
   */
  public InitialEventSelector myInitialEventSelector(InitialEventSelector ies)
  {
    Object event = ies.getEvent();
    
    if (event instanceof ResponseEvent)
    {
      Response response = ((ResponseEvent)event).getResponse();

      if(response.getStatusCode() == 404)
      {
        ies.setCustomName( "OpenIMS-Example-StaticCustomName" );
      }
      else
      {
        ies.setInitialEvent( false );
      }
    }
    else if(event instanceof PushNotificationRequest)
    {
      ies.setCustomName( "OpenIMS-Example-StaticCustomName" );
    }
    else
    {
      ies.setInitialEvent( false );
    }

    return ies;
  }

  // ##########################################################################
  // ##                          EVENT HANDLERS                              ##
  // ##########################################################################

  public void onServiceStartedEvent( javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci )
  {
    try
    {
      // check if it's my service that is starting
      ServiceActivity sa = ( (ServiceActivityFactory) myEnv.lookup( "slee/serviceactivity/factory" ) ).getActivity();
      if( sa.equals( aci.getActivity() ) )
      {
        logger.info( "################################################################################" );
        logger.info( "### O P E N I M S    E X A M P L E    A P P L I C A T I O N  :: S T A R T E D ##" );
        logger.info( "################################################################################" );

        shMessageFactory = provider.getClientMessageFactory();
        avpFactory = provider.getClientAvpFactory();

        logger.info( "Performing sanity check..." );
        logger.info( "Provider [" + provider + "]" );
        logger.info( "Message Factory [" + shMessageFactory + "]" );
        logger.info( "AVP Factory [" + avpFactory + "]" );
        logger.info( "Check completed. Result: " + ((provider != null ? 1 : 0) + (shMessageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3" );

        logger.info( "Connected to " + provider.getPeerCount() + " peers." );

        for(DiameterIdentity peer : provider.getConnectedPeers())
        {
          logger.info( "Connected to Peer[" +  peer.toString() + "]" );
        }

        NullActivity timerBus = this.nullActivityFactory.createNullActivity();

        ActivityContextInterface timerBusACI = this.nullACIFactory.getActivityContextInterface(timerBus);

        timerBusACI.attach( sbbContext.getSbbLocalObject() );

        TimerOptions options = new TimerOptions();

        timerFacility.setTimer(timerBusACI, null, System.currentTimeMillis() + 5000, options);
      }
    }
    catch ( Exception e )
    {
      logger.error( "Unable to handle service started event...", e );
    }
  }

  public void onTimerEvent(TimerEvent event, ActivityContextInterface aci)
  {
    try
    {
      Properties props = new Properties();
      props.load( this.getClass().getClassLoader().getResourceAsStream("example.properties") );
      
      this.originIP = props.getProperty( "origin.ip" ) == null ? this.originIP : props.getProperty( "origin.ip" ); 
      this.originPort = props.getProperty( "origin.port" ) == null ? this.originPort : props.getProperty( "origin.port" ); 
      this.originRealm = props.getProperty( "origin.realm" ) == null ? this.originRealm : props.getProperty( "origin.realm" ); 

      this.destinationIP = props.getProperty( "destination.ip" ) == null ? this.destinationIP : props.getProperty( "destination.ip" );
      this.destinationPort = props.getProperty( "destination.port" ) == null ? this.destinationPort : props.getProperty( "destination.port" );
      this.destinationRealm = props.getProperty( "destination.realm" ) == null ? this.destinationRealm : props.getProperty( "destination.realm" ); 
      
      String usersStr = props.getProperty( "users" );
      
      if(usersStr != null && usersStr.length() > 0)
      {
        String[] users = usersStr.split( "," );
        
        logger.info( "Subscribing to Profile Updates from Users " + users.toString() );
        
        for(String user : users)
        {
          sendSubscribeNotificationsRequest(user.trim());
        }
      }
      else
      {
        logger.warn( "No Users are defined for the example. Nothing will happen..." );
      }
    }
    catch (Exception e) {
      logger.error( "Failure reading properties file.", e );
    }
  }

  public void onPushNotificationRequest(net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest pnr, ActivityContextInterface aci)
  {
    logger.info( "Push-Notification-Request received.\r\n" + pnr );

    String userData = pnr.getUserData();

    try
    {
      String userId = null;
      
      DiameterAvp[] avps = pnr.getAvps();
      
      for(DiameterAvp avp : avps)
      {
        if(avp.getCode() == 700)
        {
          if(avp instanceof GroupedAvp)
          {
            GroupedAvp userIdentity = (GroupedAvp)avp;
            for(DiameterAvp subAvp : userIdentity.getExtensionAvps())
            {
              if(subAvp.getCode() == 601)
              {
                userId = subAvp.stringValue();
              }
            }
          }
        }
      }
      
      Collection<MissedCall> mCs = missedCalls.get( userId );

      if(mCs != null && mCs.size() > 0)
      {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = (Document) builder.parse(new InputSource(new StringReader(userData)));

        String userState = doc.getElementsByTagName("IMSUserState").item(0).getTextContent();

        if(userState.equals("1"))
        {
          synchronized ( mCs )
          {
            for(MissedCall missedCall : mCs )
            {
              // Send SIP Message
              sendSIPMessage( userId, missedCall.getNotification() );
            }
          }
          
          // Clear the missed calls for this user
          mCs.clear();
        }
      }
    }
    catch (Exception e) {
      logger.error( "Error parsing User-Data AVP.", e );
    }
  }

  public void on4xxResponse(javax.sip.ResponseEvent event, ActivityContextInterface aci)
  {
    Response response = event.getResponse();

    logger.info( "Received SIP 4xx » " + response.getStatusCode() );

    // Is it a 404?
    if(response.getStatusCode() == 404)
    {
      // Let's see from whom to whom
      String to = ((ToHeader) response.getHeader("To")).getAddress().toString();
      String from = ((FromHeader) response.getHeader("From")).getAddress().toString();

      logger.info( "From[" + from + "], To [" + to + "]");

      String toAddress = to.substring( to.indexOf("sip:"), to.indexOf( ">" ) );
      
      // Create the MissedCall object
      MissedCall mC = new MissedCall(from, new Date());

      Collection<MissedCall> mCs = missedCalls.get(toAddress);

      if(mCs == null)
      {
        mCs = new ArrayList<MissedCall>();
        missedCalls.put( toAddress, mCs );
      }

      if(!mCs.contains( mC ))
      {
        mCs.add( mC );
      }
    }
  }

  // ###################################

  public void onSubscriptionNotificationsAnswer(net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer sna, ActivityContextInterface aci)
  {
    logger.info( "Subscription-Notifications-Answer received with Result-Code[" + sna.getResultCode() + "]..");
    
    if(sna.getResultCode() != ResultCode.SUCCESS)
    {
      logger.warn( "Subscription WAS NOT successful. Please check your permissions and/or users." );
    }
  }

  public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci)
  {
    logger.info( " Activity Ended[" + aci.getActivity() + "]" );
  }


  // ##########################################################################
  // ##                           PRIVATE METHODS                            ##
  // ##########################################################################

  private void sendSubscribeNotificationsRequest(String user)
  {
    try 
    {
      ShClientSubscriptionActivity shClientSubscriptionActivity = this.provider.createShClientSubscriptionActivity();

      shClientSubscriptionActivity.getDiameterAvpFactory();
      shClientSubscriptionActivity.getDiameterMessageFactory();

      ActivityContextInterface localACI = this.acif.getActivityContextInterface(shClientSubscriptionActivity);
      localACI.attach(getSbbContext().getSbbLocalObject());

      List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
      
      SubscribeNotificationsRequest snr = ((ShClientMessageFactory)shClientSubscriptionActivity.getDiameterMessageFactory()).createSubscribeNotificationsRequest();
      
      //< Subscribe-Notifications-Request > :: =   < Diameter Header: 308, REQ, PXY, 16777217 >
      //        < Session-Id >
      avps.add(avpFactory.getBaseFactory().createAvp(Avp.SESSION_ID, shClientSubscriptionActivity.getSessionId().getBytes() ));
      
      //        { Vendor-Specific-Application-Id }
      DiameterAvp avpVendorId = avpFactory.getBaseFactory().createAvp( Avp.VENDOR_ID, MessageFactory._SH_VENDOR_ID );
      DiameterAvp avpAcctApplicationId = avpFactory.getBaseFactory().createAvp( Avp.AUTH_APPLICATION_ID, MessageFactory._SH_APP_ID );

      avps.add( avpFactory.getBaseFactory().createAvp( Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[]{avpVendorId, avpAcctApplicationId} ) );

      //        { Auth-Session-State }
      //        { Origin-Host }
      avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_HOST, ("aaa://" + this.originIP + ":" + this.originPort).getBytes() ));
      //        { Origin-Realm }
      avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_REALM, this.originRealm.getBytes() ));
      //        [ Destination-Host ]
      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, ("aaa://" + this.destinationIP + ":" + this.destinationPort).getBytes() ));
      //        { Destination-Realm }
      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, this.destinationRealm.getBytes() ));
      //        *[ Supported-Features ]
      //        { User-Identity }
      
      UserIdentityAvp ui = avpFactory.createUserIdentity();
      ui.setPublicIdentity("sip:" + user.replaceFirst( "sip:", "" ));
      avps.add(ui);
      //        [ Wildcarded-PSI ]
      //        [ Wildcarded-IMPU ]
      //        *[ Service-Indication ]
      //        [ Send-Data-Indication ]
      //        [ Server-Name ]
      //        { Subs-Req-Type }

      // 0 == Subscribe // 1 == Unsubrscribe
      DiameterAvp srt = avpFactory.getBaseFactory().createAvp(MessageFactory._SH_VENDOR_ID, DiameterShAvpCodes.SUBS_REQ_TYPE, 0);
      avps.add(srt);

      //        *{ Data-Reference }
      //Its enumerated: 0 == Whole data
      DiameterAvp avp = avpFactory.getBaseFactory().createAvp(MessageFactory._SH_VENDOR_ID, DiameterShAvpCodes.DATA_REFERENCE, 11);
      avps.add(avp);
      //        [ Identity-Set ]
      //        [ Expiry-Time ]
      avps.add(avpFactory.getBaseFactory().createAvp(Avp.AUTH_SESSION_STATE, AuthSessionState.Open.ordinal()));        

      //We can user setters, but this is faster :)
      snr.setExtensionAvps(avps.toArray(avps.toArray(new DiameterAvp[avps.size()])));
      
      logger.info( "Created Subscribe-Notifications-Request:\r\n" + snr );
      
      shClientSubscriptionActivity.sendSubscriptionNotificationRequest(snr);
      
      logger.info("Subscribe-Notifications-Request sent!");

    } catch (Exception e) {
      logger.error( "Failure trying to create/send Subscribe-Notifications-Request.", e );
    }
  }
  
  private void sendSIPMessage(String toAddressString, String message)
  {
    try
    {
      // Create To Header
      Address toAddress = sipAddressFactory.createAddress(toAddressString);
      toAddress.setDisplayName(toAddressString);
      ToHeader toHeader = sipHeaderFactory.createToHeader( toAddress, null );
      
      // Create From Header
      SipURI fromAddress = sipAddressFactory.createSipURI("missed-calls", System.getProperty("bind.address", "127.0.0.1"));

      Address fromNameAddress = sipAddressFactory.createAddress(fromAddress);
      fromNameAddress.setDisplayName("Missed Calls");
      FromHeader fromHeader = sipHeaderFactory.createFromHeader(fromNameAddress, "12345SomeTagID6789");

      // Create Via Headers
      ArrayList viaHeaders = new ArrayList();
      ViaHeader viaHeader = sipHeaderFactory.createViaHeader(
          sipProvider.getListeningPoints()[0].getIPAddress(), 
          sipProvider.getListeningPoints()[0].getPort(), 
          sipProvider.getListeningPoints()[0].getTransport(), 
          null);
      viaHeaders.add(viaHeader);

      // Create Max-Forwards Header
      MaxForwardsHeader maxForwards = this.sipHeaderFactory.createMaxForwardsHeader(70);

      // Create the Request
      URI uri = sipProvider.getAddressFactory().createURI(toAddressString);
      Request req = sipMessageFactory.createRequest(uri, Request.MESSAGE, this.sipProvider.getNewCallId(), 
          sipHeaderFactory.createCSeqHeader(1L, Request.MESSAGE), fromHeader, toHeader, viaHeaders, maxForwards);
      
      // Add the content with it's type
      ContentTypeHeader contentType = sipHeaderFactory.createContentTypeHeader("text", "plain");
      req.setContent(message, contentType);
      
      // And send it!
      ClientTransaction ct = sipProvider.getNewClientTransaction(req);
      ct.sendRequest();
    }
    catch (Exception e) {
      logger.error( "Failure creating SIP Message notification.", e );
    }

  }

}
