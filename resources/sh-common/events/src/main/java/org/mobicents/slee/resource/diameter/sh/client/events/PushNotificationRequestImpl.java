package org.mobicents.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

public class PushNotificationRequestImpl extends DiameterShMessageImpl implements PushNotificationRequest {

  private static transient Logger logger = Logger.getLogger(PushNotificationRequestImpl.class);

  public PushNotificationRequestImpl(Message msg)
  {
    super(msg);
    msg.setRequest(true);
    super.longMessageName = "Push-Notification-Request";
    super.shortMessageName = "PNR";
  }

  public UserIdentityAvp getUserIdentity()
  {
    if (!hasUserIdentity())
    {
      return null;
    }

    Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);

    try {
      return new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
    }
    catch (AvpDataException e) {
      logger.error( "Unable to decode User-Identity AVP contents.", e );
    }

    return null;
  }

  public boolean hasUserIdentity()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
  }

  public void setUserIdentity(UserIdentityAvp userIdentity)
  {
    super.setAvpAsGroup(userIdentity.getCode(), MessageFactory._SH_VENDOR_ID, new UserIdentityAvp[] { userIdentity }, true, true);
  }

  public boolean hasUserData()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
  }

  public String getUserData()
  {
    try
    {
      return hasUserData() ? new String(super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getRaw()) : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode User-Data AVP contents.", e );
    }

    return null;
  }

  public void setUserData(byte[] userData)
  {
    super.message.getAvps().removeAvp(DiameterShAvpCodes.USER_DATA);
    super.message.getAvps().addAvp(DiameterShAvpCodes.USER_DATA, userData, MessageFactory._SH_VENDOR_ID, true, false);
  }

}
