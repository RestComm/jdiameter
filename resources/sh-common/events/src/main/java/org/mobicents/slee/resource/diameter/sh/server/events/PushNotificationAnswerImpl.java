package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;

public class PushNotificationAnswerImpl extends DiameterShMessageImpl implements PushNotificationAnswer {

  private static transient Logger logger = Logger.getLogger(PushNotificationAnswerImpl.class);

  public PushNotificationAnswerImpl(Message msg)
	{
		super(msg);
		
		msg.setRequest(false);
		
		super.longMessageName = "Push-Notificaton-Answer";
		super.shortMessageName = "PNA";
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
    return super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT)!=null;
  }

  public void setExperimentalResult(ExperimentalResultAvp experimentalResult)
  {
    super.setAvpAsGroup(experimentalResult.getCode(), experimentalResult.getExtensionAvps(), experimentalResult.getMandatoryRule() == 1, true);
  }

}
