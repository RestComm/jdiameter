package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

import org.jdiameter.api.Avp;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:17:27:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RedirectServerAvpImpl extends GroupedAvpImpl implements
		RedirectServerAvp {

	public RedirectServerAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
	
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#getRedirectAddressType()
	 */
	public RedirectAddressType getRedirectAddressType() {
		if(hasRedirectAddressType())
		{
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Address_Type);
			try {
				return RedirectAddressType.IPv4_Address.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Redirect_Address_Type);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#getRedirectServerAddress()
	 */
	public String getRedirectServerAddress() {
		if(hasRedirectServerAddress())
		{
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Server_Address);
			try {
				return rawAvp.getUTF8String();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Redirect_Server_Address);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#hasRedirectAddressType()
	 */
	public boolean hasRedirectAddressType() {
		return super.hasAvp(CreditControlAVPCodes.Redirect_Address_Type);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#hasRedirectServerAddress()
	 */
	public boolean hasRedirectServerAddress() {
		return super.hasAvp(CreditControlAVPCodes.Redirect_Server_Address);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#setRedirectAddressType(net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType)
	 */
	public void setRedirectAddressType(RedirectAddressType redirectAddressType) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Redirect_Address_Type);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsUInt32(CreditControlAVPCodes.Redirect_Address_Type, Long.valueOf(avpRep.getVendorId()).longValue(), redirectAddressType.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCodes.Redirect_Address_Type, redirectAddressType.getValue(), true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#setRedirectServerAddress(java.lang.String)
	 */
	public void setRedirectServerAddress(String redirectServerAddress) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Redirect_Server_Address);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsString(CreditControlAVPCodes.Redirect_Address_Address,redirectServerAddress, Long.valueOf(avpRep.getVendorId()).longValue(),false, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsString(CreditControlAVPCodes.Redirect_Server_Address,redirectServerAddress,false,  true);

	}

}
