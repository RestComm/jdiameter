package org.mobicents.slee.tests.diameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.DiameterProvider;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

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
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Erick Svenson
 */
public abstract class DiameterTestsSbb implements javax.slee.Sbb {

  private static Logger logger = Logger.getLogger( DiameterTestsSbb.class );

  private SbbContext sbbContext = null; // This SBB's context

  private Context myEnv = null; // This SBB's environment

  private DiameterProvider provider = null;

  private DiameterMessageFactory messageFactory = null;
  private DiameterAvpFactory avpFactory = null;

  private TimerFacility timerFacility = null;

  private static class DiameterUser
  {
    String msisdn;
    String name;
    String imsi;
    Double balance;
    Double reserved;
    
    public DiameterUser( String msisdn, String name, String imsi, Double balance, Double reserved )
    {
      this.msisdn = msisdn;
      this.name = name;
      this.msisdn = msisdn;
      this.balance = balance;
      this.reserved = reserved;
    }
  }
  
  private static HashMap<String, DiameterUser> users = new HashMap();
  
  static {
    users.put( "00001000", new DiameterUser("00001000", "Alexandre Mendonca", "00001000", 1000.0, 0.0) );
    users.put( "00001001", new DiameterUser("00001001", "Bartosz Baranowski", "00001001", 100.0, 0.0) );
    users.put( "00001002", new DiameterUser("00001002", "Erick Svensson", "00001002", 0.0, 0.0) );
  }
  
