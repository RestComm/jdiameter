package org.mobicents.slee.resource.diameter.sh.client.events;

import java.nio.charset.Charset;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

public class UserDataAnswerImpl extends ProfileUpdateAnswerImpl implements UserDataAnswer {

	public UserDataAnswerImpl(Message msg) {
		super(msg);
		super.longMessageName="User-Data-Answer";
		super.shortMessageName="UDA";
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
