package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:40:53 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link UnitValueAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UnitValueAvpImpl extends GroupedAvpImpl implements UnitValueAvp {

	public UnitValueAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#getExpotent()
	 */
	public int getExponent() {
		if (hasExponent()) {
			return super.getAvpAsInt32(CreditControlAVPCodes.Exponent);
		}

		return Integer.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#getValueDigits
	 * ()
	 */
	public long getValueDigits() {
		if (hasValueDigits()) {
			return super.getAvpAsInt64(CreditControlAVPCodes.Value_Digits);
		}

		return Long.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#hasExponent()
	 */
	public boolean hasExponent() {
		return super.hasAvp(CreditControlAVPCodes.Exponent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#hasValueDigits
	 * ()
	 */
	public boolean hasValueDigits() {
		return super.hasAvp(CreditControlAVPCodes.Value_Digits);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#setExpotent
	 * (int)
	 */
	public void setExponent(int exponent) {
		if (hasAvp(CreditControlAVPCodes.Exponent)) {
			throw new IllegalStateException("AVP Exponent is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Exponent, exponent, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#setValueDigits
	 * (long)
	 */
	public void setValueDigits(long digits) {
		if (hasAvp(CreditControlAVPCodes.Value_Digits)) {
			throw new IllegalStateException("AVP Value-Digits is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Value_Digits);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

		super.setAvpAsUInt64(CreditControlAVPCodes.Value_Digits, digits, mandatoryAvp == 1, true);
	}

}
