package net.java.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesIndicatorType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedActionType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;

/**
 * <pre> <b>3.1.  Credit-Control-Request (CCR) Command</b>
 *   The Credit-Control-Request message (CCR) is indicated by the
 *   command-code field being set to 272 and the ’R’ bit being set in the
 *   Command Flags field.  It is used between the Diameter credit-control
 *   client and the credit-control server to request credit authorization
 *   for a given service.
 *   The Auth-Application-Id MUST be set to the value 4, indicating the
 *   Diameter credit-control application.
 *
 *      <Credit-Control-Request> ::= < Diameter Header: 272, REQ, PXY >
 *                                   < Session-Id >
 *                                   { Origin-Host }
 *                                   { Origin-Realm }
 *                                   { Destination-Realm }
 *                                   { Auth-Application-Id }
 *                                   { Service-Context-Id }
 *                                   { CC-Request-Type }
 *                                   { CC-Request-Number }
 *                                   [ Destination-Host ]
 *                                   [ User-Name ]
 *                                   [ CC-Sub-Session-Id ]
 *                                   [ Acct-Multi-Session-Id ]
 *                                   [ Origin-State-Id ]
 *                                   [ Event-Timestamp ]
 *                                  *[ Subscription-Id ]
 *                                   [ Service-Identifier ]
 *                                   [ Termination-Cause ]
 *                                   [ Requested-Service-Unit ]
 *                                   [ Requested-Action ]
 *                                  *[ Used-Service-Unit ]
 *                                   [ Multiple-Services-Indicator ]
 *                                  *[ Multiple-Services-Credit-Control ]
 *                                  *[ Service-Parameter-Info ]
 *                                   [ CC-Correlation-Id ]
 *                                   [ User-Equipment-Info ]
 *                                  *[ Proxy-Info ]
 *                                  *[ Route-Record ]
 *                                  *[ AVP ]
 * </pre>
 *
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlRequest extends CreditControlMessage {

  /**
   * Returns the value of the CC-Correlation-Id AVP, of type OctetString.
   * 
   * @return
   */
  byte[] getCcCorrelationId();

  /**
   * Sets the value of the CC-Correlation-Id AVP, of type OctetString.
   *  
   * @param ccCorrelationId
   * @throws IllegalStateException 
   */
  void setCcCorrelationId(byte[] ccCorrelationId) throws IllegalStateException;

  /**
   * Returns true if the CC-Correlation-Id AVP is present in the message.
   * 
   * @return
   */
  boolean hasCcCorrelationId();

  /**
   * Returns the value of the Multiple-Services-Indicator AVP, of type Enumerated.
   * 
   * @return
   */
  MultipleServicesIndicatorType  getMultipleServicesIndicator();
  
  /**
   * Sets the value of the Multiple-Services-Indicator AVP, of type Enumerated.
   *  
   * @param multipleServicesIndicator
   * @throws IllegalStateException 
   */
  void setMultipleServicesIndicator(MultipleServicesIndicatorType multipleServicesIndicator) throws IllegalStateException;

  /**
   * Returns true if the Multiple-Services-Indicator AVP is present in the message.
   * 
   * @return
   */
  boolean hasMultipleServicesIndicator();

  /**
   * Returns the value of the Requested-Action AVP, of type Enumerated.
   * 
   * @return
   */
  RequestedActionType  getRequestedAction();
  
  /**
   * Sets the value of the Requested-Action AVP, of type Enumerated.
   * 
   * @param requestedAction
   * @throws IllegalStateException 
   */
  void setRequestedAction(RequestedActionType requestedAction) throws IllegalStateException;

  /**
   * Returns true if the Requested-Action AVP is present in the message.
   * 
   * @return
   */
  boolean hasRequestedAction();

  /**
   * Returns the value of the Requested-Service-Unit AVP, of type Grouped.
   * 
   * @return
   */
  RequestedServiceUnitAvp getRequestedServiceUnit();
  
  /**
   * Sets the value of the Requested-Service-Unit AVP, of type Grouped.
   * 
   * @param requestedServiceUnit
   * @throws IllegalStateException 
   */
  void setRequestedServiceUnit(RequestedServiceUnitAvp requestedServiceUnit) throws IllegalStateException;

  /**
   * Returns true if the Requested-Service-Unit AVP is present in the message.
   * 
   * @return
   */
  boolean hasRequestedServiceUnit();

  /**
   * Returns the value of the Service-Context-Id AVP, of type UTF8String.
   * 
   * @return
   */
  java.lang.String getServiceContextId();
  
  /**
   * Sets the value of the Service-Context-Id AVP, of type UTF8String.
   * 
   * @param serviceContextId
   * @throws IllegalStateException 
   */
  void setServiceContextId(java.lang.String serviceContextId) throws IllegalStateException;

  /**
   * Returns true if the Service-Context-Id AVP is present in the message.
   * 
   * @return
   */
  boolean hasServiceContextId();

  /**
   * Returns the value of the Service-Identifier AVP, of type Unsigned32.
   * 
   * @return
   */
  long getServiceIdentifier();
  
  /**
   * Sets the value of the Service-Identifier AVP, of type Unsigned32.
   * 
   * @param serviceIdentifier
   * @throws IllegalStateException 
   */
  void setServiceIdentifier(long serviceIdentifier) throws IllegalStateException;

  /**
   * Returns true if the Service-Identifier AVP is present in the message.
   * 
   * @return
   */
  boolean hasServiceIdentifier();

  /**
   * Returns the set of Service-Parameter-Info AVPs.
   * 
   * @return
   */
  ServiceParameterInfoAvp[] getServiceParameterInfos();
  
  /**
   * Sets a single Service-Parameter-Info AVP in the message, of type Grouped.
   * 
   * @param serviceParameterInfo
   * @throws IllegalStateException 
   */
  void setServiceParameterInfo(ServiceParameterInfoAvp serviceParameterInfo) throws IllegalStateException;

  /**
   * Sets the set of Service-Parameter-Info AVPs, with all the values in the given array.
   * 
   * @param serviceParameterInfos
   * @throws IllegalStateException 
   */
  void setServiceParameterInfos(ServiceParameterInfoAvp[] serviceParameterInfos) throws IllegalStateException;

  /**
   * Returns the set of Subscription-Id AVPs.
   * 
   * @return
   */
  SubscriptionIdAvp[] getSubscriptionIds();
  
  /**
   * Sets a single Subscription-Id AVP in the message, of type Grouped.
   * 
   * @param subscriptionId
   * @throws IllegalStateException 
   */
  void setSubscriptionId(SubscriptionIdAvp subscriptionId) throws IllegalStateException;

  /**
   * Sets the set of Subscription-Id AVPs, with all the values in the given array.
   * 
   * @param subscriptionIds
   * @throws IllegalStateException 
   */
  void setSubscriptionIds(SubscriptionIdAvp[] subscriptionIds) throws IllegalStateException;

  /**
   * Returns the value of the Termination-Cause AVP, of type Enumerated.
   * 
   * @return
   */
  TerminationCauseType getTerminationCause();
  
  /**
   * Sets the value of the Termination-Cause AVP, of type Enumerated.
   * 
   * @param terminationCause
   * @throws IllegalStateException 
   */
  void setTerminationCause(TerminationCauseType terminationCause) throws IllegalStateException;

  /**
   * Returns true if the Termination-Cause AVP is present in the message.
   * 
   * @return
   */
  boolean hasTerminationCause();

  /**
   * Returns the set of Used-Service-Unit AVPs.
   * 
   * @return
   */
  UsedServiceUnitAvp[]  getUsedServiceUnits();
  
  /**
   * Sets a single Used-Service-Unit AVP in the message, of type Grouped.
   * 
   * @param usedServiceUnit
   * @throws IllegalStateException 
   */
  void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit) throws IllegalStateException;

  /**
   * Sets the set of Used-Service-Unit AVPs, with all the values in the given array.
   * 
   * @param usedServiceUnits
   * @throws IllegalStateException 
   */
  void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits) throws IllegalStateException;

  /**
   * Returns the value of the User-Equipment-Info AVP, of type Grouped.
   * 
   * @return
   */
  UserEquipmentInfoAvp  getUserEquipmentInfo();

  /**
   * Sets the value of the User-Equipment-Info AVP, of type Grouped.
   * 
   * @param userEquipmentInfo
   * @throws IllegalStateException 
   */
  void setUserEquipmentInfo(UserEquipmentInfoAvp userEquipmentInfo) throws IllegalStateException;

  /**
   * Returns true if the User-Equipment-Info AVP is present in the message.
   * 
   * @return
   */
  boolean hasUserEquipmentInfo();
  
}
