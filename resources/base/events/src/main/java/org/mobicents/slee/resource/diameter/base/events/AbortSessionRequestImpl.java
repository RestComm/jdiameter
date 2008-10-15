package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AbortSessionRequest;

import org.jdiameter.api.Message;

public class AbortSessionRequestImpl extends ExtensionDiameterMessageImpl implements AbortSessionRequest {


	public AbortSessionRequestImpl(Message message) {
        super(message);
    }
	@Override
	public String getLongName() {

		return "Abort-Session-Request";
	}

	@Override
	public String getShortName() {
		
		return "ASR";
	}

	
  
}
