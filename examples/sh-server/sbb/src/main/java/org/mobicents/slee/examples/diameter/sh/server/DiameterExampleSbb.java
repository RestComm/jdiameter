package org.mobicents.slee.examples.diameter.sh.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
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
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.ShServerActivity;
import net.java.slee.resource.diameter.sh.server.ShServerActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.ShServerProvider;
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

  private ShServerProvider provider = null;
  
  private ShServerMessageFactory messageFactory = null;
  private DiameterShAvpFactory avpFactory = null;
  private ShServerActivityContextInterfaceFactory acif=null;
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
      
      provider = (ShServerProvider) myEnv.lookup("slee/resources/diameter-sh-server-ra-interface");
      logger.info( "Got Provider:" + provider );
      
      messageFactory = provider.getServerMessageFactory();
      logger.info( "Got Message Factory:" + messageFactory );
      
      avpFactory = provider.getAvpFactory();
      logger.info( "Got AVP Factory:" + avpFactory );
      
      acif=(ShServerActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterShServerResourceAdaptor/java.net/0.8.1/acif");
      
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
        
        messageFactory = provider.getServerMessageFactory();
        avpFactory = provider.getAvpFactory();
        
        logger.info( "Performing sanity check..." );
        logger.info( "Provider [" + provider + "]" );
        logger.info( "Message Factory [" + messageFactory + "]" );
        logger.info( "AVP Factory [" + avpFactory + "]" );
        logger.info( "Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3" );

        logger.info( "Connected to " + provider.getPeerCount() + " peers." );
        
        for(DiameterIdentityAvp peer : provider.getConnectedPeers())
          logger.info( "Connected to Peer[" +  peer.stringValue() + "]" );

        //TimerOptions options = new TimerOptions();
        
       // timerFacility.setTimer(aci, null, System.currentTimeMillis() + 5000, options);
        
      
      }
    }
    catch ( Exception e )
    {
      logger.error( "Unable to handle service started event...", e );
    }
  }
  
  public void onTimerEvent(TimerEvent event, ActivityContextInterface aci)
  {

  }
  
  
  public void onUserDataRequest( UserDataRequest event, ActivityContextInterface aci )
  {
	  logger.info(" onUserDataRequest :: "+event);
	  UserDataAnswer answer=((ShServerActivity)aci.getActivity()).createUserDataAnswer(2001, false);
	  try {
		  logger.info(" onUserDataRequest :: Answer:"+answer);
		((ShServerActivity)aci.getActivity()).sendUserDataAnswer(answer);
	} catch (TransactionRequiredLocalException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SLEEException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
 
  public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		logger.info( " Activity Ended["+aci.getActivity()+"]" );
	}
  
}
