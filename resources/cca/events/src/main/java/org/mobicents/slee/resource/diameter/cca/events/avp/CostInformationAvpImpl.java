package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * Start time:13:38:54 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CostInformationAvpImpl extends CcMoneyAvpImpl implements
		CostInformationAvp {

	public CostInformationAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	public String getCostUnit() {
		if(hasCostUnit())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Cost_Unit);
			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Cost_Unit);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public boolean hasCostUnit() {
		return hasAvp(CreditControlAVPCodes.Cost_Unit);
	}

	public void setCostUnit(String costUnit) {
		//super.avpSet.removeAvp(CreditControlAVPCodes.Cost_Unit);
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Cost_Unit);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		setAvpAsString(CreditControlAVPCodes.Cost_Unit, costUnit, false, true);
	}

	

}
