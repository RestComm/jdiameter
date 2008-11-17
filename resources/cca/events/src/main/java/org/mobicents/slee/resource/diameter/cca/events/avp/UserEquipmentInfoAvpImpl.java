/**
 * Start time:20:36:25 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:20:36:25 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UserEquipmentInfoAvpImpl extends GroupedAvpImpl implements
		UserEquipmentInfoAvp {

	/**
	 * tttttt	
	 * @param code
	 * @param vendorId
	 * @param mnd
	 * @param prt
	 * @param value
	 */
	public UserEquipmentInfoAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#getUserEquipmentInfoType()
	 */
	public UserEquipmentInfoType getUserEquipmentInfoType() {
		if(hasUserEquipmentInfoType())
		{
			int v=super.getAvpAsInt32(CreditControlAVPCode.USER_EQUIPMENT_INFO_TYPE);
			return UserEquipmentInfoType.EUI64.fromInt(v);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#getUserEquipmentInfoValue()
	 */
	public byte[] getUserEquipmentInfoValue() {
		if(hasUserEquipmentInfoValue())
		{
			return super.getAvpAsByteArray(CreditControlAVPCode.USER_EQUIPMENT_INFO_VALUE);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#hasUserEquipmentInfoType()
	 */
	public boolean hasUserEquipmentInfoType() {
		return super.hasAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#hasUserEquipmentInfoValue()
	 */
	public boolean hasUserEquipmentInfoValue() {
		return super.hasAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#setUserEquipmentInfoType(net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType)
	 */
	public void setUserEquipmentInfoType(UserEquipmentInfoType type) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsUInt32(CreditControlAVPCode.USER_EQUIPMENT_INFO_TYPE, Long.valueOf(avpRep.getVendorId()).longValue(), type.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCode.USER_EQUIPMENT_INFO_TYPE,  type.getValue(), mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#setUserEquipmentInfoValue(byte[])
	 */
	public void setUserEquipmentInfoValue(byte[] value) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO_VALUE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsByteArray(CreditControlAVPCode.USER_EQUIPMENT_INFO_VALUE, Long.valueOf(avpRep.getVendorId()).longValue(), value, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsByteArray(CreditControlAVPCode.USER_EQUIPMENT_INFO_VALUE, value, mandatoryAvp==1, protectedAvp==1, true);

	}

}
