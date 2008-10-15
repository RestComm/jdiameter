package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;

public class ExtensionDiameterMessageImpl extends DiameterMessageImpl implements ExtensionDiameterMessage
{

  @Override
  public String getLongName() {
    //FIXME: baranowb; not documented
    return "Extension-Message";
  }

  @Override
  public String getShortName() {
    //FIXME: baranowb; not documented
    return "EM";
  }

  public ExtensionDiameterMessageImpl(Message message) {
    super(message);
  }

  

}
