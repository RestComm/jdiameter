package net.java.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
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

/**
 * Factory to support the creation of Grouped AVP instances.
 *
 * <br>Super project:  mobicents
 * <br>10:54:41 AM Dec 30, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface CreditControlAVPFactory {


  public DiameterAvpFactory getBaseFactory();

  /**
   * Create an empty CcMoney (Grouped AVP) instance.
   * 
   * @return
   */
  CcMoneyAvp createCcMoney();

  /**
   * Create a CcMoney (Grouped AVP) instance using required AVP values.
   * 
   * @param unitValue
   * @return
   */
  CcMoneyAvp createCcMoney(UnitValueAvp unitValue);

  /**
   * Create an empty CostInformation (Grouped AVP) instance.
   * 
   * @return
   */
  CostInformationAvp createCostInformation();

  /**
   * Create a CostInformation (Grouped AVP) instance using required AVP values.
   * 
   * @param unitValue
   * @param currencyCode
   * @return
   */
  CostInformationAvp createCostInformation(UnitValueAvp unitValue, long currencyCode);

  /**
   * Create an empty FinalUnitIndication (Grouped AVP) instance.
   * 
   * @return
   */
  FinalUnitIndicationAvp createFinalUnitIndication();

  /**
   * Create a FinalUnitIndication (Grouped AVP) instance using required AVP values.
   * 
   * @param finalUnitType
   * @return
   */
  FinalUnitIndicationAvp createFinalUnitIndication(FinalUnitActionType finalUnitType);

  /**
   * Create an empty GrantedServiceUnit (Grouped AVP) instance.
   * 
   * @return
   */
  GrantedServiceUnitAvp createGrantedServiceUnit();

  /**
   * Create an empty GSUPoolReference (Grouped AVP) instance.
   * 
   * @return
   */
  GSUPoolReferenceAvp createGSUPoolReference();

  /**
   * Create a GSUPoolReference (Grouped AVP) instance using required AVP values.
   * 
   * @param gsuPoolIdentifier
   * @param ccUnitType
   * @param unitValue
   * @return
   */
  GSUPoolReferenceAvp createGSUPoolReference(long gsuPoolIdentifier, CcUnitType ccUnitType, UnitValueAvp unitValue);

  /**
   * Create an empty MultipleServicesCreditControl (Grouped AVP) instance.
   * 
   * @return
   */
  MultipleServicesCreditControlAvp createMultipleServicesCreditControl();

  /**
   * Create an empty RedirectServer (Grouped AVP) instance.
   * 
   * @return
   */
  RedirectServerAvp createRedirectServer();

  /**
   * Create a RedirectServer (Grouped AVP) instance using required AVP values.
   * 
   * @param redirectAddressType
   * @param redirectServerAddress
   * @return
   */
  RedirectServerAvp createRedirectServer(RedirectAddressType redirectAddressType, String redirectServerAddress);

  /**
   * Create an empty RequestedServiceUnit (Grouped AVP) instance.
   * 
   * @return
   */
  RequestedServiceUnitAvp createRequestedServiceUnit();

  /**
   * Create an empty ServiceParameterInfo (Grouped AVP) instance.
   * 
   * @return
   */
  ServiceParameterInfoAvp createServiceParameterInfo();

  /**
   * Create a ServiceParameterInfo (Grouped AVP) instance using required AVP values.
   * 
   * @param serviceParameterType
   * @param serviceParameterValue
   * @return
   */
  ServiceParameterInfoAvp createServiceParameterInfo(long serviceParameterType, byte[] serviceParameterValue);

  /**
   * Create an empty SubscriptionId (Grouped AVP) instance.
   * 
   * @return
   */
  SubscriptionIdAvp createSubscriptionId();

  /**
   * Create a SubscriptionId (Grouped AVP) instance using required AVP values.
   * 
   * @param subscriptionIdType
   * @param subscriptionIdData
   * @return
   */
  SubscriptionIdAvp createSubscriptionId(SubscriptionIdType subscriptionIdType, String subscriptionIdData);

  /**
   * Create an empty UnitValue (Grouped AVP) instance.
   * 
   * @return
   */
  UnitValueAvp createUnitValue();

  /**
   * Create a UnitValue (Grouped AVP) instance using required AVP values.
   * 
   * @param valueDigits
   * @return
   */
  UnitValueAvp createUnitValue(long valueDigits);

  /**
   * Create an empty UsedServiceUnit (Grouped AVP) instance.
   * 
   * @return
   */
  UsedServiceUnitAvp createUsedServiceUnit();

  /**
   * Create an empty EquipmentInfo (Grouped AVP) instance.
   * 
   * @return
   */
  UserEquipmentInfoAvp createUserEquipmentInfo();

  /**
   * Create a UserEquipmentInfo (Grouped AVP) instance using required AVP values.
   * 
   * @param userEquipmentInfoType
   * @param userEquipmentInfoValue
   * @return
   */
  UserEquipmentInfoAvp createUserEquipmentInfo(UserEquipmentInfoType userEquipmentInfoType, byte[] userEquipmentInfoValue);
}