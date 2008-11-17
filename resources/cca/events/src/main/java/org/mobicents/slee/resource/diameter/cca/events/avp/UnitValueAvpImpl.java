/**
 * Start time:18:40:53 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:40:53 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UnitValueAvpImpl extends GroupedAvpImpl implements UnitValueAvp {

	/**
	 * tttttt	
	 * @param code
	 * @param vendorId
	 * @param mnd
	 * @param prt
	 * @param value
	 */
	public UnitValueAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#getExpotent()
	 */
	public int getExpotent() {
		if(hasExponent())
			return super.getAvpAsInt32(CreditControlAVPCode.EXPONENT);
		return Integer.MIN_VALUE;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#getValueDigits()
	 */
	public long getValueDigits() {
		if(hasValueDigits())
		{
			return super.getAvpAsInt64(CreditControlAVPCode.VALUE_DIGITS);
		}
		return Long.MIN_VALUE;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#hasExponent()
	 */
	public boolean hasExponent() {
		return super.hasAvp(CreditControlAVPCode.EXPONENT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#hasValueDigits()
	 */
	public boolean hasValueDigits() {
		return super.hasAvp(CreditControlAVPCode.VALUE_DIGITS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#setExpotent(int)
	 */
	public void setExpotent(int expotent) {

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.EXPONENT);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//super.setAvpAsUInt32(CreditControlAVPCode.EXPONENT, Long.getLong(avpRep.getVendorId()),expotent, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCode.EXPONENT,expotent, mandatoryAvp==1, protectedAvp==1, true);
		

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp#setValueDigits(long)
	 */
	public void setValueDigits(long digits) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.EXPONENT);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//super.setAvpAsUInt64(CreditControlAVPCode.EXPONENT, Long.getLong(avpRep.getVendorId()),digits, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt64(CreditControlAVPCode.EXPONENT,digits, mandatoryAvp==1, protectedAvp==1, true);

	}

}
