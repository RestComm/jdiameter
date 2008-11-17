/**
 * Start time:17:27:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.REDIRECT_ADDRESS_TYPE);
			try {
				return RedirectAddressType.IPv4_Address.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.REDIRECT_ADDRESS_TYPE);
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
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.REDIRECT_SERVER_ADDRESS);
			try {
				return rawAvp.getUTF8String();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.REDIRECT_SERVER_ADDRESS);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#hasRedirectAddressType()
	 */
	public boolean hasRedirectAddressType() {
		return super.hasAvp(CreditControlAVPCode.REDIRECT_ADDRESS_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#hasRedirectServerAddress()
	 */
	public boolean hasRedirectServerAddress() {
		return super.hasAvp(CreditControlAVPCode.REDIRECT_SERVER_ADDRESS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#setRedirectAddressType(net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType)
	 */
	public void setRedirectAddressType(RedirectAddressType redirectAddressType) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REDIRECT_ADDRESS_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsUInt32(CreditControlAVPCode.REDIRECT_ADDRESS_TYPE, Long.valueOf(avpRep.getVendorId()).longValue(), redirectAddressType.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCode.REDIRECT_ADDRESS_TYPE, redirectAddressType.getValue(), mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#setRedirectServerAddress(java.lang.String)
	 */
	public void setRedirectServerAddress(String redirectServerAddress) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REDIRECT_SERVER_ADDRESS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsString(CreditControlAVPCode.REDIRECT_SERVER_ADDRESS,redirectServerAddress, Long.valueOf(avpRep.getVendorId()).longValue(),false, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsString(CreditControlAVPCode.REDIRECT_SERVER_ADDRESS,redirectServerAddress,false, mandatoryAvp==1, protectedAvp==1, true);

	}

}
