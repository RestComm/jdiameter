package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;

import org.jdiameter.api.Message;

public class AbortSessionAnswerImpl extends ExtensionDiameterMessageImpl implements
		AbortSessionAnswer {

	
	public AbortSessionAnswerImpl(Message message) {
        super(message);
    }
	

	@Override
	public String getLongName() {

		return "Abort-Session-Answer";
	}

	@Override
	public String getShortName() {

		return "ASA";
	}
}
