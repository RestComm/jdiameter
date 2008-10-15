package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public class DeviceWatchdogRequestImpl extends DiameterMessageImpl implements DeviceWatchdogRequest
{

	public DeviceWatchdogRequestImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLongName() {

		return "Device-Watchdog-Request";
	}

	@Override
	public String getShortName() {
		
		return "DWR";
	}
  

}
