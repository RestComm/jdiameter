package org.mobicents.slee.resource.diameter.cca.events.avp;

import java.util.Date;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.CC_Input_Octets);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Input_Octets);
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
			Avp rawGroup=super.avpSet.getAvp(CreditControlAVPCodes.CC_Money);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Money);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				CcMoneyAvpImpl ccMoney=new CcMoneyAvpImpl(CreditControlAVPCodes.CC_Money,rawGroup.getVendorId(),rawGroup.isMandatory()?1:0,rawGroup.isEncrypted()?1:0,rawGroup.getRaw());
				return ccMoney;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e.getMessage(), CreditControlAVPCodes.CC_Money);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.CC_Output_Octets);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Output_Octets);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.CC_Service_Specific_Units);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Service_Specific_Units);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.CC_Time);
			try {
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Time);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.CC_Total_Octets);
			try {
				return rawAvp.getUnsigned64();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Total_Octets);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Tariff_Time_Change);
			try {
				return rawAvp.getTime();
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Tariff_Time_Change);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlInputOctets()
	 */
	public boolean hasCreditControlInputOctets() {
		return super.hasAvp(CreditControlAVPCodes.CC_Input_Octets);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlMoneyAvp()
	 */
	public boolean hasCreditControlMoneyAvp() {
		return super.hasAvp(CreditControlAVPCodes.CC_Money);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlOutputOctets()
	 */
	public boolean hasCreditControlOutputOctets() {
		return super.hasAvp(CreditControlAVPCodes.CC_Output_Octets);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlServiceSpecificUnits()
	 */
	public boolean hasCreditControlServiceSpecificUnits() {
		return super.hasAvp(CreditControlAVPCodes.CC_Service_Specific_Units);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTime()
	 */
	public boolean hasCreditControlTime() {
		return super.hasAvp(CreditControlAVPCodes.CC_Time);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTotalOctets()
	 */
	public boolean hasCreditControlTotalOctets() {
		return super.hasAvp(CreditControlAVPCodes.CC_Total_Octets);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasTariffTimeChange()
	 */
	public boolean hasTariffTimeChange() {
		return super.hasAvp(CreditControlAVPCodes.Tariff_Time_Change);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlInputOctets(long)
	 */
	public void setCreditControlInputOctets(long ttc) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Input_Octets);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCodes.CC_Input_Octets, ttc, true, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlMoneyAvp(net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp)
	 */
	public void setCreditControlMoneyAvp(CcMoneyAvp ccm) {
		super.avpSet.removeAvp(CreditControlAVPCodes.CC_Money);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Money);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.CC_Money, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.CC_Money, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)ccm).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCodes.CC_Money,ccm.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCodes.CC_Money,ccm.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlOutputOctets(long)
	 */
	public void setCreditControlOutputOctets(long ccoo) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Output_Octets);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCodes.CC_Output_Octets, ccoo, true, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlServiceSpecificUnits(long)
	 */
	public void setCreditControlServiceSpecificUnits(long ccssu) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Service_Specific_Units);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCodes.CC_Service_Specific_Units, ccssu, true, true);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTime(long)
	 */
	public void setCreditControlTime(long cct) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Time);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCodes.CC_Time, cct, true, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTotalOctets(long)
	 */
	public void setCreditControlTotalOctets(long ccto) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Total_Octets);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt64(CreditControlAVPCodes.CC_Total_Octets, ccto, true, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setTariffTimeChange(java.util.Date)
	 */
	public void setTariffTimeChange(Date ttc) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Tariff_Time_Change);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		super.avpSet.addAvp(CreditControlAVPCodes.Tariff_Time_Change	, ttc, mandatoryAvp==1, protectedAvp==1);

	}

}
