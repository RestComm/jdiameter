package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.ReAuthRequestTypeAvp;

public class ReAuthRequestImpl extends ExtensionDiameterMessageImpl implements ReAuthRequest
{

	
	public ReAuthRequestImpl(Message message) {
        super(message);
    }

    public boolean hasReAuthRequestType() {
        return message.getAvps().getAvp(Avp.RE_AUTH_REQUEST_TYPE) != null;
    }

    public ReAuthRequestTypeAvp getReAuthRequestType() {
        return ReAuthRequestTypeAvp.fromInt(getAvpAsInt32(Avp.RE_AUTH_REQUEST_TYPE));
    }

    public void setReAuthRequestType(ReAuthRequestTypeAvp reAuthRequestType) {
        setAvpAsInt32(Avp.RE_AUTH_REQUEST_TYPE, reAuthRequestType.getValue(), true,true);
    }

	
	@Override
	public String getLongName() {
		
		return "Re-Auth-Request";
	}

	@Override
	public String getShortName() {

		return "RAR";
	}

	
}
