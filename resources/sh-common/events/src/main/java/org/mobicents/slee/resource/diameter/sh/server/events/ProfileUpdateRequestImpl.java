package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

public class ProfileUpdateRequestImpl extends DiameterShMessageImpl implements ProfileUpdateRequest {

  private static transient Logger logger = Logger.getLogger(ProfileUpdateRequestImpl.class);

  public ProfileUpdateRequestImpl(Message msg)
	{
		super(msg);
		
		msg.setRequest(true);
		
		super.longMessageName = "Profile-Update-Request";
		super.shortMessageName = "PUR";
	}

	public DataReferenceType getDataReference()
	{
		try
    {
      return hasDataReference() ? DataReferenceType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.DATA_REFERENCE).getInteger32()) : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode Data-Reference AVP contents.", e );
    }
    
    return null;
	}

	public UserIdentityAvp getUserIdentity()
	{
		if (hasUserIdentity())
		{
			Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);
			
			try
			{
				return new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
			}
			catch (AvpDataException e) {
	      logger.error( "Unable to decode User-Identity AVP contents.", e );
			}
		}

		return null;
	}

	public boolean hasDataReference()
	{
		return super.message.getAvps().getAvp(DiameterShAvpCodes.DATA_REFERENCE) != null;
	}

	public boolean hasUserData()
	{
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
	}

	public boolean hasUserIdentity()
	{
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
	}

	public void setDataReference(DataReferenceType dataReference)
	{
		super.setAvpAsInt32(DiameterShAvpCodes.DATA_REFERENCE, dataReference.getValue(), true, true);
	}

	public void setUserIdentity(UserIdentityAvp userIdentity)
	{
		super.setAvpAsGroup(userIdentity.getCode(), new UserIdentityAvp[] { userIdentity }, true, true);
	}

	public String getUserData()
	{
		try
    {
      return hasUserData() ? super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getUTF8String() : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode User-Data AVP contents.", e );
    }
    
    return null;
	}

	public void setUserData(byte[] userData)
	{
		super.message.getAvps().removeAvp(DiameterShAvpCodes.USER_DATA);
    super.message.getAvps().addAvp(DiameterShAvpCodes.USER_DATA, userData, 10415L, true, false);
	}

}
