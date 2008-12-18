package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;


import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:13:01:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CcMoneyAvpImpl extends GroupedAvpImpl implements CcMoneyAvp {

	public CcMoneyAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#getCurrencyCode()
	 */
	public long getCurrencyCode() {
		
		if(hasCurrencyCode())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Currency_Code);
		
			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError("",CreditControlAVPCodes.Currency_Code);
				e.printStackTrace();
		
			}
		
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#getUnitValue()
	 */
	public UnitValueAvp getUnitValue() {
		if(hasUnitValue())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Unit_Value);
			
			try {
				UnitValueAvp avp=new UnitValueAvpImpl(CreditControlAVPCodes.Unit_Value,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return avp;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Unit_Value);
				e.printStackTrace();
			} 
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasCurrencyCode()
	 */
	public boolean hasCurrencyCode() {
		return hasAvp(CreditControlAVPCodes.Currency_Code);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return hasAvp(CreditControlAVPCodes.Unit_Value);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setCurrencyCode(long)
	 */
	public void setCurrencyCode(long code) {
		
		super.avpSet.removeAvp(CreditControlAVPCodes.Currency_Code);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Currency_Code);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.avpSet.addAvp(CreditControlAVPCodes.Currency_Code, code, mandatoryAvp==1, protectedAvp==1);
		

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public void setUnitValue(UnitValueAvp unitValue) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Unit_Value);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Unit_Value);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.Unit_Value, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.Unit_Value, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)unitValue).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCodes.Unit_Value,unitValue.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCodes.Unit_Value,unitValue.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

	}

}
