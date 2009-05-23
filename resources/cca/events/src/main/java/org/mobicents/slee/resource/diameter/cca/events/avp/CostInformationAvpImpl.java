package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * Start time:13:38:54 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP {@link CostInformationAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CostInformationAvpImpl extends MoneyLikeAvpImpl implements CostInformationAvp {

	private static transient Logger logger = Logger.getLogger(CostInformationAvpImpl.class);

	public CostInformationAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp#getCostUnit
	 * ()
	 */
	public String getCostUnit() {
		if (hasCostUnit()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Cost_Unit);
			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Cost_Unit);
				logger.error("Failure while trying to obtain Cost-Unit AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp#hasCostUnit
	 * ()
	 */
	public boolean hasCostUnit() {
		return hasAvp(CreditControlAVPCodes.Cost_Unit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp#setCostUnit
	 * (java.lang.String)
	 */
	public void setCostUnit(String costUnit) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Cost_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		// super.avpSet.removeAvp(CreditControlAVPCodes.Cost_Unit);
		super.avpSet.addAvp(CreditControlAVPCodes.Cost_Unit, costUnit.getBytes(), mandatoryAvp == 1, protectedAvp == 1);
	}

}
