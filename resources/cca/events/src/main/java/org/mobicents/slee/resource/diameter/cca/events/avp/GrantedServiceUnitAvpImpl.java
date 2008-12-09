/**
 * Start time:15:04:05 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import java.util.Date;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:15:04:05 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GrantedServiceUnitAvpImpl extends GroupedAvpImpl implements
		GrantedServiceUnitAvp {

	public GrantedServiceUnitAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlInputOctets()
	 */
	public long getCreditControlInputOctets() {
		if(hasCreditControlInputOctets())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_INPUT_OCTETS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_INPUT_OCTETS);
				e.printStackTrace();
			}
		}
		return -1;
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlMoneyAvp()
	 */
	public CcMoneyAvp getCreditControlMoneyAvp() {
		
		if(hasCreditControlMoneyAvp())
		{
			Avp rawGroup=super.avpSet.getAvp(CreditControlAVPCode.CC_MONEY);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_MONEY);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				CcMoneyAvpImpl ccMoney=new CcMoneyAvpImpl(CreditControlAVPCode.CC_MONEY,rawGroup.getVendorId(),rawGroup.isMandatory()?1:0,rawGroup.isEncrypted()?1:0,rawGroup.getRaw());
				return ccMoney;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e.getMessage(), CreditControlAVPCode.CC_MONEY);
				e.printStackTrace();
			} 
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlOutputOctets()
	 */
	public long getCreditControlOutputOctets() {
		if(hasCreditControlOutputOctets())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_OUTPUT_OCTETS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_OUTPUT_OCTETS);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlServiceSpecificUnits()
	 */
	public long getCreditControlServiceSpecificUnits() {
		if(hasCreditControlServiceSpecificUnits())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTime()
	 */
	public long getCreditControlTime() {
		if(hasCreditControlTime())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_TIME);
			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_TIME);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTotalOctets()
	 */
	public long getCreditControlTotalOctets() {
		if(hasCreditControlTotalOctets())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_TOTAL_OCTETS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_TOTAL_OCTETS);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getTariffTimeChange()
	 */
	public Date getTariffTimeChange() {

		if(hasTariffTimeChange())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE);
			try {
				return rawAvp.getTime();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.TARIFF_TIME_CHANGE);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlInputOctets()
	 */
	public boolean hasCreditControlInputOctets() {
		return super.hasAvp(CreditControlAVPCode.CC_INPUT_OCTETS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlMoneyAvp()
	 */
	public boolean hasCreditControlMoneyAvp() {
		return super.hasAvp(CreditControlAVPCode.CC_MONEY);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlOutputOctets()
	 */
	public boolean hasCreditControlOutputOctets() {
		return super.hasAvp(CreditControlAVPCode.CC_OUTPUT_OCTETS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlServiceSpecificUnits()
	 */
	public boolean hasCreditControlServiceSpecificUnits() {
		return super.hasAvp(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTime()
	 */
	public boolean hasCreditControlTime() {
		return super.hasAvp(CreditControlAVPCode.CC_TIME);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTotalOctets()
	 */
	public boolean hasCreditControlTotalOctets() {
		return super.hasAvp(CreditControlAVPCode.CC_TOTAL_OCTETS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasTariffTimeChange()
	 */
	public boolean hasTariffTimeChange() {
		return super.hasAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlInputOctets(long)
	 */
	public void setCreditControlInputOctets(long ttc) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_INPUT_OCTETS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_INPUT_OCTETS, ttc, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlMoneyAvp(net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp)
	 */
	public void setCreditControlMoneyAvp(CcMoneyAvp ccm) {
		super.avpSet.removeAvp(CreditControlAVPCode.CC_MONEY);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_MONEY);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.CC_MONEY, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.CC_MONEY, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)ccm).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCode.CC_MONEY,ccm.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCode.CC_MONEY,ccm.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlOutputOctets(long)
	 */
	public void setCreditControlOutputOctets(long ccoo) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_OUTPUT_OCTETS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_OUTPUT_OCTETS, ccoo, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlServiceSpecificUnits(long)
	 */
	public void setCreditControlServiceSpecificUnits(long ccssu) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS, ccssu, mandatoryAvp==1, protectedAvp==1, true);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTime(long)
	 */
	public void setCreditControlTime(long cct) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_TIME);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_TIME, cct, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTotalOctets(long)
	 */
	public void setCreditControlTotalOctets(long ccto) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_TOTAL_OCTETS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_TOTAL_OCTETS, ccto, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setTariffTimeChange(java.util.Date)
	 */
	public void setTariffTimeChange(Date ttc) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		super.avpSet.addAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE	, ttc, mandatoryAvp==1, protectedAvp==1);

	}

}
/**
 * Start time:15:04:05 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import java.util.Date;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:15:04:05 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GrantedServiceUnitAvpImpl extends GroupedAvpImpl implements
		GrantedServiceUnitAvp {

	public GrantedServiceUnitAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlInputOctets()
	 */
	public long getCreditControlInputOctets() {
		if(hasCreditControlInputOctets())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_INPUT_OCTETS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_INPUT_OCTETS);
				e.printStackTrace();
			}
		}
		return -1;
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlMoneyAvp()
	 */
	public CcMoneyAvp getCreditControlMoneyAvp() {
		
		if(hasCreditControlMoneyAvp())
		{
			Avp rawGroup=super.avpSet.getAvp(CreditControlAVPCode.CC_MONEY);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_MONEY);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				CcMoneyAvpImpl ccMoney=new CcMoneyAvpImpl(CreditControlAVPCode.CC_MONEY,rawGroup.getVendorId(),rawGroup.isMandatory()?1:0,rawGroup.isEncrypted()?1:0,rawGroup.getRaw());
				return ccMoney;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e.getMessage(), CreditControlAVPCode.CC_MONEY);
				e.printStackTrace();
			} 
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlOutputOctets()
	 */
	public long getCreditControlOutputOctets() {
		if(hasCreditControlOutputOctets())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_OUTPUT_OCTETS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_OUTPUT_OCTETS);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlServiceSpecificUnits()
	 */
	public long getCreditControlServiceSpecificUnits() {
		if(hasCreditControlServiceSpecificUnits())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTime()
	 */
	public long getCreditControlTime() {
		if(hasCreditControlTime())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_TIME);
			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_TIME);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTotalOctets()
	 */
	public long getCreditControlTotalOctets() {
		if(hasCreditControlTotalOctets())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_TOTAL_OCTETS);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_TOTAL_OCTETS);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getTariffTimeChange()
	 */
	public Date getTariffTimeChange() {

		if(hasTariffTimeChange())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE);
			try {
				return rawAvp.getTime();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.TARIFF_TIME_CHANGE);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlInputOctets()
	 */
	public boolean hasCreditControlInputOctets() {
		return super.hasAvp(CreditControlAVPCode.CC_INPUT_OCTETS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlMoneyAvp()
	 */
	public boolean hasCreditControlMoneyAvp() {
		return super.hasAvp(CreditControlAVPCode.CC_MONEY);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlOutputOctets()
	 */
	public boolean hasCreditControlOutputOctets() {
		return super.hasAvp(CreditControlAVPCode.CC_OUTPUT_OCTETS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlServiceSpecificUnits()
	 */
	public boolean hasCreditControlServiceSpecificUnits() {
		return super.hasAvp(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTime()
	 */
	public boolean hasCreditControlTime() {
		return super.hasAvp(CreditControlAVPCode.CC_TIME);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTotalOctets()
	 */
	public boolean hasCreditControlTotalOctets() {
		return super.hasAvp(CreditControlAVPCode.CC_TOTAL_OCTETS);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasTariffTimeChange()
	 */
	public boolean hasTariffTimeChange() {
		return super.hasAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlInputOctets(long)
	 */
	public void setCreditControlInputOctets(long ttc) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_INPUT_OCTETS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_INPUT_OCTETS, ttc, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlMoneyAvp(net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp)
	 */
	public void setCreditControlMoneyAvp(CcMoneyAvp ccm) {
		super.avpSet.removeAvp(CreditControlAVPCode.CC_MONEY);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_MONEY);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.CC_MONEY, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.CC_MONEY, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)ccm).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCode.CC_MONEY,ccm.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCode.CC_MONEY,ccm.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlOutputOctets(long)
	 */
	public void setCreditControlOutputOctets(long ccoo) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_OUTPUT_OCTETS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_OUTPUT_OCTETS, ccoo, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlServiceSpecificUnits(long)
	 */
	public void setCreditControlServiceSpecificUnits(long ccssu) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_SERVICE_SPECIFIC_UNITS, ccssu, mandatoryAvp==1, protectedAvp==1, true);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTime(long)
	 */
	public void setCreditControlTime(long cct) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_TIME);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_TIME, cct, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTotalOctets(long)
	 */
	public void setCreditControlTotalOctets(long ccto) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_TOTAL_OCTETS);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCode.CC_TOTAL_OCTETS, ccto, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setTariffTimeChange(java.util.Date)
	 */
	public void setTariffTimeChange(Date ttc) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		super.avpSet.addAvp(CreditControlAVPCode.TARIFF_TIME_CHANGE	, ttc, mandatoryAvp==1, protectedAvp==1);

	}

}
