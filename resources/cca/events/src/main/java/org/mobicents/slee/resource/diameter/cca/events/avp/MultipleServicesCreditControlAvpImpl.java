package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Final_Unit_Indication);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Final_Unit_Indication);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			FinalUnitIndicationAvp result=null;
			try {
				result = new FinalUnitIndicationAvpImpl(CreditControlAVPCodes.Final_Unit_Indication,Long.valueOf(avpRep.getVendorId()),mandatoryAvp,protectedAvp,rawAvp.getRaw());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Final_Unit_Indication);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Granted_Service_Unit);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.GRANTED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			GrantedServiceUnitAvp result=null;
			try {
				result = new GrantedServiceUnitAvpImpl(CreditControlAVPCodes.Granted_Service_Unit,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Granted_Service_Unit);
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
		if(super.hasAvp(CreditControlAVPCodes.G_S_U_Pool_Reference))
		{
			AvpSet rawAvps=super.avpSet.getAvps(CreditControlAVPCodes.G_S_U_Pool_Reference);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.G_S_U_POOL_REFERENCE);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			GSUPoolReferenceAvp[] result=new GSUPoolReferenceAvp[rawAvps.size()];
			
			for(int index=0;index<rawAvps.size();index++)
			{
				Avp rawAvp=rawAvps.getAvpByIndex(index);
			try {
				result[index] = new GSUPoolReferenceAvpImpl(CreditControlAVPCodes.G_S_U_Pool_Reference,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
			} catch (Exception e) {
				super.reportAvpFetchError("index: "+index+", "+e, CreditControlAVPCodes.G_S_U_Pool_Reference);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Rating_Group);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Rating_Group);
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
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.REQUESTED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
			try {
				RequestedServiceUnitAvp result=new RequestedServiceUnitAvpImpl(CreditControlAVPCodes.Requested_Service_Unit,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Requested_Service_Unit);
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
		return super.getAllAvpAsUInt32(CreditControlAVPCodes.Service_Identifier);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getTariffChangeUsage()
	 */
	public TariffChangeUsageType getTariffChangeUsage() {
		if(hasTariffChangeUsage())
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Tariff_Change_Usage);
			try {
				return TariffChangeUsageType.UNIT_AFTER_TARIFF_CHANGE.fromInt((int) rawAvp.getUnsigned32());
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Tariff_Change_Usage);
				e.printStackTrace();
			} 
			
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getUsedServiceUnits()
	 */
	public UsedServiceUnitAvp[] getUsedServiceUnits() {
		if(super.hasAvp(CreditControlAVPCodes.Used_Service_Unit))
		{
			AvpSet set=super.avpSet.getAvps(CreditControlAVPCodes.Used_Service_Unit);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.USED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			UsedServiceUnitAvp[] avps=new UsedServiceUnitAvp[set.size()];
			for(int index=0;index<set.size();index++)
			{
				try {
					Avp rawAvp=set.getAvpByIndex(index);
					UsedServiceUnitAvp avp=new UsedServiceUnitAvpImpl(CreditControlAVPCodes.Used_Service_Unit,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
					avps[index]=avp;
				} catch (Exception e) {
					super.reportAvpFetchError("index: "+index+", "+e, CreditControlAVPCodes.Used_Service_Unit);
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
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Validity_Time);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Validity_Time);
				e.printStackTrace();
			} 
			
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasFinalUnitIndication()
	 */
	public boolean hasFinalUnitIndication() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasGrantedServiceUnit()
	 */
	public boolean hasGrantedServiceUnit() {
		return super.hasAvp(CreditControlAVPCodes.Granted_Service_Unit);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasRatingGroup()
	 */
	public boolean hasRatingGroup() {
		return super.hasAvp(CreditControlAVPCodes.Rating_Group);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasRequestedServiceUnit()
	 */
	public boolean hasRequestedServiceUnit() {
		return super.hasAvp(CreditControlAVPCodes.Requested_Service_Unit);
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
		return super.hasAvp(CreditControlAVPCodes.Tariff_Change_Usage);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasValidityTime()
	 */
	public boolean hasValidityTime() {
		return super.hasAvp(CreditControlAVPCodes.Validity_Time);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp)
	 */
	public void setFinalUnitIndication(
			FinalUnitIndicationAvp finalUnitIndication) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Final_Unit_Indication);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCodes.Final_Unit_Indication , finalUnitIndication.longValue(), true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGrantedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp)
	 */
	public void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Granted_Service_Unit);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Granted_Service_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.GRANTED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.GRANTED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)grantedServiceUnit).getRaw());
		super.avpSet.addAvp(CreditControlAVPCodes.Granted_Service_Unit,grantedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

		
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

		super.avpSet.removeAvp(CreditControlAVPCodes.G_S_U_Pool_Reference);
		
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.G_S_U_Pool_Reference);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		for(GSUPoolReferenceAvpImpl gsuPoolReference: (GSUPoolReferenceAvpImpl[]) gsuPoolReferences )
			//super.avpSet.addAvp(gsuPoolReference.getRaw());
		{
	
			super.avpSet.addAvp(CreditControlAVPCodes.G_S_U_Pool_Reference,gsuPoolReference.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setRatingGroup(long)
	 */
	public void setRatingGroup(long ratingGroup) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Rating_Group);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCodes.Rating_Group , ratingGroup, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setRequestedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp)
	 */
	public void setRequestedServiceUnit(
			RequestedServiceUnitAvp requestedServiceUnit) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Requested_Service_Unit);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.REQUESTED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.REQUESTED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)requestedServiceUnit).getRaw());		
		super.avpSet.addAvp(CreditControlAVPCodes.Requested_Service_Unit,requestedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setResultCode(long)
	 */
	public void setResultCode(long resultCode) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.RESULT_CODE);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(DiameterAvpCodes.RESULT_CODE , resultCode,true);
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
		
		super.avpSet.removeAvp(CreditControlAVPCodes.Service_Identifier);
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Identifier);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		for(long serviceIdentifier:serviceIdentifiers)
				super.setAvpAsUInt32(CreditControlAVPCodes.Service_Identifier , serviceIdentifier, false);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setTariffChangeUsage(net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType)
	 */
	public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Tariff_Change_Usage);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCodes.Tariff_Change_Usage , tariffChangeUsage.getValue(),  true);
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
		super.avpSet.removeAvp(CreditControlAVPCodes.Used_Service_Unit);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Used_Service_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		for(UsedServiceUnitAvp usedServiceUnit: usedServiceUnits)
		{
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.USED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.USED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
			//inserted.addAvp(((GroupedAvpImpl)usedServiceUnit).getRaw());
			super.avpSet.addAvp(CreditControlAVPCodes.Used_Service_Unit,usedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setValidityTime(long)
	 */
	public void setValidityTime(long validityTime) {
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Validity_Time);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.setAvpAsUInt32(CreditControlAVPCodes.Validity_Time , validityTime,true);
	}

}
