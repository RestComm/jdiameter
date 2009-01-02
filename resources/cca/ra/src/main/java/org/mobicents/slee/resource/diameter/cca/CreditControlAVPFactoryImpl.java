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
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlAVPFactoryImpl implements CreditControlAVPFactory {

  protected DiameterAvpFactory baseAvpFactory = null;

  private AvpDictionary avpDictionary = AvpDictionary.INSTANCE;

  protected Stack stack = null;

  public CreditControlAVPFactoryImpl(DiameterAvpFactory baseAvpFactory, Stack stack)
  {
    super();

    this.baseAvpFactory = baseAvpFactory;
    this.stack = stack;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createCcMoney()
   */
  public CcMoneyAvp createCcMoney()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.CC_Money);

    // Create the AVP with the provided representation data
    CcMoneyAvpImpl avp = new CcMoneyAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Cost_Information);

    // Create the AVP with the provided representation data
    CostInformationAvpImpl avp = new CostInformationAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Final_Unit_Indication);

    // Create the AVP with the provided representation data
    FinalUnitIndicationAvpImpl avp=new FinalUnitIndicationAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.G_S_U_Pool_Reference);

    // Create the AVP with the provided representation data
    GSUPoolReferenceAvpImpl avp = new GSUPoolReferenceAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGSUPoolReference(long, net.java.slee.resource.diameter.cca.events.avp.CcUnitType, net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
   */
  public GSUPoolReferenceAvp createGSUPoolReference(long gsuPoolIdentifier, CcUnitType ccUnitType, UnitValueAvp unitValue)
  {
    // Create the empty AVP
    GSUPoolReferenceAvp avp = createGSUPoolReference();

    // Set the provided AVP values
    avp.setCreditControlUnitType(ccUnitType);
    avp.setUnitValue(unitValue);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createGrantedServiceUnit()
   */
  public GrantedServiceUnitAvp createGrantedServiceUnit()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Granted_Service_Unit);

    // Create the AVP with the provided representation data
    GrantedServiceUnitAvpImpl avp = new GrantedServiceUnitAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createMultipleServicesCreditControl()
   */
  public MultipleServicesCreditControlAvp createMultipleServicesCreditControl()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control);

    // Create the AVP with the provided representation data
    MultipleServicesCreditControlAvpImpl avp = new MultipleServicesCreditControlAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createRedirectServer()
   */
  public RedirectServerAvp createRedirectServer()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Redirect_Server);

    // Create the AVP with the provided representation data
    RedirectServerAvpImpl avp = new RedirectServerAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep=avpDictionary.getAvp(CreditControlAVPCodes.Requested_Service_Unit);

    // Create the AVP with the provided representation data
    RequestedServiceUnitAvpImpl avp = new RequestedServiceUnitAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createServiceParameterInfo()
   */
  public ServiceParameterInfoAvp createServiceParameterInfo()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Service_Parameter_Info);

    // Create the AVP with the provided representation data
    ServiceParameterInfoAvpImpl avp = new ServiceParameterInfoAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Subscription_Id);

    // Create the AVP with the provided representation data
    SubscriptionIdAvpImpl avp = new SubscriptionIdAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Unit_Value);

    // Create the AVP with the provided representation data
    UnitValueAvpImpl avp = new UnitValueAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.Used_Service_Unit);

    // Create the AVP with the provided representation data
    UsedServiceUnitAvpImpl avp = new UsedServiceUnitAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlAVPFactory#createUserEquipmentInfo()
   */
  public UserEquipmentInfoAvp createUserEquipmentInfo()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(CreditControlAVPCodes.User_Equipment_Info);

    // Create the AVP with the provided representation data
    UserEquipmentInfoAvpImpl avp = new UserEquipmentInfoAvpImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
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
