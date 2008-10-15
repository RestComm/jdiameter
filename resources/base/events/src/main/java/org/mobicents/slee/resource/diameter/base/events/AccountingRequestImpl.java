package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AccountingRequest;

import org.jdiameter.api.Message;

/**
 * 
 * AccountingRequestImpl.java
 *
 * <br>Super project:  mobicents
 * <br>5:57:35 PM Jun 20, 2008 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author Erick Svenson
 */
public class AccountingRequestImpl extends AccountingAnswerImpl implements AccountingRequest
{

	public AccountingRequestImpl(Message message)
	{
		super(message);
	}

	@Override
	public String getLongName()
	{
		return "Accounting-Request";
	}

	@Override
	public String getShortName() 
	{
		return "ACR";
	}

	public boolean isValid()
	{
	  // One of Acct-Application-Id and Vendor-Specific-Application-Id AVPs
	  // MUST be present.  If the Vendor-Specific-Application-Id grouped AVP
	  // is present, it must have an Acct-Application-Id inside.
	  
    if(!this.message.isRequest())
    {
      return false;
    }
    else if(!this.hasAccountingRealtimeRequired())
    {
      if(!this.hasVendorSpecificApplicationId())
      {
        return false;
      }
      else
      {
        if(this.getVendorSpecificApplicationId().getAcctApplicationId() == -1)
        {
          return false;
        }
      }
    }
    
    return true;
	}
 
}
