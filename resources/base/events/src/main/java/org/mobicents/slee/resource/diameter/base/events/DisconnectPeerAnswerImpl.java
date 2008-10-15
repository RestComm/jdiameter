package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;

public class DisconnectPeerAnswerImpl extends DiameterMessageImpl implements DisconnectPeerAnswer
{

	public DisconnectPeerAnswerImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLongName() {
		
		return "Disconnect-Peer-Answer";
	}

	@Override
	public String getShortName() {
		
		return "DPA";
	}

	
  
}
