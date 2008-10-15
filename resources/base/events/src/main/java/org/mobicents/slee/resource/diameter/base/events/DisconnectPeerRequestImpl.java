package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.DisconnectCauseType;

public class DisconnectPeerRequestImpl extends DiameterMessageImpl implements DisconnectPeerRequest
{

	public DisconnectPeerRequestImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLongName() {
		return "Disconnect-Peer-Request";
	}

	@Override
	public String getShortName() {

		return "DPR";
	}

	public DisconnectCauseType getDisconnectCause() {
		
		if(!hasDisconnectCause())
			return null;
		Avp avp=super.message.getAvps().getAvp(Avp.DISCONNECT_CAUSE);
		
		
		try {
			DisconnectCauseType type=DisconnectCauseType.fromInt(avp.getInteger32());
			return type;
		} catch (AvpDataException e) {
			
			e.printStackTrace();
		}
		
		return null;
		
	}

	public boolean hasDisconnectCause() {
		return super.message.getAvps().getAvp(Avp.DISCONNECT_CAUSE)!=null;
	}


	public void setDisconnectCause(DisconnectCauseType disconnectCause) {
		super.setAvpAsUInt32(Avp.DISCONNECT_CAUSE, disconnectCause.getValue(), true, true);
		
	}

  
}
