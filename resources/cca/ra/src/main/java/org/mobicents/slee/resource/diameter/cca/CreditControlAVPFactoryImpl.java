package org.mobicents.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
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
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlAVPFactoryImpl implements CreditControlAVPFactory {

  protected DiameterAvpFactory baseAvpFactory = null;

  public CreditControlAVPFactoryImpl(DiameterAvpFactory baseAvpFactory)
  {
    super();

    this.baseAvpFactory = baseAvpFactory;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCcMoney()
   */
  public CcMoneyAvp createCcMoney()
  {
    return (CcMoneyAvp) AvpUtilities.createAvp( CreditControlAVPCodes.CC_Money, null, CcMoneyAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCcMoney(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
   */
  public CcMoneyAvp createCcMoney(UnitValueAvp unitValue)
  {
    // Create the empty AVP
    CcMoneyAvp avp = createCcMoney();

    // Set the provided AVP values
    avp.setUnitValue(unitValue);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCostInformation()
   */
  public CostInformationAvp createCostInformation()
  {
    return (CostInformationAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Cost_Information, null, CostInformationAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCostInformation(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp, long)
   */
  public CostInformationAvp createCostInformation(UnitValueAvp unitValue, long currencyCode)
  {
    // Create the empty AVP
    CostInformationAvp avp = createCostInformation();

    // Set the provided AVP values
    avp.setUnitValue(unitValue);
    avp.setCurrencyCode(currencyCode);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createFinalUnitIndication()
   */
  public FinalUnitIndicationAvp createFinalUnitIndication()
  {
    return (FinalUnitIndicationAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Final_Unit_Indication, null, FinalUnitIndicationAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType)
   */
  public FinalUnitIndicationAvp createFinalUnitIndication(FinalUnitActionType finalUnitAction)
  {
    // Create the empty AVP
    FinalUnitIndicationAvp avp = createFinalUnitIndication();

    // Set the provided AVP values
    avp.setFinalUnitAction(finalUnitAction);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGSUPoolReference()
   */
  public GSUPoolReferenceAvp createGSUPoolReference()
  {
    return (GSUPoolReferenceAvp) AvpUtilities.createAvp( CreditControlAVPCodes.G_S_U_Pool_Reference, null, GSUPoolReferenceAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGSUPoolReference(long, net.java.slee.resource.diameter.cca.events.avp.CcUnitType, net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
   */
  public GSUPoolReferenceAvp createGSUPoolReference(long gsuPoolIdentifier, CcUnitType ccUnitType, UnitValueAvp unitValue)
  {
    // Create the empty AVP
    GSUPoolReferenceAvp avp = createGSUPoolReference();

    // Set the provided AVP values
    avp.setGSUPoolIdentifier(gsuPoolIdentifier);
    avp.setCreditControlUnitType(ccUnitType);
    avp.setUnitValue(unitValue);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGrantedServiceUnit()
   */
  public GrantedServiceUnitAvp createGrantedServiceUnit()
  {
    return (GrantedServiceUnitAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Granted_Service_Unit, null, GrantedServiceUnitAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createMultipleServicesCreditControl()
   */
  public MultipleServicesCreditControlAvp createMultipleServicesCreditControl()
  {
    return (MultipleServicesCreditControlAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Multiple_Services_Credit_Control, null, MultipleServicesCreditControlAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRedirectServer()
   */
  public RedirectServerAvp createRedirectServer()
  {
    return (RedirectServerAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Redirect_Server, null, RedirectServerAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRedirectServer(net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType, java.lang.String)
   */
  public RedirectServerAvp createRedirectServer(RedirectAddressType redirectAddressType, String redirectServerAddress)
  {
    // Create the empty AVP
    RedirectServerAvp avp = createRedirectServer();

    // Set the provided AVP values
    avp.setRedirectServerAddress(redirectServerAddress);
    avp.setRedirectAddressType(redirectAddressType);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRequestedServiceUnit()
   */
  public RequestedServiceUnitAvp createRequestedServiceUnit()
  {
    return (RequestedServiceUnitAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Requested_Service_Unit, null, RequestedServiceUnitAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createServiceParameterInfo()
   */
  public ServiceParameterInfoAvp createServiceParameterInfo()
  {
    return (ServiceParameterInfoAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Service_Parameter_Info, null, ServiceParameterInfoAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createServiceParameterInfo(long, byte[])
   */
  public ServiceParameterInfoAvp createServiceParameterInfo(long serviceParameterType, byte[] serviceParameterValue)
  {
    // Create the empty AVP
    ServiceParameterInfoAvp avp = createServiceParameterInfo();

    // Set the provided AVP values
    avp.setServiceParameterType(serviceParameterType);
    avp.setServiceParameterValue(serviceParameterValue);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createSubscriptionId()
   */
  public SubscriptionIdAvp createSubscriptionId()
  {
    return (SubscriptionIdAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Subscription_Id, null, SubscriptionIdAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createSubscriptionId(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType, java.lang.String)
   */
  public SubscriptionIdAvp createSubscriptionId(SubscriptionIdType subscriptionIdType, String subscriptionIdData)
  {
    // Create the empty AVP
    SubscriptionIdAvp avp = createSubscriptionId();

    // Set the provided AVP values
    avp.setSubscriptionIdType(subscriptionIdType);
    avp.setSubscriptionIdData(subscriptionIdData);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUnitValue()
   */
  public UnitValueAvp createUnitValue()
  {
    return (UnitValueAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Unit_Value, null, UnitValueAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUnitValue(long)
   */
  public UnitValueAvp createUnitValue(long valueDigits)
  {
    // Create the empty AVP
    UnitValueAvp avp = createUnitValue();

    // Set the provided AVP values
    avp.setValueDigits(valueDigits);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUsedServiceUnit()
   */
  public UsedServiceUnitAvp createUsedServiceUnit()
  {
    return (UsedServiceUnitAvp) AvpUtilities.createAvp( CreditControlAVPCodes.Used_Service_Unit, null, UsedServiceUnitAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUserEquipmentInfo()
   */
  public UserEquipmentInfoAvp createUserEquipmentInfo()
  {
    return (UserEquipmentInfoAvp) AvpUtilities.createAvp( CreditControlAVPCodes.User_Equipment_Info, null, UserEquipmentInfoAvpImpl.class );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUserEquipmentInfo(net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType, byte[])
   */
  public UserEquipmentInfoAvp createUserEquipmentInfo(UserEquipmentInfoType userEquipmentInfoType, byte[] userEquipmentInfoValue)
  {
    // Create the empty AVP
    UserEquipmentInfoAvp avp = createUserEquipmentInfo();

    // Set the provided AVP values
    avp.setUserEquipmentInfoType(userEquipmentInfoType);
    avp.setUserEquipmentInfoValue(userEquipmentInfoValue);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#getBaseFactory()
   */
  public DiameterAvpFactory getBaseFactory()
  {
    return this.baseAvpFactory;
  }
}
