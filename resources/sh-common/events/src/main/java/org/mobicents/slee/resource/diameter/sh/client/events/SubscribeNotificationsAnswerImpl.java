package org.mobicents.slee.resource.diameter.sh.client.events;

import java.util.Date;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

public class SubscribeNotificationsAnswerImpl extends ProfileUpdateAnswerImpl implements SubscribeNotificationsAnswer {



	public SubscribeNotificationsAnswerImpl(Message msg) {
		super(msg);
		super.longMessageName="Subscribe-Notification-Answer";
		super.shortMessageName="SNA";
	}



	public Date getExpiryTime() {
		if(hasExpiryTime())
		{
			
			return super.getAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME);
			
		}
		return null;
	}



	public boolean hasExpiryTime() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.EXPIRY_TIME)!=null;
	}



	public void setExpiryTime(Date expiryTime) {
		super.setAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME, expiryTime, true, true);

	}

}
