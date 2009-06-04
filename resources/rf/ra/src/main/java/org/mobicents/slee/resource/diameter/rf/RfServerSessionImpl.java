package org.mobicents.slee.resource.diameter.rf;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.rf.RfMessageFactory;
import net.java.slee.resource.diameter.rf.RfServerSession;

import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ServerAccSession;
import org.mobicents.slee.resource.diameter.base.AccountingServerSessionActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;


/**
 * 
 * RfServerSessionImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:23:43 AM Apr 14, 2009 
 * <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfServerSessionImpl extends AccountingServerSessionActivityImpl implements RfServerSession {

  protected RfMessageFactory rfMessageFactory = null;
  protected DiameterIdentity remoteRealm;

	/**
 * Should contina requests, so we can create answer.
 */
protected ArrayList<DiameterMessageImpl> stateMessages = new ArrayList<DiameterMessageImpl>();
  /**
   * 
   * @param messageFactory
   * @param avpFactory
   * @param serverSession
   * @param timeout
   * @param destinationHost
   * @param destinationRealm
   * @param endpoint
   * @param stack
   */
  public RfServerSessionImpl( DiameterMessageFactoryImpl messageFactory, DiameterAvpFactoryImpl avpFactory, ServerAccSession serverSession, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint, Stack stack )
  {
    super( messageFactory, avpFactory, serverSession, timeout, destinationHost, destinationRealm, endpoint, stack );
    
    this.rfMessageFactory = new RfMessageFactoryImpl(messageFactory, stack);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.rf.RfServerSession#createRfAccountingAnswer()
   */
  public AccountingAnswer createRfAccountingAnswer()
  {
	  
	  
	    AccountingAnswer answer = null;
	  for(int index =0 ;index<stateMessages.size();index++)
	    {
	    	if(stateMessages.get(index).getCommand().getCode() == AccountingRequest.commandCode)
	    	{
	    		AccountingRequest msg = (AccountingRequest) stateMessages.get(index);
	    	
	    		answer = super.createAccountAnswer(msg);
	    		
	    		 if(!answer.hasSessionId() && session!=null)
	    		 {
	    			 answer.setSessionId(session.getSessionId());
	    		 }
	    		 answer.setAcctApplicationId( 3L );
	    		 //FIXME: add destiantion realm?
	    		 
	    		 ((DiameterShMessageImpl)answer).setData(msg);
	    		 break;
	    	}
	    }
	  

    
    return answer;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.rf.RfServerSession#createRfAccountingAnswer(net.java.slee.resource.diameter.base.events.AccountingRequest)
   */
  public AccountingAnswer createRfAccountingAnswer( AccountingRequest acr )
  {
    return super.createAccountAnswer(acr);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.rf.RfServerSession#sendAccountingAnswer(net.java.slee.resource.diameter.base.events.AccountingAnswer)
   */
  public void sendAccountingAnswer( AccountingAnswer accountingAnswer ) throws IOException, IllegalArgumentException
  {
    super.sendAccountAnswer( accountingAnswer );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.rf.RfSession#getRfMessageFactory()
   */
  public RfMessageFactory getRfMessageFactory()
  {
    return this.rfMessageFactory;
  }
  
  public void fetchSessionData(DiameterMessage msg, boolean incoming)
  {
    if(msg.getHeader().isRequest())
    {
      //Well it should always be getting this on request and only once ?
      if(incoming)
      {
       //FIXME: add more ?
        if(this.remoteRealm == null)
        {
          this.remoteRealm = msg.getOriginRealm();
        }
               
        stateMessages.add((DiameterMessageImpl) msg);
        
      }
      else
      {
        //FIXME, do more :)
      }
    }
  }
  
  private void clean(DiameterShMessageImpl msg)
  {
	  if(msg.getData()!=null)
	  {
		  this.stateMessages.remove(msg.removeData());
	  }
  }
}
