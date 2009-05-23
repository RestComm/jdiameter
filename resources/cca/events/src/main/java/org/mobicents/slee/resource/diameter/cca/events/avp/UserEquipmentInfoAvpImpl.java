package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:20:36:25 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link UserEquipmentInfoAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UserEquipmentInfoAvpImpl extends GroupedAvpImpl implements UserEquipmentInfoAvp {

	public UserEquipmentInfoAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#
	 * getUserEquipmentInfoType()
	 */
	public UserEquipmentInfoType getUserEquipmentInfoType() {
		if (hasUserEquipmentInfoType()) {
			int v = super.getAvpAsInt32(CreditControlAVPCodes.User_Equipment_Info_Type);
			return UserEquipmentInfoType.EUI64.fromInt(v);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#
	 * getUserEquipmentInfoValue()
	 */
	public byte[] getUserEquipmentInfoValue() {
		if (hasUserEquipmentInfoValue()) {
			return super.getAvpAsByteArray(CreditControlAVPCodes.User_Equipment_Info_Value);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#
	 * hasUserEquipmentInfoType()
	 */
	public boolean hasUserEquipmentInfoType() {
		return super.hasAvp(CreditControlAVPCodes.User_Equipment_Info_Type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#
	 * hasUserEquipmentInfoValue()
	 */
	public boolean hasUserEquipmentInfoValue() {
		return super.hasAvp(CreditControlAVPCodes.User_Equipment_Info_Value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#
	 * setUserEquipmentInfoType
	 * (net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType)
	 */
	public void setUserEquipmentInfoType(UserEquipmentInfoType type) {
		if (hasAvp(CreditControlAVPCodes.User_Equipment_Info_Type)) {
			throw new IllegalStateException("AVP User-Equipment-Info-Type is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.User_Equipment_Info_Type, type.getValue(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#
	 * setUserEquipmentInfoValue(byte[])
	 */
	public void setUserEquipmentInfoValue(byte[] value) {
		if (hasAvp(CreditControlAVPCodes.User_Equipment_Info_Value)) {
			throw new IllegalStateException("AVP User-Equipment-Info-Value is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.User_Equipment_Info_Value);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsByteArray(CreditControlAVPCodes.User_Equipment_Info_Value, value, mandatoryAvp == 1, protectedAvp == 1, true);
	}

}
