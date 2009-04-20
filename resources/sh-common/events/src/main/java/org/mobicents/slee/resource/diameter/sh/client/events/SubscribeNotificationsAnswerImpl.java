package org.mobicents.slee.resource.diameter.sh.client.events;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

public class SubscribeNotificationsAnswerImpl extends DiameterShMessageImpl implements SubscribeNotificationsAnswer {

  private static transient Logger logger = Logger.getLogger(SubscribeNotificationsAnswerImpl.class);

  public SubscribeNotificationsAnswerImpl(Message msg)
  {
    super(msg);
    msg.setRequest(false);
    super.longMessageName = "Subscribe-Notification-Answer";
    super.shortMessageName = "SNA";
  }
  
  public Date getExpiryTime()
  {
    return this.hasExpiryTime() ? super.getAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME) : null;
  }

  public boolean hasExpiryTime()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.EXPIRY_TIME) != null;
  }
  
  public void setExpiryTime(Date expiryTime)
  {
    super.setAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME, expiryTime, true, true);
  }

  public ExperimentalResultAvp getExperimentalResult()
  {
    ExperimentalResultAvp avp = null;
    
    try
    {
      Avp rawAvp = super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);
     
      if(rawAvp != null)
      {
        Avp ercAvp = rawAvp.getGrouped().getAvp( DiameterAvpCodes.EXPERIMENTAL_RESULT_CODE );
        Avp vidAvp = rawAvp.getGrouped().getAvp( DiameterAvpCodes.VENDOR_ID );
        
        avp = new ExperimentalResultAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, new byte[]{});
      
        if(ercAvp != null)
        {
          avp.setExperimentalResultCode( ercAvp.getUnsigned32() );
        }
        
        if(vidAvp != null)
        {
          avp.setVendorId( vidAvp.getUnsigned32() );
        }
      }
    }
    catch (AvpDataException e) {
      logger.error( "Unable to decode Experimental-Result AVP contents.", e );
    }

    return avp;
  }

  public boolean hasExperimentalResult()
  {
    return super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT) != null;
  }

  public void setExperimentalResult( ExperimentalResultAvp experimentalResult )
  {
    super.setAvpAsGroup(experimentalResult.getCode(), experimentalResult.getExtensionAvps(), experimentalResult.getMandatoryRule() == 1, true);
  }

}