  public void setSbbContext( SbbContext context )
  {
    logger.info( "sbbRolledBack invoked." );

    this.sbbContext = context;

    try
    {
      myEnv = (Context) new InitialContext().lookup( "java:comp/env" );

      provider = (DiameterProvider) myEnv.lookup("slee/resources/diameter-base-ra-acif");
      logger.info( "Got Provider:" + provider );

      messageFactory = provider.getDiameterMessageFactory();
      logger.info( "Got Message Factory:" + provider );

      avpFactory = provider.getDiameterAvpFactory();
      logger.info( "Got AVP Factory:" + provider );

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
        logger.info( "##  D I A M E T E R   T E S T   A P P L I C A T I O N   S B B   E N G A G E D ##" );
        logger.info( "################################################################################" );

        messageFactory = provider.getDiameterMessageFactory();
        avpFactory = provider.getDiameterAvpFactory();

        logger.info( "Performing sanity check..." );
        logger.info( "Provider [" + provider + "]" );
        logger.info( "Message Factory [" + messageFactory + "]" );
        logger.info( "AVP Factory [" + avpFactory + "]" );
        logger.info( "# Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3" );

        logger.info( "Connected to " + provider.getPeerCount() + " peers." );
      }
    }
    catch ( Exception e )
    {
      logger.error( "Unable to handle service started event...", e );
    }
  }

  public void onTimerEvent(TimerEvent event, ActivityContextInterface aci)
  {
    sendAccountingRequest();
  }

  public void onAbortSessionRequest(net.java.slee.resource.diameter.base.events.AbortSessionRequest asr, ActivityContextInterface aci)
  {
    logger.info( "Abort-Session-Request received." );
  }

  public void onAbortSessionAnswer(net.java.slee.resource.diameter.base.events.AbortSessionAnswer asa, ActivityContextInterface aci)
  {
    logger.info( "Abort-Session-Answer received." );
  }

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
            avps.add(avpFactory.createAvp(Avp.ORIGIN_HOST, "aaa://127.0.0.1:1812".getBytes() ));
            break;
          case Avp.ORIGIN_REALM:
            avps.add(avpFactory.createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes() ));
            break;
          case Avp.DESTINATION_HOST:
            avps.add(avpFactory.createAvp(Avp.DESTINATION_HOST, "aaa://127.0.0.1:21812".getBytes() ));
            hasDestinationHost = true;
            break;
          case Avp.DESTINATION_REALM:
            avps.add(avpFactory.createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
            hasDestinationRealm = true;
            break;
          default:
            avps.add(avp);
          }
        }

        if(!hasDestinationHost)
          avps.add(avpFactory.createAvp(Avp.DESTINATION_HOST, "127.0.0.1".getBytes() ));

        if(!hasDestinationRealm)
          avps.add(avpFactory.createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));

        logger.info( "AVPs ==> " + avps );

        DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
        avpArray = avps.toArray(avpArray);

        logger.info( "Creating Custom Message..." );
        DiameterMessage ms = messageFactory.createAccountingRequest(avpArray);
        logger.info( "Created Custom Message[" + ms + "]" );

        logger.info( "Sending Custom Message..." );
        provider.createActivity().sendMessage( ms );
        logger.info( "Sent Custom Message[" + ms + "]" );
      }
      else
      {
        // In here we act as a server...
        int subscriptionIdType = -1;
        String subscriptionIdData = "";
        
        int unitType = -1;
        long valueDigits = -1;
        int exponent = 0;
        
        int requestedAction = -1;
        
        int serviceParameterType;
        int serviceParameterValue;
        int serviceParameterInfo;

        DiameterAvp subscriptionIdAvp = null;
        
        DiameterAvp requestedActionAvp = null;

        if(aci.getActivity() instanceof AccountingServerSessionActivityImpl)
        {
          for(DiameterAvp avp : acr.getAvps())
          {
            switch ( avp.getCode() )
            {
            case SUBSCRIPTION_ID:
            {
              // This should contain a SUBSCRIPTION_ID_TYPE and a SUBSCRIPTION_ID_DATA
              if(avp instanceof GroupedAvp)
              {
                GroupedAvp gAvp = (GroupedAvp)avp;
                
                for(DiameterAvp subAvp : gAvp.getExtensionAvps())
                {
                  switch(subAvp.getCode())
                  {
                  case SUBSCRIPTION_ID_TYPE:
                    subscriptionIdType = subAvp.intValue();
                    break;
                  case SUBSCRIPTION_ID_DATA:
                    subscriptionIdData = subAvp.stringValue();
                    break;
                  }
                }
              }
            }
            break;
            
            case REQUESTED_SERVICE_UNIT:
            {              
              // This should contain a UNIT_TYPE and a UNIT_VALUE
              if(avp instanceof GroupedAvp)
              {
                GroupedAvp gAvp = (GroupedAvp)avp;

                for(DiameterAvp subAvp : gAvp.getExtensionAvps())
                {
                  switch(subAvp.getCode())
                  {
                    case UNIT_TYPE:
                      unitType = subAvp.intValue();
                    break;
                    case UNIT_VALUE:
                    {
                      // This should contain a VALUE_DIGITS
                      if(subAvp instanceof GroupedAvp)
                      {
                        GroupedAvp gSubAvp = (GroupedAvp)subAvp;
                        
                        for(DiameterAvp subSubAvp : gSubAvp.getExtensionAvps())
                        {
                          switch(subSubAvp.getCode())
                          {
                            case VALUE_DIGITS:
                              valueDigits = subSubAvp.longValue();
                            break;
                            case EXPONENT:
                              exponent = subSubAvp.intValue();
                            break;
                          }
                        }
                      }
                      break;
                    }
                  }
                }
              }
            }
            break;

            case REQUESTED_ACTION:
              requestedAction = avp.intValue();
              requestedActionAvp = avp;
            break;
            
            case SERVICE_PARAMETER_TYPE:
              // We can discard this...
            break;
            case SERVICE_PARAMETER_VALUE:
              // We can discard this...
            break;
            case SERVICE_PARAMETER_INFO:
              // We can discard this...
            break;
              default:
                
            }
          }
          
          logger.info( "Subscription-Id-Type: " + subscriptionIdType );
          logger.info( "Subscription-Id-Data: " + subscriptionIdData );
          logger.info( "Unit-Type: " + unitType );
          logger.info( "Value-Digits: " + valueDigits );
          logger.info( "Exponent: " + exponent );
          logger.info( "Requested-Action: " + requestedAction );    
          
          AccountingServerSessionActivityImpl assa = (AccountingServerSessionActivityImpl)aci.getActivity();

          // Aditional AVPs container
          List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

          AccountingAnswer ans = null;          
          
          double chargingValue = valueDigits * Math.pow( 10, exponent );
          
          if(subscriptionIdType == 0 || subscriptionIdType == 1)
          {
            DiameterUser user = null;
            if( (user = users.get( subscriptionIdData )) == null )
            {
              // Not a valid user. Reject it with DIAMETER_END_USER_NOT_FOUND.
              ans = assa.createAccountAnswer( acr, 5241 );

              // Subscription ID
              DiameterAvp subscriptionIdTypeAvp = avpFactory.createAvp( 193, 555, subscriptionIdType );
              DiameterAvp subscriptionIdDataAvp = avpFactory.createAvp( 193, 554, subscriptionIdData );
              avps.add( avpFactory.createAvp( 193, 553, new DiameterAvp[]{subscriptionIdTypeAvp, subscriptionIdDataAvp} ) );
            }
            else if(requestedAction == 0 && user.balance < chargingValue)
            {
              logger.info( "Received Direct Debit Request:" );
              logger.info( "User ID " + subscriptionIdData + " (" + user.name + ")" );
              logger.info( "Current Balance: " + user.balance );
              logger.info( "Charging Value: " + chargingValue );

              // Not able to provide the service. not enough balance.
              ans = assa.createAccountAnswer( acr, 4241 );

              // Subscription ID
              DiameterAvp subscriptionIdTypeAvp = avpFactory.createAvp( 193, 555, subscriptionIdType );
              DiameterAvp subscriptionIdDataAvp = avpFactory.createAvp( 193, 554, subscriptionIdData );
              avps.add( avpFactory.createAvp( 193, 553, new DiameterAvp[]{subscriptionIdTypeAvp, subscriptionIdDataAvp} ) );
            }
            else
            {
              boolean isError = false;
              
              // Refund Account?
              if(requestedAction == 1)
              {
                logger.info( "Received Refund Account Request:" );
                logger.info( "User ID " + subscriptionIdData + " (" + user.name + ")" );
                logger.info( "Old Balance: " + user.balance );
                user.balance += chargingValue;
                logger.info( "New Balance: " + user.balance );
              }
              else if(requestedAction == 0)
              {
                logger.info( "Received Direct Debit Request:" );
                logger.info( "User ID " + subscriptionIdData + " (" + user.name + ")" );
                logger.info( "Old Balance: " + user.balance );
                user.balance -= chargingValue;
                logger.info( "New Balance: " + user.balance );
              }
              else
              {
                logger.warn( "Unknown requested action (" + requestedAction + ")" );

                DiameterAvp failedAvp = avpFactory.createAvp( 0, 279, new DiameterAvp[]{requestedActionAvp} );

                ans = assa.createAccountAnswer( acr, ResultCode.INVALID_AVP_VALUE);

                avps.add( failedAvp );
                
                isError = true;
              }
              
              if(!isError)
              {
                // It's OK. Let's answer with 2001
                ans = assa.createAccountAnswer( acr, ResultCode.SUCCESS );
                
                // Subscription ID
                DiameterAvp subscriptionIdTypeAvp = avpFactory.createAvp( 193, 555, subscriptionIdType );
                DiameterAvp subscriptionIdDataAvp = avpFactory.createAvp( 193, 554, subscriptionIdData );
                avps.add( avpFactory.createAvp( 193, 553, new DiameterAvp[]{subscriptionIdTypeAvp, subscriptionIdDataAvp} ) );
  
                // Granted Service Unit
                DiameterAvp unitTypeAvp = avpFactory.createAvp( 193, 611, unitType );
                DiameterAvp valueDigitsAvp = avpFactory.createAvp( 193, 617, valueDigits );
                DiameterAvp unitValueAvp = avpFactory.createAvp( 193, 612, new DiameterAvp[]{valueDigitsAvp} );
                avps.add( avpFactory.createAvp( 193, 602, new DiameterAvp[]{unitTypeAvp, unitValueAvp} ) );
                
                // Cost Information
                DiameterAvp costAvp = avpFactory.createAvp( 193, 603, chargingValue );
                DiameterAvp currencyCodeAvp = avpFactory.createAvp( 193, 544, 978 );
                avps.add( avpFactory.createAvp( 193, 604, new DiameterAvp[]{costAvp, currencyCodeAvp} ) );   
              }
            }
          }

          DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
          avpArray = avps.toArray(avpArray);
          
          ans.setExtensionAvps( avpArray );
          
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

  public void onAccountingAnswer(net.java.slee.resource.diameter.base.events.AccountingAnswer aca, ActivityContextInterface aci)
  {
    logger.info( "Accounting-Answer received." );
  }

  public void onCapabilitiesExchangeRequest(net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest cer, ActivityContextInterface aci)
  {
    logger.info( "Capabilities-Exchange-Request received." );
  }

  public void onCapabilitiesExchangeAnswer(net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer cea, ActivityContextInterface aci)
  {
    logger.info( "Capabilities-Exchange-Answer received." );
  }

  public void onDeviceWatchdogRequest(net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest dwr, ActivityContextInterface aci)
  {
    logger.info( "Device-Watchdog-Request received." );
  }

  public void onDeviceWatchdogAnswer(net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer dwa, ActivityContextInterface aci)
  {
    logger.info( "Device-Watchdog-Answer received." );
  }

  public void onDisconnectPeerRequest(net.java.slee.resource.diameter.base.events.DisconnectPeerRequest dpr, ActivityContextInterface aci)
  {
    logger.info( "Disconnect-Peer-Request received." );
  }

  public void onDisconnectPeerAnswer(net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer dpa, ActivityContextInterface aci)
  {
    logger.info( "Disconnect-Peer-Answer received." );
  }

  public void onReAuthRequest(net.java.slee.resource.diameter.base.events.ReAuthRequest rar, ActivityContextInterface aci)
  {
    logger.info( "Re-Auth-Request received." );
  }

  public void onReAuthAnswer(net.java.slee.resource.diameter.base.events.ReAuthAnswer raa, ActivityContextInterface aci)
  {
    logger.info( "Re-Auth-Answer received." );
  }

  public void onSessionTerminationRequest(net.java.slee.resource.diameter.base.events.SessionTerminationRequest rar, ActivityContextInterface aci)
  {
    logger.info( "Session-Termination-Request received." );
  }

  public void onSessionTerminationAnswer(net.java.slee.resource.diameter.base.events.SessionTerminationAnswer raa, ActivityContextInterface aci)
  {
    logger.info( "Session-Termination-Answer received." );
  }

  public void onErrorAnswer(net.java.slee.resource.diameter.base.events.ErrorAnswer era, ActivityContextInterface aci)
  {
    logger.info( "Error-Answer received." );
  }

  // ##########################################################################
  // ##                           PRIVATE METHODS                            ##
  // ##########################################################################

  private final static int SUBSCRIPTION_ID_TYPE = 555;
  private final static int SUBSCRIPTION_ID_DATA = 554;
  private final static int SUBSCRIPTION_ID = 553;

  private final static int UNIT_TYPE = 611; 
  private final static int VALUE_DIGITS = 617; 
  private final static int UNIT_VALUE = 612; 
  private final static int EXPONENT = 616;
  private final static int REQUESTED_SERVICE_UNIT = 606; 

  private final static int REQUESTED_ACTION = 615; // 0 = Direct Debit, 1 = Refund Account
  
  private final static int SERVICE_PARAMETER_TYPE = 608; 
  private final static int SERVICE_PARAMETER_VALUE = 609; 
  private final static int SERVICE_PARAMETER_INFO = 607;
  
  private void sendAccountingRequest()
  {
    try
    {
      List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

      avps.add(avpFactory.createAvp(Avp.SESSION_ID, "12345".getBytes() ));

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
      provider.createActivity().sendMessage( ms );
      logger.info( "Sent Custom Message[" + ms + "]" );
    }
    catch (Exception e)
    {
      logger.error( "", e );
    }
  }

}
