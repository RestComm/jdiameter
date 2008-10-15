package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;

import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AddressAvp;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

public class CapabilitiesExchangeRequestImpl extends CapabilitiesExchangeAnswerImpl implements CapabilitiesExchangeRequest
{

	public CapabilitiesExchangeRequestImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLongName() {
		
		return "Capabilities-Exchange-Request";
	}

	@Override
	public String getShortName() {

		return "CER";
	}

	

	
}
