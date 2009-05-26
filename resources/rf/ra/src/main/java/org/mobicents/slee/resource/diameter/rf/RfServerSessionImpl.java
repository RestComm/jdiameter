package org.mobicents.slee.resource.diameter.rf;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.rf.RfMessageFactory;
import net.java.slee.resource.diameter.rf.RfServerSession;

import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ServerAccSession;
import org.mobicents.slee.resource.diameter.base.AccountingServerSessionActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;

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

  RfMessageFactory rfMessageFactory = null;
  
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
    AccountingAnswer answer = messageFactory.createAccountingAnswer();
    
    if(sessionId != null)
    {
      answer.setSessionId( sessionId );
    }
    
    answer.setAcctApplicationId( 3L );
    
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
}
