/**
 * Start time:18:25:13 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:25:13 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscriptionIdAvpImpl extends GroupedAvpImpl implements
		SubscriptionIdAvp {

	/**
	 * @param code
	 * @param vendorId
	 * @param mnd
	 * @param prt
	 * @param value
	 */
	public SubscriptionIdAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	public String getSubscriptionIdData() {
		if(hasSubscriptionIdData())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID_DATA);
			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.SUBSCRIPTION_ID_DATA);
				e.printStackTrace();
			}
		}
		
		return null;
		
		
	}

	public SubscriptionIdType getSubscriptionIdType() {
		if(hasSubscriptionIdType())
		{
			int v=(int) super.getAvpAsUInt32(CreditControlAVPCode.SUBSCRIPTION_ID_TYPE);
			return SubscriptionIdType.END_USER_E164.fromInt(v);
		}
			
			
		return null;
	}

	public boolean hasSubscriptionIdData() {
		return super.hasAvp(CreditControlAVPCode.SUBSCRIPTION_ID_DATA);
	}

	public boolean hasSubscriptionIdType() {
		return super.hasAvp(CreditControlAVPCode.SUBSCRIPTION_ID_TYPE);
	}

	public void setSubscriptionIdData(String data) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID_DATA);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsString(CreditControlAVPCode.SUBSCRIPTION_ID_DATA,data, Long.valueOf(avpRep.getVendorId()).longValue(),  mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsString(CreditControlAVPCode.SUBSCRIPTION_ID_DATA,data,   mandatoryAvp==1, protectedAvp==1, true);
		
	}

	public void setSubscriptionIdType(SubscriptionIdType type) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsUInt32(CreditControlAVPCode.SUBSCRIPTION_ID_TYPE, Long.valueOf(avpRep.getVendorId()).longValue(), type.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCode.SUBSCRIPTION_ID_TYPE, type.getValue(), mandatoryAvp==1, protectedAvp==1, true);
	}

	

}
