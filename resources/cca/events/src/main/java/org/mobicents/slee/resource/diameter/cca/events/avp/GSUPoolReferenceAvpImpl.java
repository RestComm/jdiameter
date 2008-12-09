/**
 * Start time:16:03:57 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_UNIT_TYPE);
			try {
				return CcUnitType.INPUT_OCTETS.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_UNIT_TYPE);
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
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_UNIT_TYPE);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.UNIT_VALUE);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.UNIT_VALUE);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
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
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasCreditControlUnitType()
	 */
	public boolean hasCreditControlUnitType() {
		return super.hasAvp(CreditControlAVPCode.CC_UNIT_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasGSUPoolIdentifier()
	 */
	public boolean hasGSUPoolIdentifier() {
		return super.hasAvp(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return super.hasAvp(CreditControlAVPCode.UNIT_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setCreditControlUnitType(net.java.slee.resource.diameter.cca.events.avp.CcUnitType)
	 */
	public void setCreditControlUnitType(CcUnitType ccUnitType) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_UNIT_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCode.CC_UNIT_TYPE, ccUnitType.getValue(), mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setGSUPoolIdentifier(long)
	 */
	public void setGSUPoolIdentifier(long gsuPoolIdentifier) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER, gsuPoolIdentifier, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
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
 * Start time:16:03:57 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.CC_UNIT_TYPE);
			try {
				return CcUnitType.INPUT_OCTETS.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_UNIT_TYPE);
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
	
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.CC_UNIT_TYPE);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.UNIT_VALUE);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.UNIT_VALUE);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
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
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasCreditControlUnitType()
	 */
	public boolean hasCreditControlUnitType() {
		return super.hasAvp(CreditControlAVPCode.CC_UNIT_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasGSUPoolIdentifier()
	 */
	public boolean hasGSUPoolIdentifier() {
		return super.hasAvp(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasUnitValue()
	 */
	public boolean hasUnitValue() {
		return super.hasAvp(CreditControlAVPCode.UNIT_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setCreditControlUnitType(net.java.slee.resource.diameter.cca.events.avp.CcUnitType)
	 */
	public void setCreditControlUnitType(CcUnitType ccUnitType) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_UNIT_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCode.CC_UNIT_TYPE, ccUnitType.getValue(), mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setGSUPoolIdentifier(long)
	 */
	public void setGSUPoolIdentifier(long gsuPoolIdentifier) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCode.G_S_U_POOL_IDENTIFIER, gsuPoolIdentifier, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
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
