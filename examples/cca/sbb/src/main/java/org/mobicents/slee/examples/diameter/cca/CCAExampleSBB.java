/**
 * Start time:21:45:41 2008-12-14<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.examples.diameter.cca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlProvider;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;

/**
 * Start time:21:45:41 2008-12-14<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CCAExampleSBB implements javax.slee.Sbb {
	private static Logger logger = Logger.getLogger(CCAExampleSBB.class);

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private CreditControlActivityContextInterfaceFactory acif=null;
	
	private CreditControlProvider provider = null;

	private CreditControlMessageFactory messageFactory = null;

	private CreditControlAVPFactory avpFactory = null;

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
	      provider=(CreditControlProvider) myEnv.lookup("slee/resources/diameter-cca-ra-interface");
	      logger.info( "Got Provider:" + provider );
	      
	      messageFactory=provider.getCreditControlMessageFactory();
	      logger.info( "Got Message Factory:" + messageFactory );
	      avpFactory=provider.getCreditControlAVPFactory();
	      logger.info( "Got AVP Factory:" + avpFactory );
	      acif=(CreditControlActivityContextInterfaceFactory) myEnv.lookup("slee/resources/CCAResourceAdaptor/java.net/0.8.1/acif");
	      
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
	          
	      }
	      
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	 }
	  
	  public void onTimerEvent(TimerEvent event, ActivityContextInterface aci)
	  {
		 doSendEventCCR();
	  }
	  
	  
	  private void doSendEventCCR()
	  {
		
		  //Create ACTIVITY: WE DONT HAVE CREATE METHOD FOR IDENTITY>.... what a mess
		  //DiameterIdentityAvp destinationHost=avpFactory.getBaseFactory().create(Avp.DESTINATION_HOST, "aaa://" + destinationIP + ":3868".getBytes() );
		  //DiameterIdentityAvp destinationRealm=avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() );
		  
		  try {
			CreditControlClientSession session=this.provider.createClientSession();
			ActivityContextInterface localACI=this.acif.getActivityContextInterface(session);
			localACI.attach(this.getSbbContext().getSbbLocalObject());
			
			CreditControlRequest request=session.createCreditControlRequest();
			List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
			
			
			avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_HOST, "aaa://" + originIP + ":1812".getBytes() ));
		    avps.add(avpFactory.getBaseFactory().createAvp(Avp.ORIGIN_REALM, "mobicents.org".getBytes() ));
		      
		    avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, "aaa://" + destinationIP + ":3868".getBytes() ));
		    avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, "mobicents.org".getBytes() ));
			avps.add(avpFactory.getBaseFactory().createAvp(CreditControlAVPCodes.CC_Request_Type, 4l));
			avps.add(avpFactory.getBaseFactory().createAvp(CreditControlAVPCodes.CC_Request_Number, 0l));
			RequestedServiceUnitAvp rsu=this.avpFactory.createRequestedServiceUnit();
			CcMoneyAvp ccMoney=this.avpFactory.createCcMoney();
			ccMoney.setCurrencyCode(100);
			ccMoney.setUnitValue(this.avpFactory.createUnitValue());
			rsu.setCreditControlInputOctets(10);
			rsu.setCreditControlMoneyAvp(ccMoney);
			rsu.setCreditControlServiceSpecificUnits(1000);
			rsu.setCreditControlTime(100);
			rsu.setCreditControlTotalOctets(5000);
			avps.add(rsu);
			avps.add(avpFactory.getBaseFactory().createAvp(CreditControlAVPCodes.Requested_Action, 0));
			
			
			//Now create & send
			request.setExtensionAvps(avps.toArray(new DiameterAvp[avps.size()]));
	
			logger.info("About to send:\n"+request);
			
			session.sendCreditControlRequest(request);
			
			
			
		} catch (CreateActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAvpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AvpNotAllowedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	  
	  
	  
	  
	  public void onCreditControlRequest(CreditControlRequest request, ActivityContextInterface aci )
	  {
		  
		  
	  }
	  public void onCreditControlAnswer(CreditControlAnswer answer, ActivityContextInterface aci )
	  {
		  
		  logger.info("Received CCA :D");
		  
		  
	  }
	  
}
