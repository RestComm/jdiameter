package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

public class ProfileUpdateRequestImpl extends DiameterShMessageImpl implements ProfileUpdateRequest {

	public ProfileUpdateRequestImpl(Message msg) {
		super(msg);
		super.longMessageName="Profile-Update-Request";
		super.shortMessageName="PUR";
	}

	public DataReferenceType getDataReference() {
		if (hasDataReference()) {
			Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.DATA_REFERENCE);
			try {
				return DataReferenceType.fromInt(rawAvp.getInteger32());
			} catch (AvpDataException e) {

				e.printStackTrace();
			}

		}

		return null;
	}

	public UserIdentityAvp getUserIdentity() {

		if (hasUserIdentity()) {
			Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);
			try {

				UserIdentityAvpImpl uia = new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp
						.getRaw());
				return uia;
			} catch (AvpDataException e) {

				e.printStackTrace();
			}
		}

		return null;
	}

	public boolean hasDataReference() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.DATA_REFERENCE) != null;
	}

	public boolean hasUserData() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
	}

	public boolean hasUserIdentity() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
	}

	public void setDataReference(DataReferenceType dataReference) {

		super.setAvpAsInt32(DiameterShAvpCodes.DATA_REFERENCE, dataReference.getValue(), true, true);

	}

	public void setUserIdentity(UserIdentityAvp userIdentity) {

		super.setAvpAsGroup(userIdentity.getCode(), new UserIdentityAvp[] { userIdentity }, true, true);
	}

	public String getUserData() {
		if (hasUserData()) {
			Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA);
			try {

				String uia = new String(rawAvp.getRaw());
				return uia;
			} catch (AvpDataException e) {

				e.printStackTrace();
			}
		}

		return null;
	}

	public void setUserData(byte[] userData) {

		// FIXME:Dictionary
		super.message.getAvps().removeAvp(DiameterShAvpCodes.USER_DATA);
		super.addAvpAsByteArray(DiameterShAvpCodes.USER_DATA, userData, true);

	}

}
