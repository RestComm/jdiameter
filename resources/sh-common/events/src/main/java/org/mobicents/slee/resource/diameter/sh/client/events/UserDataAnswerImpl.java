package org.mobicents.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

import org.apache.log4j.Logger;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;

public class UserDataAnswerImpl extends ProfileUpdateAnswerImpl implements UserDataAnswer {

  private static transient Logger logger = Logger.getLogger(UserDataAnswerImpl.class);

  public UserDataAnswerImpl(Message msg)
  {
    super(msg);
    msg.setRequest(false);
    super.longMessageName = "User-Data-Answer";
    super.shortMessageName = "UDA";
  }

  public boolean hasUserData()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
  }

  public String getUserData()
  {
    if (hasUserData())
    {
      try
      {
        return new String(super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getRaw());
      }
      catch (AvpDataException e) {
        logger.error( "Unable to decode User-Data AVP contents.", e );
      }
    }

    return null;
  }

  public void setUserData(byte[] userData)
  {
    super.message.getAvps().removeAvp(DiameterShAvpCodes.USER_DATA);
    super.message.getAvps().addAvp(DiameterShAvpCodes.USER_DATA, userData, 10415L, true, false);
  }

}
