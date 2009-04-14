package org.mobicents.slee.resource.diameter.rf;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.rf.RfMessageFactory;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.jdiameter.api.Stack;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;

/**
 * 
 * RfMessageFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:59:52 AM Apr 14, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfMessageFactoryImpl extends DiameterMessageFactoryImpl implements RfMessageFactory {

  private static Logger logger = Logger.getLogger( RfMessageFactoryImpl.class );

  private final static ApplicationId RF_APPLICATION_ID = ApplicationId.createByAccAppId( _RF_TGPP_VENDOR_ID, _RF_ACC_APP_ID );

  private DiameterMessageFactoryImpl baseMessageFactory;
  
  public RfMessageFactoryImpl(DiameterMessageFactoryImpl baseMessageFactory, Stack stack)
  {
    super(stack);

    this.baseMessageFactory = baseMessageFactory;
  }

  public AccountingRequest createRfAccountingRequest( AccountingRecordType accountingrecordtype )
  {
    AccountingRequest acr = new AccountingRequestImpl( createMessage( Message.ACCOUNTING_REQUEST, RF_APPLICATION_ID, null ) );

    acr.setAccountingRecordType( accountingrecordtype );

    return acr;
  }

  public DiameterMessageFactory getBaseMessageFactory()
  {
    return this.baseMessageFactory;
  }


  protected Message createMessage(int commandCode, ApplicationId applicationId, DiameterAvp[] avps) {
    Message msg = null;

    if (session == null)
    {
      try
      {
        msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
      }
      catch (Exception e) {
        logger.error("", e);
      }
    }
    else
    {
      String destRealm = null;
      String destHost = null;

      if(avps!=null)
      {
        for (DiameterAvp avp : avps)
        {
          if (avp.getCode() == Avp.DESTINATION_REALM)
          {
            destRealm = avp.octetStringValue();
          }
          else if (avp.getCode() == Avp.DESTINATION_HOST)
          {
            destHost = avp.octetStringValue();
          }
        }
      }

      msg = destHost == null ? session.createRequest(commandCode, applicationId, destRealm) : session.createRequest(commandCode, applicationId, destRealm, destHost);
    }

    if (avps != null)
    {
      for (DiameterAvp avp : avps)
      {
        addAvp(avp, msg.getAvps());
      }
    }

    // Do we have a session-id already or shall we make one?
    if (msg.getAvps().getAvp(Avp.SESSION_ID) == null)
    {
      msg.getAvps().addAvp(Avp.SESSION_ID, generateSessionId(), true, false, false);
    }

    msg.setProxiable( true );

    return msg;
  }

}
