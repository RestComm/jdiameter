package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

public class ErrorAnswerImpl extends ExtensionDiameterMessageImpl implements
		ErrorAnswer {

	public ErrorAnswerImpl(Message message) {
		super(message);
		
	}

	public ProxyInfoAvp getProxyInfo() {
		if(hasProxyInfo())
			return super.getProxyInfos()[0];
		else
			return null;
	}

	public boolean hasProxyInfo() {
		ProxyInfoAvp[] infos = super.getProxyInfos();
		if (infos != null && infos.length > 0)
			return true;
		else
			return false;
	}

}
