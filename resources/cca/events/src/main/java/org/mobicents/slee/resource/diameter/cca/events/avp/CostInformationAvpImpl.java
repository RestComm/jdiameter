/**
 * Start time:13:38:54 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;

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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.COST_UNIT);
			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(e.getMessage(), CreditControlAVPCode.COST_UNIT);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public boolean hasCostUnit() {
		return hasAvp(CreditControlAVPCode.COST_UNIT);
	}

	public void setCostUnit(String costUnit) {
		super.avpSet.removeAvp(CreditControlAVPCode.COST_UNIT);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.COST_UNIT);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		setAvpAsString(CreditControlAVPCode.COST_UNIT, costUnit, false, mandatoryAvp==1, protectedAvp==1, true);
	}

	

}
