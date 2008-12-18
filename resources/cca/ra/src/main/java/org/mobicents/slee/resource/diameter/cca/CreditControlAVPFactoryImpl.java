/**
 * Start time:16:32:52 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Stack;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.cca.events.avp.CcMoneyAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.CostInformationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RedirectServerAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.SubscriptionIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UnitValueAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvpImpl;


/**
 * Start time:16:32:52 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlAVPFactoryImpl implements CreditControlAVPFactory {

	
	protected DiameterAvpFactory baseAvpFactory = null;
	private static transient Logger logger = Logger.getLogger(CreditControlAVPFactoryImpl.class);

	//protected MessageParser parser = new MessageParser(null);

	protected Stack stack=null;
	
	
	
	public CreditControlAVPFactoryImpl(DiameterAvpFactory baseAvpFactory,
			Stack stack) {
		super();
		this.baseAvpFactory = baseAvpFactory;
		this.stack = stack;
	}
	
	
	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCcMoney()
	 */
	public CcMoneyAvp createCcMoney() {
		
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Money);
		CcMoneyAvpImpl avp=new CcMoneyAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCcMoney(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public CcMoneyAvp createCcMoney(UnitValueAvp unitValue) {
		CcMoneyAvp value= createCcMoney();
		value.setUnitValue(unitValue);
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCostInformation()
	 */
	public CostInformationAvp createCostInformation() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Cost_Information);
		CostInformationAvpImpl avp=new CostInformationAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCostInformation(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp, long)
	 */
	public CostInformationAvp createCostInformation(UnitValueAvp unitValue,
			long currencyCode) {

		CostInformationAvp value= createCostInformation();
		value.setUnitValue(unitValue);
		value.setCurrencyCode(currencyCode);
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createFinalUnitIndication()
	 */
	public FinalUnitIndicationAvp createFinalUnitIndication() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Final_Unit_Indication);
		FinalUnitIndicationAvpImpl avp=new FinalUnitIndicationAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType)
	 */
	public FinalUnitIndicationAvp createFinalUnitIndication(
			FinalUnitActionType finalUnitAction) {
		
		FinalUnitIndicationAvp value= createFinalUnitIndication();
		value.setFinalUnitAction(finalUnitAction);
		
		return value;
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGSUPoolReference()
	 */
	public GSUPoolReferenceAvp createGSUPoolReference() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.G_S_U_Pool_Reference);
		GSUPoolReferenceAvpImpl avp=new GSUPoolReferenceAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGSUPoolReference(long, net.java.slee.resource.diameter.cca.events.avp.CcUnitType, net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
	 */
	public GSUPoolReferenceAvp createGSUPoolReference(long gsuPoolIdentifier,
			CcUnitType ccUnitType, UnitValueAvp unitValue) {
		
		GSUPoolReferenceAvp  value=createGSUPoolReference();
		value.setCreditControlUnitType(ccUnitType);
		value.setUnitValue(unitValue);
		
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGrantedServiceUnit()
	 */
	public GrantedServiceUnitAvp createGrantedServiceUnit() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Granted_Service_Unit);
		GrantedServiceUnitAvpImpl avp=new GrantedServiceUnitAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createMultipleServicesCreditControl()
	 */
	public MultipleServicesCreditControlAvp createMultipleServicesCreditControl() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control);
		MultipleServicesCreditControlAvpImpl avp=new MultipleServicesCreditControlAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRedirectServer()
	 */
	public RedirectServerAvp createRedirectServer() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Redirect_Server);
		RedirectServerAvpImpl avp=new RedirectServerAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRedirectServer(net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType, java.lang.String)
	 */
	public RedirectServerAvp createRedirectServer(
			RedirectAddressType redirectAddressType,
			String redirectServerAddress) {
		
		RedirectServerAvp value=createRedirectServer();
		value.setRedirectServerAddress(redirectServerAddress);
		value.setRedirectAddressType(redirectAddressType);
		
		
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRequestedServiceUnit()
	 */
	public RequestedServiceUnitAvp createRequestedServiceUnit() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
		RequestedServiceUnitAvpImpl avp=new RequestedServiceUnitAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createServiceParameterInfo()
	 */
	public ServiceParameterInfoAvp createServiceParameterInfo() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Parameter_Info);
		ServiceParameterInfoAvpImpl avp=new ServiceParameterInfoAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createServiceParameterInfo(long, byte[])
	 */
	public ServiceParameterInfoAvp createServiceParameterInfo(
			long serviceParameterType, byte[] serviceParameterValue) {

		ServiceParameterInfoAvp value= createServiceParameterInfo();
		value.setServiceParameterType(serviceParameterType);
		value.setServiceParameterValue(serviceParameterValue);
		
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createSubscriptionId()
	 */
	public SubscriptionIdAvp createSubscriptionId() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Subscription_Id);
		SubscriptionIdAvpImpl avp=new SubscriptionIdAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createSubscriptionId(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType, java.lang.String)
	 */
	public SubscriptionIdAvp createSubscriptionId(
			SubscriptionIdType subscriptionIdType, String subscriptionIdData) {

		SubscriptionIdAvp value= createSubscriptionId();
		value.setSubscriptionIdType(subscriptionIdType);
		value.setSubscriptionIdData(subscriptionIdData);
		
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUnitValue()
	 */
	public UnitValueAvp createUnitValue() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Unit_Value);
		UnitValueAvpImpl avp=new UnitValueAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUnitValue(long)
	 */
	public UnitValueAvp createUnitValue(long valueDigits) {

		UnitValueAvp value= createUnitValue();
		value.setValueDigits(valueDigits);
		
		
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUsedServiceUnit()
	 */
	public UsedServiceUnitAvp createUsedServiceUnit() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Used_Service_Unit);
		UsedServiceUnitAvpImpl avp=new UsedServiceUnitAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUserEquipmentInfo()
	 */
	public UserEquipmentInfoAvp createUserEquipmentInfo() {
		AvpRepresentation representation=AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.User_Equipment_Info);
		UserEquipmentInfoAvpImpl avp=new UserEquipmentInfoAvpImpl(representation.getCode(),representation.getVendorId(),representation.isMandatory()?1:0,representation.isProtected()?1:0,new byte[]{});
		return avp;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUserEquipmentInfo(net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType, byte[])
	 */
	public UserEquipmentInfoAvp createUserEquipmentInfo(
			UserEquipmentInfoType userEquipmentInfoType,
			byte[] userEquipmentInfoValue) {

		UserEquipmentInfoAvp value=createUserEquipmentInfo();
		value.setUserEquipmentInfoType(userEquipmentInfoType);
		value.setUserEquipmentInfoValue(userEquipmentInfoValue);
		
		return value;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#getBaseFactory()
	 */
	public DiameterAvpFactory getBaseFactory() {

		return this.baseAvpFactory;
	}

}
