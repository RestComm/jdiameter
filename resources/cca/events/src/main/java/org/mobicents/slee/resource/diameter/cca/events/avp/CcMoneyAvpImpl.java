/**
 * Start time:13:01:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CURRENCY_CODE);
		
			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError("",CreditControlAVPCode.CURRENCY_CODE);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.UNIT_VALUE);
			
			try {
				UnitValueAvp avp=new UnitValueAvpImpl(CreditControlAVPCode.UNIT_VALUE,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return avp;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.UNIT_VALUE);
				e.printStackTrace();
			} 
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasCurrencyCode()
	 */
	public boolean hasCurrencyCode() {
		return hasAvp(CreditControlAVPCode.CURRENCY_CODE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return hasAvp(CreditControlAVPCode.UNIT_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setCurrencyCode(long)
	 */
	public void setCurrencyCode(long code) {
		
		super.avpSet.removeAvp(CreditControlAVPCode.CURRENCY_CODE);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CURRENCY_CODE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.avpSet.addAvp(CreditControlAVPCode.CURRENCY_CODE, code, mandatoryAvp==1, protectedAvp==1);
		

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public void setUnitValue(UnitValueAvp unitValue) {
		super.avpSet.removeAvp(CreditControlAVPCode.UNIT_VALUE);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.UNIT_VALUE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.UNIT_VALUE, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.UNIT_VALUE, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)unitValue).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCode.UNIT_VALUE,unitValue.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCode.UNIT_VALUE,unitValue.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

	}

}
/**
 * Start time:13:01:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CURRENCY_CODE);
		
			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError("",CreditControlAVPCode.CURRENCY_CODE);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.UNIT_VALUE);
			
			try {
				UnitValueAvp avp=new UnitValueAvpImpl(CreditControlAVPCode.UNIT_VALUE,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return avp;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.UNIT_VALUE);
				e.printStackTrace();
			} 
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasCurrencyCode()
	 */
	public boolean hasCurrencyCode() {
		return hasAvp(CreditControlAVPCode.CURRENCY_CODE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return hasAvp(CreditControlAVPCode.UNIT_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setCurrencyCode(long)
	 */
	public void setCurrencyCode(long code) {
		
		super.avpSet.removeAvp(CreditControlAVPCode.CURRENCY_CODE);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CURRENCY_CODE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.avpSet.addAvp(CreditControlAVPCode.CURRENCY_CODE, code, mandatoryAvp==1, protectedAvp==1);
		

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public void setUnitValue(UnitValueAvp unitValue) {
		super.avpSet.removeAvp(CreditControlAVPCode.UNIT_VALUE);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.UNIT_VALUE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.UNIT_VALUE, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.UNIT_VALUE, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)unitValue).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCode.UNIT_VALUE,unitValue.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCode.UNIT_VALUE,unitValue.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

	}

}
