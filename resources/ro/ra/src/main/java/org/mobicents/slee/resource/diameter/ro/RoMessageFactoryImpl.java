package org.mobicents.slee.resource.diameter.ro;

import java.util.List;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlMessage;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.ro.RoMessageFactory;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlAnswerImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlRequestImpl;


/**
 * RoMessageFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>6:29:07 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoMessageFactoryImpl extends CreditControlMessageFactoryImpl implements RoMessageFactory {

  public RoMessageFactoryImpl( DiameterMessageFactoryImpl baseFactory, Session session, Stack stack, CreditControlAVPFactory localFactory )
  {
    super( baseFactory, session, stack, localFactory );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoMessageFactory#createRoCreditControlRequest()
   */
  public CreditControlRequest createRoCreditControlRequest()
  {
    return (CreditControlRequest) this.createCreditControlMessage( null, true );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoMessageFactory#createRoCreditControlRequest(java.lang.String)
   */
  public CreditControlRequest createRoCreditControlRequest( String sessionId )
  {
    return (CreditControlRequest) this.createCreditControlMessage( sessionId, true );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoMessageFactory#getBaseMessageFactory()
   */
  public DiameterMessageFactory getBaseMessageFactory()
  {
    return this.baseFactory;
  }

  /**
   * Creates a Credit-Control Message (Request or Answer) with the given Session-Id (if any).
   * 
   * @param sessionId
   * @param isRequest
   * @return
   * @throws IllegalArgumentException
   */
  private CreditControlMessage createCreditControlMessage(String sessionId, boolean isRequest) throws IllegalArgumentException
  {
    ApplicationId applicationId = ApplicationId.createByAuthAppId(_RO_TGPP_VENDOR_ID, _RO_AUTH_APP_ID);

    List<DiameterAvp> list = (List<DiameterAvp>) this.avpList.clone();

    if(sessionId != null)
    {
      DiameterAvp sessionIdAvp;

      try
      {
        sessionIdAvp = this.localFactory.getBaseFactory().createAvp(Avp.SESSION_ID, sessionId);

        // Clean any present Session-Id AVP
        for(DiameterAvp avp : list)
        {
          if(avp.getCode() == Avp.SESSION_ID)
          {
            list.remove( avp );
          }
        }

        // And add this to as close as possible to the header
        list.add(0, sessionIdAvp);
      }
      catch (NoSuchAvpException e)
      {
        throw new IllegalArgumentException(e);
      }
    }

    Message msg = createMessage(CreditControlRequest.commandCode, applicationId, list.size() > 0 ? list.toArray(new DiameterAvp[list.size()]) : null);
    msg.setRequest( isRequest );

    return isRequest ? new CreditControlRequestImpl(msg) : new CreditControlAnswerImpl(msg);
  }

}
