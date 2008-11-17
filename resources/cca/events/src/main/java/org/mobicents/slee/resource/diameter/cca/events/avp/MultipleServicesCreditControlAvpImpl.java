/**
 * Start time:16:29:27 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:16:29:27 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MultipleServicesCreditControlAvpImpl extends GroupedAvpImpl
		implements MultipleServicesCreditControlAvp {

	public MultipleServicesCreditControlAvpImpl(int code, long vendorId,
			int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getFinalUnitIndication()
	 */
	public FinalUnitIndicationAvp getFinalUnitIndication() {
		if(hasFinalUnitIndication())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			FinalUnitIndicationAvp result=null;
			try {
				result = new FinalUnitIndicationAvpImpl(CreditControlAVPCode.FINAL_UNIT_INDICATION,Long.valueOf(avpRep.getVendorId()),mandatoryAvp,protectedAvp,rawAvp.getRaw());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.FINAL_UNIT_INDICATION);
				e.printStackTrace();
			} 
			return result;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getGrantedServiceUnit()
	 */
	public GrantedServiceUnitAvp getGrantedServiceUnit() {
		if(hasGrantedServiceUnit())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			GrantedServiceUnitAvp result=null;
			try {
				result = new GrantedServiceUnitAvpImpl(CreditControlAVPCode.GRANTED_SERVICE_UNIT,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.GRANTED_SERVICE_UNIT);
				e.printStackTrace();
			} 
			return result;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getGsuPoolReferences()
	 */
	public GSUPoolReferenceAvp[] getGsuPoolReferences() {
		if(super.hasAvp(CreditControlAVPCode.G_S_U_POOL_REFERENCE))
		{
			AvpSet rawAvps=super.avpSet.getAvps(CreditControlAVPCode.G_S_U_POOL_REFERENCE);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.G_S_U_POOL_REFERENCE);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			GSUPoolReferenceAvp[] result=new GSUPoolReferenceAvp[rawAvps.size()];
			
			for(int index=0;index<rawAvps.size();index++)
			{
				Avp rawAvp=rawAvps.getAvpByIndex(index);
			try {
				result[index] = new GSUPoolReferenceAvpImpl(CreditControlAVPCode.G_S_U_POOL_REFERENCE,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
			} catch (Exception e) {
				super.reportAvpFetchError("index: "+index+", "+e, CreditControlAVPCode.G_S_U_POOL_REFERENCE);
				e.printStackTrace();
			}
			}
			return result;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getRatingGroup()
	 */
	public long getRatingGroup() {
		if(hasRatingGroup())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.RATING_GROUP);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.RATING_GROUP);
				e.printStackTrace();
			} 
			
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getRequestedServiceUnit()
	 */
	public RequestedServiceUnitAvp getRequestedServiceUnit() {
		if(hasRequestedServiceUnit())
		{
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			try {
				RequestedServiceUnitAvp result=new RequestedServiceUnitAvpImpl(CreditControlAVPCode.REQUESTED_SERVICE_UNIT,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
				e.printStackTrace();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getResultCode()
	 */
	public long getResultCode() {
		if(hasResultCode())
		{
			Avp rawAvp=super.avpSet.getAvp(DiameterAvpCodes.RESULT_CODE);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, DiameterAvpCodes.RESULT_CODE);
				e.printStackTrace();
			} 
			
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getServiceIdentifiers()
	 */
	public long[] getServiceIdentifiers() {
		return super.getAllAvpAsUInt32(CreditControlAVPCode.SERVICE_IDENTIFIER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getTariffChangeUsage()
	 */
	public TariffChangeUsageType getTariffChangeUsage() {
		if(hasTariffChangeUsage())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.TARIFF_CHANGE_USAGE);
			try {
				return TariffChangeUsageType.UNIT_AFTER_TARIFF_CHANGE.fromInt((int) rawAvp.getUnsigned32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.TARIFF_CHANGE_USAGE);
				e.printStackTrace();
			} 
			
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getUsedServiceUnits()
	 */
	public UsedServiceUnitAvp[] getUsedServiceUnits() {
		if(super.hasAvp(CreditControlAVPCode.USED_SERVICE_UNIT))
		{
			AvpSet set=super.avpSet.getAvps(CreditControlAVPCode.USED_SERVICE_UNIT);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			UsedServiceUnitAvp[] avps=new UsedServiceUnitAvp[set.size()];
			for(int index=0;index<set.size();index++)
			{
				try {
					Avp rawAvp=set.getAvpByIndex(index);
					UsedServiceUnitAvp avp=new UsedServiceUnitAvpImpl(CreditControlAVPCode.USED_SERVICE_UNIT,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
					avps[index]=avp;
				} catch (Exception e) {
					super.reportAvpFetchError("index: "+index+", "+e, CreditControlAVPCode.USED_SERVICE_UNIT);
					e.printStackTrace();
				}
			}
			return avps;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getValidityTime()
	 */
	public long getValidityTime() {
		if(hasValidityTime())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.VALIDITY_TIME);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.VALIDITY_TIME);
				e.printStackTrace();
			} 
			
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasFinalUnitIndication()
	 */
	public boolean hasFinalUnitIndication() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasGrantedServiceUnit()
	 */
	public boolean hasGrantedServiceUnit() {
		return super.hasAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasRatingGroup()
	 */
	public boolean hasRatingGroup() {
		return super.hasAvp(CreditControlAVPCode.RATING_GROUP);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasRequestedServiceUnit()
	 */
	public boolean hasRequestedServiceUnit() {
		return super.hasAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasResultCode()
	 */
	public boolean hasResultCode() {
		return super.hasAvp(DiameterAvpCodes.RESULT_CODE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasTariffChangeUsage()
	 */
	public boolean hasTariffChangeUsage() {
		return super.hasAvp(CreditControlAVPCode.TARIFF_CHANGE_USAGE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasValidityTime()
	 */
	public boolean hasValidityTime() {
		return super.hasAvp(CreditControlAVPCode.VALIDITY_TIME);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp)
	 */
	public void setFinalUnitIndication(
			FinalUnitIndicationAvp finalUnitIndication) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCode.FINAL_UNIT_INDICATION , finalUnitIndication.longValue(), mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGrantedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp)
	 */
	public void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit) {
		super.avpSet.removeAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)grantedServiceUnit).getRaw());
		super.avpSet.addAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT,grantedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGsuPoolReference(net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp)
	 */
	public void setGsuPoolReference(GSUPoolReferenceAvp gsuPoolReference) {
		this.setGsuPoolReferences(new GSUPoolReferenceAvp[]{gsuPoolReference});
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGsuPoolReferences(net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp[])
	 */
	public void setGsuPoolReferences(GSUPoolReferenceAvp[] gsuPoolReferences) {

		super.avpSet.removeAvp(CreditControlAVPCode.G_S_U_POOL_REFERENCE);
		
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.G_S_U_POOL_REFERENCE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		for(GSUPoolReferenceAvpImpl gsuPoolReference: (GSUPoolReferenceAvpImpl[]) gsuPoolReferences )
			//super.avpSet.addAvp(gsuPoolReference.getRaw());
		{
	
			super.avpSet.addAvp(CreditControlAVPCode.G_S_U_POOL_REFERENCE,gsuPoolReference.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setRatingGroup(long)
	 */
	public void setRatingGroup(long ratingGroup) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.RATING_GROUP);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCode.RATING_GROUP , ratingGroup, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setRequestedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp)
	 */
	public void setRequestedServiceUnit(
			RequestedServiceUnitAvp requestedServiceUnit) {
		super.avpSet.removeAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)requestedServiceUnit).getRaw());		
		super.avpSet.addAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT,requestedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setResultCode(long)
	 */
	public void setResultCode(long resultCode) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.RESULT_CODE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(DiameterAvpCodes.RESULT_CODE , resultCode, mandatoryAvp==1, protectedAvp==1, true);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setServiceIdentifier(long)
	 */
	public void setServiceIdentifier(long serviceIdentifier) {
		this.setServiceIdentifiers(new long[]{serviceIdentifier});

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setServiceIdentifiers(long[])
	 */
	public void setServiceIdentifiers(long[] serviceIdentifiers) {
		
		super.avpSet.removeAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		for(long serviceIdentifier:serviceIdentifiers)
				super.setAvpAsUInt32(CreditControlAVPCode.SERVICE_IDENTIFIER , serviceIdentifier, mandatoryAvp==1, protectedAvp==1, false);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setTariffChangeUsage(net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType)
	 */
	public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.TARIFF_CHANGE_USAGE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCode.TARIFF_CHANGE_USAGE , tariffChangeUsage.getValue(), mandatoryAvp==1, protectedAvp==1, true);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setUsedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp)
	 */
	public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit) {
		this.setUsedServiceUnits(new UsedServiceUnitAvp[]{usedServiceUnit});

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setUsedServiceUnits(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp[])
	 */
	public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits) {
		super.avpSet.removeAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		for(UsedServiceUnitAvp usedServiceUnit: usedServiceUnits)
		{
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.USED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.USED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
			//inserted.addAvp(((GroupedAvpImpl)usedServiceUnit).getRaw());
			super.avpSet.addAvp(CreditControlAVPCode.USED_SERVICE_UNIT,usedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setValidityTime(long)
	 */
	public void setValidityTime(long validityTime) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.VALIDITY_TIME);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCode.VALIDITY_TIME , validityTime, mandatoryAvp==1, protectedAvp==1, true);
	}

}
