package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
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
		
		Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCodes.CC_Correlation_Id);
		if(rawAvp==null)
		{
		return null;
		}else
		{
			try {
				return rawAvp.getRaw();
			} catch (AvpDataException e) {
				log.error("Failed to get avp, code: "+CreditControlAVPCodes.CC_Correlation_Id);
				e.printStackTrace();
				return null;
			}
		}
	}

	public MultipleServicesIndicatorType getMultipleServicesIndicator() {
		if(hasMultipleServicesIndicator())
		{
			try {
				return MultipleServicesIndicatorType.MULTIPLE_SERVICES_NOT_SUPPORTED.fromInt(super.message.getAvps().getAvp(CreditControlAVPCodes.Multiple_Services_Indicator).getInteger32());
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
				return RequestedActionType.CHECK_BALANCE.fromInt(super.message.getAvps().getAvp(CreditControlAVPCodes.Requested_Action).getInteger32());
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
		}
		return null;
	}

	public RequestedServiceUnitAvp getRequestedServiceUnit() {
		if(hasRequestedServiceUnit())
		{
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCodes.Requested_Service_Unit);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				RequestedServiceUnitAvp result=new RequestedServiceUnitAvpImpl(CreditControlAVPCodes.Requested_Service_Unit,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
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
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCodes.Service_Context_Id);
			
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
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCodes.Service_Identifier);
			
			try {
				
				return rawAvp.getUnsigned32();
			} catch (AvpDataException e) {
			
				e.printStackTrace();
			}
		}
		return -1;
	}

	public ServiceParameterInfoAvp[] getServiceParameterInfos() {
		if(super.hasAvp(CreditControlAVPCodes.Service_Parameter_Info))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCodes.Service_Parameter_Info);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Parameter_Info);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			ServiceParameterInfoAvp[] avps=new ServiceParameterInfoAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						ServiceParameterInfoAvp avp=new ServiceParameterInfoAvpImpl(CreditControlAVPCodes.Service_Parameter_Info,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
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
		if(super.hasAvp(CreditControlAVPCodes.Subscription_Id))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCodes.Subscription_Id);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Subscription_Id);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			SubscriptionIdAvp[] avps=new SubscriptionIdAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						SubscriptionIdAvp avp=new SubscriptionIdAvpImpl(CreditControlAVPCodes.Subscription_Id,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
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
		if(super.hasAvp(CreditControlAVPCodes.Used_Service_Unit))
		{
			AvpSet set=super.message.getAvps().getAvps(CreditControlAVPCodes.Used_Service_Unit);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Used_Service_Unit);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			UsedServiceUnitAvp[] avps=new UsedServiceUnitAvp[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				try {
						Avp rawAvp=set.getAvpByIndex(index);
						UsedServiceUnitAvp avp=new UsedServiceUnitAvpImpl(CreditControlAVPCodes.Used_Service_Unit,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
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
			Avp rawAvp=super.message.getAvps().getAvp(CreditControlAVPCodes.User_Equipment_Info);
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.User_Equipment_Info);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			try {
				UserEquipmentInfoAvp result=new UserEquipmentInfoAvpImpl(CreditControlAVPCodes.User_Equipment_Info,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasCcCorrelationId() {
		return super.hasAvp(CreditControlAVPCodes.CC_Correlation_Id);
	}

	public boolean hasMultipleServicesIndicator() {
		return super.hasAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
	}

	public boolean hasRequestedAction() {
		return super.hasAvp(CreditControlAVPCodes.Requested_Action);
	}

	public boolean hasRequestedServiceUnit() {
		return super.hasAvp(CreditControlAVPCodes.Requested_Service_Unit);
	}

	public boolean hasServiceContextId() {
		return super.hasAvp(CreditControlAVPCodes.Service_Context_Id);
	}

	public boolean hasServiceIdentifier() {
		return super.hasAvp(CreditControlAVPCodes.Service_Identifier);
	}

	public boolean hasTerminationCause() {
		return super.hasAvp(DiameterAvpCodes.TERMINATION_CAUSE);
	}

	public boolean hasUserEquipmentInfo() {
		return super.hasAvp(CreditControlAVPCodes.User_Equipment_Info);
	}

	public void setCcCorrelationId(byte[] ccCorrelationId)
			throws IllegalStateException {
		//if(hasCcCorrelationId())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			AvpSet avpSet=super.message.getAvps();
			avpSet.removeAvp(CreditControlAVPCodes.CC_Correlation_Id);
		
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Correlation_Id);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.CC_Correlation_Id);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Correlation_Id, ccCorrelationId, mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Correlation_Id, ccCorrelationId,Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCodes.Multiple_Services_Indicator, multipleServicesIndicator.getValue(), mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCodes.Multiple_Services_Indicator, multipleServicesIndicator.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setRequestedAction(RequestedActionType requestedAction)
			throws IllegalStateException {
		//if(hasRequestedAction())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Action);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Requested_Action);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCodes.Requested_Action, requestedAction.getValue(), mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCodes.Requested_Action, requestedAction.getValue(),Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
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
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Requested_Service_Unit);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Requested_Service_Unit, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Requested_Service_Unit, mandatoryAvp==1, protectedAvp==1);
			//inserted.addAvp(((GroupedAvpImpl)requestedServiceUnit).getRaw());
			super.message.getAvps().addAvp(CreditControlAVPCodes.Requested_Service_Unit,requestedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}
		
	}

	public void setServiceContextId(String serviceContextId)
			throws IllegalStateException {
		//if(hasServiceContextId())
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
	{
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Context_Id);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.message.getAvps().removeAvp(CreditControlAVPCodes.Service_Context_Id);
		//if(avpRep.getVendorId()==null)
			super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Context_Id, serviceContextId,false, mandatoryAvp==1, protectedAvp==1);
		//else
		//	super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Context_Id, serviceContextId,Long.getLong(avpRep.getVendorId()).longValue(),false, mandatoryAvp==1, protectedAvp==1);
	}
		
	}

	public void setServiceIdentifier(long serviceIdentifier)
			throws IllegalStateException {
		//if(hasServiceIdentifier())
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Identifier);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Service_Identifier);
			//if(avpRep.getVendorId()==null)
				super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Identifier, serviceIdentifier, mandatoryAvp==1, protectedAvp==1);
			//else
			//	super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Identifier, serviceIdentifier,Long.getLong(avpRep.getVendorId()).longValue(), mandatoryAvp==1, protectedAvp==1);
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


		//if(hasAvp(CreditControlAVPCodes.Service_Parameter_Info))
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Service_Parameter_Info);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Parameter_Info);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(ServiceParameterInfoAvp serviceParameterInfo: serviceParameterInfos)
			{
				//AvpSet inserted=null;
				//if(avpRep.getVendorId()!=null)
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Service_Parameter_Info, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
				//else
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Service_Parameter_Info, mandatoryAvp==1, protectedAvp==1);
		
				//inserted.addAvp(((GroupedAvpImpl)serviceParameterInfo).getRaw());
				super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Parameter_Info,serviceParameterInfo.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
			}
		}
		
		
	}

	public void setSubscriptionId(SubscriptionIdAvp subscriptionId)
			throws IllegalStateException {
		this.setSubscriptionIds(new SubscriptionIdAvp[]{subscriptionId});
		
	}

	public void setSubscriptionIds(SubscriptionIdAvp[] subscriptionIds)
			throws IllegalStateException {
		//if(hasAvp(CreditControlAVPCodes.Subscription_Id))
		//{
		//	throw new IllegalStateException("It's neem already set!?");
		//}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Subscription_Id);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Subscription_Id);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(SubscriptionIdAvp subscriptionId: subscriptionIds)
			{
				//AvpSet inserted=null;
				//if(avpRep.getVendorId()!=null)
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Subscription_Id, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
				//else
				//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Subscription_Id, mandatoryAvp==1, protectedAvp==1);
		
				//inserted.addAvp(((GroupedAvpImpl)subscriptionId).getRaw());
				super.message.getAvps().addAvp(CreditControlAVPCodes.Subscription_Id,subscriptionId.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
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
		
		
		//if(hasAvp(CreditControlAVPCodes.Used_Service_Unit))
			//{
			//	throw new IllegalStateException("It's neem already set!?");
			//}else
		{
				super.message.getAvps().removeAvp(CreditControlAVPCodes.Used_Service_Unit);
				AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Used_Service_Unit);
				int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
				int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
				for(UsedServiceUnitAvp usedServiceUnit: usedServiceUnits)
				{
					//AvpSet inserted=null;
					//if(avpRep.getVendorId()!=null)
					//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Used_Service_Unit, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
					//else
					//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.Used_Service_Unit, mandatoryAvp==1, protectedAvp==1);
			
					//inserted.addAvp(((GroupedAvpImpl)usedServiceUnit).getRaw());
					super.message.getAvps().addAvp(CreditControlAVPCodes.Used_Service_Unit,usedServiceUnit.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.User_Equipment_Info);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.User_Equipment_Info);
			//AvpSet inserted=null;
			//if(avpRep.getVendorId()!=null)
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.User_Equipment_Info, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
			//else
			//	inserted=super.message.getAvps().addGroupedAvp(CreditControlAVPCodes.User_Equipment_Info, mandatoryAvp==1, protectedAvp==1);
	
			//inserted.addAvp(((GroupedAvpImpl)userEquipmentInfo).getRaw());
			super.message.getAvps().addAvp(CreditControlAVPCodes.User_Equipment_Info,userEquipmentInfo.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		}
	}

	

}
