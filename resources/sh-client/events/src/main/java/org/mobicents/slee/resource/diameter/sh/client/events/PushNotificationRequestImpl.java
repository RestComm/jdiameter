package org.mobicents.slee.resource.diameter.sh.client.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

public class PushNotificationRequestImpl extends DiameterShMessageImpl implements PushNotificationRequest {

	public PushNotificationRequestImpl(Message msg) {
		super(msg);
		super.longMessageName="Push-Notification-Request";
		super.shortMessageName="PNR";
	}

	public UserIdentityAvp getUserIdentity() {

		if (!hasUserIdentity())
			return null;
		Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);

		UserIdentityAvp returnValue;
		try {
			returnValue = new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
		} catch (AvpDataException e) {

			e.printStackTrace();
			return null;
		}

		return returnValue;
	}

	public boolean hasUserIdentity() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
	}

	public void setUserIdentity(UserIdentityAvp userIdentity) {

		super.setAvpAsGroup(userIdentity.getCode(), new UserIdentityAvp[] { userIdentity }, true, true);
	}

	public boolean hasUserData() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
	}

	public String getUserData() {
		if (hasUserData()) {
			try {
				return new String(super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getRaw());
			} catch (AvpDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	public void setUserData(byte[] userData) {

		super.message.getAvps().removeAvp(DiameterShAvpCodes.USER_DATA);
		super.addAvpAsByteArray(DiameterShAvpCodes.USER_DATA, userData, true);

	}

}
