package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.ReAuthAnswer;

import org.jdiameter.api.Message;

public class ReAuthAnswerImpl extends ExtensionDiameterMessageImpl implements ReAuthAnswer
{

	public ReAuthAnswerImpl(Message message) {
        super(message);
    }
	@Override
	public String getLongName() {
		
		return "Re-Auth-Answer";
	}

	@Override
	public String getShortName() {

		return "RAA";
	}

	

  
}
