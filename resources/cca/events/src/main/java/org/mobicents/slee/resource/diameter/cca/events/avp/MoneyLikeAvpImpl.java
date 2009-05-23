/**
 * Start time:14:12:35 2009-05-23<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:14:12:35 2009-05-23<br>
 * Project: diameter-parent<br>
 * Super avp for avps of certain structure:
 * 
 * <pre>
 *                HEADER NAME ::= &lt; AVP Header: CODE &gt;
 *                                   { Unit-Value }
 *                                   [ Currency-Code ]
 * </pre>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MoneyLikeAvpImpl extends GroupedAvpImpl {

	private final static Logger logger = Logger.getLogger(MoneyLikeAvpImpl.class);
	/**
	 * 
	 * @param code
	 * @param vendorId
	 * @param mnd
	 * @param prt
	 * @param value
	 */
	public MoneyLikeAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#getCurrencyCode
	 * ()
	 */
	public long getCurrencyCode() {
		if (hasCurrencyCode()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Currency_Code);

			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Currency_Code);
				logger.error("Failure while trying to obtain Currency-Code AVP.", e);
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#getUnitValue()
	 */
	public UnitValueAvp getUnitValue() {
		if (hasUnitValue()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Unit_Value);

			try {
				return new UnitValueAvpImpl(CreditControlAVPCodes.Unit_Value, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Unit_Value);
				logger.error("Failure while trying to obtain Unit-Value AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasCurrencyCode
	 * ()
	 */
	public boolean hasCurrencyCode() {
		return hasAvp(CreditControlAVPCodes.Currency_Code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return hasAvp(CreditControlAVPCodes.Unit_Value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setCurrencyCode
	 * (long)
	 */
	public void setCurrencyCode(long code) {
		if (hasCurrencyCode()) {
			throw new IllegalStateException("AVP Currency-Code is already present in message and cannot be overwritten.");
		} else {
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Currency_Code);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

			// super.avpSet.removeAvp(CreditControlAVPCodes.Currency_Code);
			super.avpSet.addAvp(CreditControlAVPCodes.Currency_Code, code, mandatoryAvp == 1, protectedAvp == 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setUnitValue
	 * (net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public void setUnitValue(UnitValueAvp unitValue) {
		if (hasUnitValue()) {
			throw new IllegalStateException("AVP Unit-Value is already present in message and cannot be overwritten.");
		} else {
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Unit_Value);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

			// super.avpSet.removeAvp(CreditControlAVPCodes.Unit_Value);
			super.avpSet.addAvp(CreditControlAVPCodes.Unit_Value, unitValue.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
		}
	}
}
