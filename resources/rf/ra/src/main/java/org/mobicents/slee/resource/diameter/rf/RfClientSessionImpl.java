package org.mobicents.slee.resource.diameter.rf;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.rf.RfClientSession;
import net.java.slee.resource.diameter.rf.RfMessageFactory;

import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ClientAccSession;
import org.mobicents.slee.resource.diameter.base.AccountingClientSessionActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;

/**
 * 
 * RfServerSessionImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:24:27 AM Apr 8, 2009 
 * <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfClientSessionImpl extends AccountingClientSessionActivityImpl implements RfClientSession {

  RfMessageFactory rfMessageFactory = null;

  /**
   * 
   * @param messageFactory
   * @param avpFactory
   * @param clientSession
   * @param timeout
   * @param destinationHost
   * @param destinationRealm
   * @param endpoint
   */
  public RfClientSessionImpl( DiameterMessageFactoryImpl messageFactory, DiameterAvpFactoryImpl avpFactory, ClientAccSession clientSession, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint, Stack stack )
  {
    super( messageFactory, avpFactory, clientSession, timeout, destinationHost, destinationRealm, endpoint );
    
    this.rfMessageFactory = new RfMessageFactoryImpl(messageFactory, stack);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.rf.RfClientSession#sendAccountingRequest(net.java.slee.resource.diameter.base.events.AccountingRequest)
   */
  public void sendAccountingRequest( AccountingRequest accountingRequest ) throws IOException, IllegalArgumentException
  {
    super.sendAccountRequest( accountingRequest );
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
