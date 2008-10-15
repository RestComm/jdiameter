package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;

public class SessionTerminationRequestImpl  extends SessionTerminationAnswerImpl implements SessionTerminationRequest{

	@Override
	public String getLongName() {
		
		return "Session-Termination-Request";
	}

	@Override
	public String getShortName() {

		return "STR";
	}

	public SessionTerminationRequestImpl(Message message) {
        super(message);
    }

    public boolean hasTerminationCause() {
        return message.getAvps().getAvp(Avp.TERMINATION_CAUSE) != null;
    }

    public TerminationCauseType getTerminationCause() {
        return TerminationCauseType.fromInt(getAvpAsInt32(Avp.TERMINATION_CAUSE));
    }

    public void setTerminationCause(TerminationCauseType terminationCause) {
        setAvpAsInt32(Avp.TERMINATION_CAUSE, terminationCause.getValue(), true);
    }
	

}
