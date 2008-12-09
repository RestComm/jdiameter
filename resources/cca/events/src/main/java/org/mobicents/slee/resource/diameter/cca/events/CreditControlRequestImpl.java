package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesIndicatorType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedActionType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.SubscriptionIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvpImpl;
/**
 * CCA CCR message impl <br>
 * <br>
 * Super project: mobicents <br>
 * 12:25:46 2008-11-10 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlRequestImpl extends CreditControlMessageImpl implements CreditControlRequest{

	public CreditControlRequestImpl(Message message) {
		super(message);
	
	}

	@Override
	public String getLongName() {
		return "Credit-Control-Request";
	}

	@Override
	public String getShortName() {
		return "CCR";
	}

	public byte[] getCcCorrelationId() {
		
		Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.CC_CORRELATION_ID);
		if(rawAvp==null)
		{
		return null;
		}else
		{
			try {
				return rawAvp.getRaw();
			} catch (AvpDataException e) {
				log.error("Failed to get avp, code: "+CreditControlAVPCode.CC_CORRELATION_ID);
				e.printStackTrace();
				return null;
			}
		}
	}

	public MultipleServicesIndicatorType getMultipleServicesIndicator() {
		if(hasMultipleServicesIndicator())
		{
			try {
				return MultipleServicesIndicatorType.MULTIPLE_SERVICES_NOT_SUPPORTED.fromInt(super.message.getAvps().getAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public RequestedActionType getRequestedAction() {
		if(hasRequestedAction())
		{
			try {
				return RequestedActionType.CHECK_BALANCE.fromInt(super.message.getAvps().getAvp(CreditControlAVPCode.REQUESTED_ACTION).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public RequestedServiceUnitAvp getRequestedServiceUnit() {
		if(hasRequestedServiceUnit())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				RequestedServiceUnitAvp result=new RequestedServiceUnitAvpImpl(CreditControlAVPCode.REQUESTED_SERVICE_UNIT,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getServiceContextId() {
		if(hasServiceContextId())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
			
			try {
				
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public long getServiceIdentifier() {
		if(hasServiceIdentifier())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
			
			try {
				
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
			
				e.printStackTrace();
			}
		}
		return -1;
	}

	public ServiceParameterInfoAvp[] getServiceParameterInfos() {
		if(super.hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			ServiceParameterInfoAvp[] avps=new ServiceParameterInfoAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						ServiceParameterInfoAvp avp=new ServiceParameterInfoAvpImpl(CreditControlAVPCode.SERVICE_PARAMETER_INFO,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
						avps[index]=avp;
					} catch (AvpDataException e) {
				
						e.printStackTrace();
					}
				return avps;
			}
		}
		return null;
	}

	public SubscriptionIdAvp[] getSubscriptionIds() {
		if(super.hasAvp(CreditControlAVPCode.SUBSCRIPTION_ID))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCode.SUBSCRIPTION_ID);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			SubscriptionIdAvp[] avps=new SubscriptionIdAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						SubscriptionIdAvp avp=new SubscriptionIdAvpImpl(CreditControlAVPCode.SERVICE_PARAMETER_INFO,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
						avps[index]=avp;
					} catch (AvpDataException e) {
				
						e.printStackTrace();
					}
				return avps;
			}
		}
		return null;
	}

	public TerminationCauseType getTerminationCause() {
		if(hasTerminationCause())
		{
			try {
				return TerminationCauseType.fromInt(super.message.getAvps().getAvp(DiameterAvpCodes.TERMINATION_CAUSE).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public UsedServiceUnitAvp[] getUsedServiceUnits() {
		if(super.hasAvp(CreditControlAVPCode.USED_SERVICE_UNIT))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCode.USED_SERVICE_UNIT);
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
					} catch (AvpDataException e) {
				
						e.printStackTrace();
					}
				return avps;
			}
		}
		return null;
	}

	public UserEquipmentInfoAvp getUserEquipmentInfo() {
		if(hasUserEquipmentInfo())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				UserEquipmentInfoAvp result=new UserEquipmentInfoAvpImpl(CreditControlAVPCode.USER_EQUIPMENT_INFO,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasCcCorrelationId() {
		return super.hasAvp(CreditControlAVPCode.CC_CORRELATION_ID);
	}

	public boolean hasMultipleServicesIndicator() {
		return super.hasAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR);
	}

	public boolean hasRequestedAction() {
		return super.hasAvp(CreditControlAVPCode.REQUESTED_ACTION);
	}

	public boolean hasRequestedServiceUnit() {
		return super.hasAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
	}

	public boolean hasServiceContextId() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
	}

	public boolean hasServiceIdentifier() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
	}

	public boolean hasTerminationCause() {
		return super.hasAvp(DiameterAvpCodes.TERMINATION_CAUSE);
	}

	public boolean hasUserEquipmentInfo() {
		return super.hasAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
	}

	public void setCcCorrelationId(byte[] ccCorrelationId)
			throws IllegalStateException {
		//if(hasCcCorrelationId())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			AvpSet avpSet=super.message.getAvps();
			avpSet.removeAvp(CreditControlAVPCode.CC_CORRELATION_ID);
		
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_CORRELATION_ID);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.CC_CORRELATION_ID, ccCorrelationId, mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.CC_CORRELATION_ID, ccCorrelationId,Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
	}

	public void setMultipleServicesIndicator(
			MultipleServicesIndicatorType multipleServicesIndicator)
			throws IllegalStateException {
		//if(hasMultipleServicesIndicator())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR, multipleServicesIndicator.getValue(), mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR, multipleServicesIndicator.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setRequestedAction(RequestedActionType requestedAction)
			throws IllegalStateException {
		//if(hasRequestedAction())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_ACTION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.REQUESTED_ACTION);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.REQUESTED_ACTION, requestedAction.getValue(), mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.REQUESTED_ACTION, requestedAction.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setRequestedServiceUnit(
			RequestedServiceUnitAvp requestedServiceUnit)
			throws IllegalStateException {
		//if(hasRequestedServiceUnit())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			//baranowb: is this correct ?
			//super.addAvp(requestedServiceUnit);
			super.message.getAvps().removeAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
			//inserted.addAvp(((GroupedAvpImpl)requestedServiceUnit).getRaw());
			super.message.getAvps().addAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT,requestedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setServiceContextId(String serviceContextId)
			throws IllegalStateException {
		//if(hasServiceContextId())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
	{
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.message.getAvps().removeAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
		//if(avpRep.getVendorId()==null)
			super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID, serviceContextId,false, mandatoryAvp==1, protectedAvp==1);
		//else
		//	super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID, serviceContextId,Long.getLong(avpRep.getVendorId()).longValue(),false, mandatoryAvp==1, protectedAvp==1);
	}
		
	}

	public void setServiceIdentifier(long serviceIdentifier)
			throws IllegalStateException {
		//if(hasServiceIdentifier())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_IDENTIFIER, serviceIdentifier, mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_IDENTIFIER, serviceIdentifier,Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
	}

	public void setServiceParameterInfo(
			ServiceParameterInfoAvp serviceParameterInfo)
			throws IllegalStateException {
		this.setServiceParameterInfos(new ServiceParameterInfoAvp[]{serviceParameterInfo});
	}

	public void setServiceParameterInfos(
			ServiceParameterInfoAvp[] serviceParameterInfos)
			throws IllegalStateException {


		//if(hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO))
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(ServiceParameterInfoAvp serviceParameterInfo: serviceParameterInfos)
			{
				//AvpSet inserted=null;
				//if(avpRep.getVendorId()!=null)
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
				//else
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO, mandatoryAvp==1, protectedAvp==1);
		
				//inserted.addAvp(((GroupedAvpImpl)serviceParameterInfo).getRaw());
				super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO,serviceParameterInfo.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
			}
		}
		
		
	}

	public void setSubscriptionId(SubscriptionIdAvp subscriptionId)
			throws IllegalStateException {
		this.setSubscriptionIds(new SubscriptionIdAvp[]{subscriptionId});
		
	}

	public void setSubscriptionIds(SubscriptionIdAvp[] subscriptionIds)
			throws IllegalStateException {
		//if(hasAvp(CreditControlAVPCode.SUBSCRIPTION_ID))
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.SUBSCRIPTION_ID);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(SubscriptionIdAvp subscriptionId: subscriptionIds)
			{
				//AvpSet inserted=null;
				//if(avpRep.getVendorId()!=null)
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SUBSCRIPTION_ID, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
				//else
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SUBSCRIPTION_ID, mandatoryAvp==1, protectedAvp==1);
		
				//inserted.addAvp(((GroupedAvpImpl)subscriptionId).getRaw());
				super.message.getAvps().addAvp(CreditControlAVPCode.SUBSCRIPTION_ID,subscriptionId.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
			}
		}
		
	}

	public void setTerminationCause(TerminationCauseType terminationCause)
			throws IllegalStateException {
		//if(hasTerminationCause())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.TERMINATION_CAUSE);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(DiameterAvpCodes.TERMINATION_CAUSE);
			if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(DiameterAvpCodes.TERMINATION_CAUSE, terminationCause.getValue(), mandatoryAvp==1, protectedAvp==1);
			else
				super.message.getAvps().addAvp(DiameterAvpCodes.TERMINATION_CAUSE, terminationCause.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
	}

	public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit)
			throws IllegalStateException {
		this.setUsedServiceUnits(new UsedServiceUnitAvp[]{usedServiceUnit});
		
	}

	public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits)
			throws IllegalStateException {
		
		
		//if(hasAvp(CreditControlAVPCode.USED_SERVICE_UNIT))
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
				super.message.getAvps().removeAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
				AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
				int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
				int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
				for(UsedServiceUnitAvp usedServiceUnit: usedServiceUnits)
				{
					//AvpSet inserted=null;
					//if(avpRep.getVendorId()!=null)
					//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
					//else
					//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
			
					//inserted.addAvp(((GroupedAvpImpl)usedServiceUnit).getRaw());
					super.message.getAvps().addAvp(CreditControlAVPCode.USED_SERVICE_UNIT,usedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
				}
			}
		
	}

	public void setUserEquipmentInfo(UserEquipmentInfoAvp userEquipmentInfo)
			throws IllegalStateException {
		//if(hasUserEquipmentInfo())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO, mandatoryAvp==1, protectedAvp==1);
	
			//inserted.addAvp(((GroupedAvpImpl)userEquipmentInfo).getRaw());
			super.message.getAvps().addAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO,userEquipmentInfo.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}
	}

	

}
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesIndicatorType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedActionType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.SubscriptionIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvpImpl;
/**
 * CCA CCR message impl <br>
 * <br>
 * Super project: mobicents <br>
 * 12:25:46 2008-11-10 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlRequestImpl extends CreditControlMessageImpl implements CreditControlRequest{

	public CreditControlRequestImpl(Message message) {
		super(message);
	
	}

	@Override
	public String getLongName() {
		return "Credit-Control-Request";
	}

	@Override
	public String getShortName() {
		return "CCR";
	}

	public byte[] getCcCorrelationId() {
		
		Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.CC_CORRELATION_ID);
		if(rawAvp==null)
		{
		return null;
		}else
		{
			try {
				return rawAvp.getRaw();
			} catch (AvpDataException e) {
				log.error("Failed to get avp, code: "+CreditControlAVPCode.CC_CORRELATION_ID);
				e.printStackTrace();
				return null;
			}
		}
	}

	public MultipleServicesIndicatorType getMultipleServicesIndicator() {
		if(hasMultipleServicesIndicator())
		{
			try {
				return MultipleServicesIndicatorType.MULTIPLE_SERVICES_NOT_SUPPORTED.fromInt(super.message.getAvps().getAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public RequestedActionType getRequestedAction() {
		if(hasRequestedAction())
		{
			try {
				return RequestedActionType.CHECK_BALANCE.fromInt(super.message.getAvps().getAvp(CreditControlAVPCode.REQUESTED_ACTION).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public RequestedServiceUnitAvp getRequestedServiceUnit() {
		if(hasRequestedServiceUnit())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				RequestedServiceUnitAvp result=new RequestedServiceUnitAvpImpl(CreditControlAVPCode.REQUESTED_SERVICE_UNIT,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getServiceContextId() {
		if(hasServiceContextId())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
			
			try {
				
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public long getServiceIdentifier() {
		if(hasServiceIdentifier())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
			
			try {
				
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
			
				e.printStackTrace();
			}
		}
		return -1;
	}

	public ServiceParameterInfoAvp[] getServiceParameterInfos() {
		if(super.hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			ServiceParameterInfoAvp[] avps=new ServiceParameterInfoAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						ServiceParameterInfoAvp avp=new ServiceParameterInfoAvpImpl(CreditControlAVPCode.SERVICE_PARAMETER_INFO,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
						avps[index]=avp;
					} catch (AvpDataException e) {
				
						e.printStackTrace();
					}
				return avps;
			}
		}
		return null;
	}

	public SubscriptionIdAvp[] getSubscriptionIds() {
		if(super.hasAvp(CreditControlAVPCode.SUBSCRIPTION_ID))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCode.SUBSCRIPTION_ID);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			SubscriptionIdAvp[] avps=new SubscriptionIdAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						SubscriptionIdAvp avp=new SubscriptionIdAvpImpl(CreditControlAVPCode.SERVICE_PARAMETER_INFO,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
						avps[index]=avp;
					} catch (AvpDataException e) {
				
						e.printStackTrace();
					}
				return avps;
			}
		}
		return null;
	}

	public TerminationCauseType getTerminationCause() {
		if(hasTerminationCause())
		{
			try {
				return TerminationCauseType.fromInt(super.message.getAvps().getAvp(DiameterAvpCodes.TERMINATION_CAUSE).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public UsedServiceUnitAvp[] getUsedServiceUnits() {
		if(super.hasAvp(CreditControlAVPCode.USED_SERVICE_UNIT))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCode.USED_SERVICE_UNIT);
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
					} catch (AvpDataException e) {
				
						e.printStackTrace();
					}
				return avps;
			}
		}
		return null;
	}

	public UserEquipmentInfoAvp getUserEquipmentInfo() {
		if(hasUserEquipmentInfo())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				UserEquipmentInfoAvp result=new UserEquipmentInfoAvpImpl(CreditControlAVPCode.USER_EQUIPMENT_INFO,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasCcCorrelationId() {
		return super.hasAvp(CreditControlAVPCode.CC_CORRELATION_ID);
	}

	public boolean hasMultipleServicesIndicator() {
		return super.hasAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR);
	}

	public boolean hasRequestedAction() {
		return super.hasAvp(CreditControlAVPCode.REQUESTED_ACTION);
	}

	public boolean hasRequestedServiceUnit() {
		return super.hasAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
	}

	public boolean hasServiceContextId() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
	}

	public boolean hasServiceIdentifier() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
	}

	public boolean hasTerminationCause() {
		return super.hasAvp(DiameterAvpCodes.TERMINATION_CAUSE);
	}

	public boolean hasUserEquipmentInfo() {
		return super.hasAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
	}

	public void setCcCorrelationId(byte[] ccCorrelationId)
			throws IllegalStateException {
		//if(hasCcCorrelationId())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			AvpSet avpSet=super.message.getAvps();
			avpSet.removeAvp(CreditControlAVPCode.CC_CORRELATION_ID);
		
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_CORRELATION_ID);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.CC_CORRELATION_ID, ccCorrelationId, mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.CC_CORRELATION_ID, ccCorrelationId,Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
	}

	public void setMultipleServicesIndicator(
			MultipleServicesIndicatorType multipleServicesIndicator)
			throws IllegalStateException {
		//if(hasMultipleServicesIndicator())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR, multipleServicesIndicator.getValue(), mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.MULTIPLE_SERVICES_INDICATOR, multipleServicesIndicator.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setRequestedAction(RequestedActionType requestedAction)
			throws IllegalStateException {
		//if(hasRequestedAction())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_ACTION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.REQUESTED_ACTION);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.REQUESTED_ACTION, requestedAction.getValue(), mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.REQUESTED_ACTION, requestedAction.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setRequestedServiceUnit(
			RequestedServiceUnitAvp requestedServiceUnit)
			throws IllegalStateException {
		//if(hasRequestedServiceUnit())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			//baranowb: is this correct ?
			//super.addAvp(requestedServiceUnit);
			super.message.getAvps().removeAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
			//inserted.addAvp(((GroupedAvpImpl)requestedServiceUnit).getRaw());
			super.message.getAvps().addAvp(CreditControlAVPCode.REQUESTED_SERVICE_UNIT,requestedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setServiceContextId(String serviceContextId)
			throws IllegalStateException {
		//if(hasServiceContextId())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
	{
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.message.getAvps().removeAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID);
		//if(avpRep.getVendorId()==null)
			super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID, serviceContextId,false, mandatoryAvp==1, protectedAvp==1);
		//else
		//	super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_CONTEXT_ID, serviceContextId,Long.getLong(avpRep.getVendorId()).longValue(),false, mandatoryAvp==1, protectedAvp==1);
	}
		
	}

	public void setServiceIdentifier(long serviceIdentifier)
			throws IllegalStateException {
		//if(hasServiceIdentifier())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.SERVICE_IDENTIFIER);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_IDENTIFIER, serviceIdentifier, mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_IDENTIFIER, serviceIdentifier,Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
	}

	public void setServiceParameterInfo(
			ServiceParameterInfoAvp serviceParameterInfo)
			throws IllegalStateException {
		this.setServiceParameterInfos(new ServiceParameterInfoAvp[]{serviceParameterInfo});
	}

	public void setServiceParameterInfos(
			ServiceParameterInfoAvp[] serviceParameterInfos)
			throws IllegalStateException {


		//if(hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO))
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(ServiceParameterInfoAvp serviceParameterInfo: serviceParameterInfos)
			{
				//AvpSet inserted=null;
				//if(avpRep.getVendorId()!=null)
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
				//else
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO, mandatoryAvp==1, protectedAvp==1);
		
				//inserted.addAvp(((GroupedAvpImpl)serviceParameterInfo).getRaw());
				super.message.getAvps().addAvp(CreditControlAVPCode.SERVICE_PARAMETER_INFO,serviceParameterInfo.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
			}
		}
		
		
	}

	public void setSubscriptionId(SubscriptionIdAvp subscriptionId)
			throws IllegalStateException {
		this.setSubscriptionIds(new SubscriptionIdAvp[]{subscriptionId});
		
	}

	public void setSubscriptionIds(SubscriptionIdAvp[] subscriptionIds)
			throws IllegalStateException {
		//if(hasAvp(CreditControlAVPCode.SUBSCRIPTION_ID))
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.SUBSCRIPTION_ID);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SUBSCRIPTION_ID);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(SubscriptionIdAvp subscriptionId: subscriptionIds)
			{
				//AvpSet inserted=null;
				//if(avpRep.getVendorId()!=null)
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SUBSCRIPTION_ID, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
				//else
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.SUBSCRIPTION_ID, mandatoryAvp==1, protectedAvp==1);
		
				//inserted.addAvp(((GroupedAvpImpl)subscriptionId).getRaw());
				super.message.getAvps().addAvp(CreditControlAVPCode.SUBSCRIPTION_ID,subscriptionId.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
			}
		}
		
	}

	public void setTerminationCause(TerminationCauseType terminationCause)
			throws IllegalStateException {
		//if(hasTerminationCause())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.TERMINATION_CAUSE);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(DiameterAvpCodes.TERMINATION_CAUSE);
			
			super.message.getAvps().addAvp(DiameterAvpCodes.TERMINATION_CAUSE, terminationCause.getValue(),avpRep.getVendorId(), mandatoryAvp==1, protectedAvp==1);
		}
	}

	public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit)
			throws IllegalStateException {
		this.setUsedServiceUnits(new UsedServiceUnitAvp[]{usedServiceUnit});
		
	}

	public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits)
			throws IllegalStateException {
		
		
		//if(hasAvp(CreditControlAVPCode.USED_SERVICE_UNIT))
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
				super.message.getAvps().removeAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
				AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USED_SERVICE_UNIT);
				int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
				int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
				for(UsedServiceUnitAvp usedServiceUnit: usedServiceUnits)
				{
					//AvpSet inserted=null;
					//if(avpRep.getVendorId()!=null)
					//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USED_SERVICE_UNIT, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
					//else
					//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USED_SERVICE_UNIT, mandatoryAvp==1, protectedAvp==1);
			
					//inserted.addAvp(((GroupedAvpImpl)usedServiceUnit).getRaw());
					super.message.getAvps().addAvp(CreditControlAVPCode.USED_SERVICE_UNIT,usedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
				}
			}
		
	}

	public void setUserEquipmentInfo(UserEquipmentInfoAvp userEquipmentInfo)
			throws IllegalStateException {
		//if(hasUserEquipmentInfo())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO);
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO, mandatoryAvp==1, protectedAvp==1);
	
			//inserted.addAvp(((GroupedAvpImpl)userEquipmentInfo).getRaw());
			super.message.getAvps().addAvp(CreditControlAVPCode.USER_EQUIPMENT_INFO,userEquipmentInfo.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}
	}

	

}
