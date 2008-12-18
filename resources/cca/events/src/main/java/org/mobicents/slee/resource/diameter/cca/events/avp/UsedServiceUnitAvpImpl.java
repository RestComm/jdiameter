package org.mobicents.slee.resource.diameter.cca.events.avp;

import org.jdiameter.api.Avp;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;

/**
 * Start time:18:48:32 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UsedServiceUnitAvpImpl extends GrantedServiceUnitAvpImpl implements UsedServiceUnitAvp{

	/**
	 * tttttt	
	 * @param code
	 * @param vendorId
	 * @param mnd
	 * @param prt
	 * @param value
	 */
	public UsedServiceUnitAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getTariffChangeUsage()
	 */
	public TariffChangeUsageType getTariffChangeUsage() {
		if(hasTariffChangeUsage())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Tariff_Change_Usage);
			try {
				return TariffChangeUsageType.UNIT_AFTER_TARIFF_CHANGE.fromInt((int) rawAvp.getUnsigned32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Tariff_Change_Usage);
				e.printStackTrace();
			} 
			
		}
		
		return null;
	}

	public boolean hasTariffChangeUsage() {
		return super.hasAvp(CreditControlAVPCodes.Tariff_Change_Usage);
	}

	public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Tariff_Change_Usage);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//super.setAvpAsUInt32(CreditControlAVPCodes.Tariff_Change_Usage, Long.valueOf(avpRep.getVendorId()).longValue() , tariffChangeUsage.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCodes.Tariff_Change_Usage , tariffChangeUsage.getValue(),  true);
	}

}
