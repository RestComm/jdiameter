package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:16:03:57 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link GSUPoolReferenceAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GSUPoolReferenceAvpImpl extends GroupedAvpImpl implements GSUPoolReferenceAvp {

	private static transient Logger logger = Logger.getLogger(GSUPoolReferenceAvpImpl.class);

	public GSUPoolReferenceAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * getCreditControlUnitType()
	 */
	public CcUnitType getCreditControlUnitType() {
		if (hasCreditControlUnitType()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Unit_Type);
			try {
				return CcUnitType.INPUT_OCTETS.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Unit_Type);
				logger.error("Failure while trying to obtain CC-Unit-Type AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * getGSUPoolIdentifier()
	 */
	public long getGSUPoolIdentifier() {
		if (hasGSUPoolIdentifier()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);

			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.G_S_U_Pool_Identifier);
				logger.error("Failure while trying to obtain G-S-U-Pool-Identifier AVP.", e);
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * getUnitValue()
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
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * hasCreditControlUnitType()
	 */
	public boolean hasCreditControlUnitType() {
		return super.hasAvp(CreditControlAVPCodes.CC_Unit_Type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * hasGSUPoolIdentifier()
	 */
	public boolean hasGSUPoolIdentifier() {
		return super.hasAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return super.hasAvp(CreditControlAVPCodes.Unit_Value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * setCreditControlUnitType
	 * (net.java.slee.resource.diameter.cca.events.avp.CcUnitType)
	 */
	public void setCreditControlUnitType(CcUnitType ccUnitType) {
		if (hasAvp(CreditControlAVPCodes.CC_Unit_Type)) {
			throw new IllegalStateException("AVP CC-Unit-Type is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.CC_Unit_Type, ccUnitType.getValue(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * setGSUPoolIdentifier(long)
	 */
	public void setGSUPoolIdentifier(long gsuPoolIdentifier) {
		if (hasAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier)) {
			throw new IllegalStateException("AVP G-S-U-Pool-Identifier is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.G_S_U_Pool_Identifier, gsuPoolIdentifier, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#
	 * setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public void setUnitValue(UnitValueAvp unitValue) {
		if (hasAvp(CreditControlAVPCodes.Unit_Value)) {
			throw new IllegalStateException("AVP Unit-Value is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Unit_Value);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		// super.avpSet.removeAvp(CreditControlAVPCodes.Unit_Value);
		super.avpSet.addAvp(CreditControlAVPCodes.Unit_Value, unitValue.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
	}

}
