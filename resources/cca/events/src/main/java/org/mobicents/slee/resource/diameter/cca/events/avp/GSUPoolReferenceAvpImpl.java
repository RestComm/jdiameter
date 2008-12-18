package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:16:03:57 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GSUPoolReferenceAvpImpl extends GroupedAvpImpl implements
		GSUPoolReferenceAvp {

	public GSUPoolReferenceAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#getCreditControlUnitType()
	 */
	public CcUnitType getCreditControlUnitType() {
		
		if(hasCreditControlUnitType())
		{
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.CC_Unit_Type);
			try {
				return CcUnitType.INPUT_OCTETS.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Unit_Type);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#getGSUPoolIdentifier()
	 */
	public long getGSUPoolIdentifier() {
		if(hasGSUPoolIdentifier())
		{
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.G_S_U_Pool_Identifier);
				e.printStackTrace();
			}
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#getUnitValue()
	 */
	public UnitValueAvp getUnitValue() {
		if(hasUnitValue())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Unit_Value);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Unit_value);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				UnitValueAvp avp=new UnitValueAvpImpl(CreditControlAVPCodes.Unit_Value,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return avp;
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Unit_Value);
				e.printStackTrace();
			} 
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasCreditControlUnitType()
	 */
	public boolean hasCreditControlUnitType() {
		return super.hasAvp(CreditControlAVPCodes.CC_Unit_Type);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasGSUPoolIdentifier()
	 */
	public boolean hasGSUPoolIdentifier() {
		return super.hasAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return super.hasAvp(CreditControlAVPCodes.Unit_Value);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setCreditControlUnitType(net.java.slee.resource.diameter.cca.events.avp.CcUnitType)
	 */
	public void setCreditControlUnitType(CcUnitType ccUnitType) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Unit_Type);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCodes.CC_Unit_Type, ccUnitType.getValue(), true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setGSUPoolIdentifier(long)
	 */
	public void setGSUPoolIdentifier(long gsuPoolIdentifier) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCodes.G_S_U_Pool_Identifier, gsuPoolIdentifier,  true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public void setUnitValue(UnitValueAvp unitValue) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Unit_Value);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Unit_Value);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.Unit_Value, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.Unit_Value, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)unitValue).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCodes.Unit_Value,unitValue.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCodes.Unit_Value,unitValue.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		

	}

}
