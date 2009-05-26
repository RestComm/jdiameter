package net.java.slee.resource.diameter.cca.events;


import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.RedirectHostUsageType;
import net.java.slee.resource.diameter.cca.events.avp.CcSessionFailoverType;
import net.java.slee.resource.diameter.cca.events.avp.CheckBalanceResultType;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlFailureHandlingType;
import net.java.slee.resource.diameter.cca.events.avp.DirectDebitingFailureHandlingType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;

/**
 * <pre> <b>3.2.  Credit-Control-Answer (CCA) Command</b>
 *   The Credit-Control-Answer message (CCA) is indicated by the command-
 *   code field being set to 272 and the ’R’ bit being cleared in the
 *   Command Flags field.  It is used between the credit-control server
 *   and the Diameter credit-control client to acknowledge a Credit-
 *   Control-Request command.
 *   Message Format
 *      <Credit-Control-Answer> ::= < Diameter Header: 272, PXY >
 *                                  < Session-Id >
 *                                  { Result-Code }
 *                                  { Origin-Host }
 *                                  { Origin-Realm }
 *                                  { Auth-Application-Id }
 *                                  { CC-Request-Type }
 *                                  { CC-Request-Number }
 *                                  [ User-Name ]
 *                                  [ CC-Session-Failover ]
 *                                  [ CC-Sub-Session-Id ]
 *                                  [ Acct-Multi-Session-Id ]
 *                                  [ Origin-State-Id ]
 *                                  [ Event-Timestamp ]
 *                                  [ Granted-Service-Unit ]
 *                                 *[ Multiple-Services-Credit-Control ]
 *                                  [ Cost-Information]
 *                                  [ Final-Unit-Indication ]
 *                                  [ Check-Balance-Result ]
 *                                  [ Credit-Control-Failure-Handling ]
 *                                  [ Direct-Debiting-Failure-Handling ]
 *                                  [ Validity-Time]
 *                                 *[ Redirect-Host]
 *                                  [ Redirect-Host-Usage ]
 *                                  [ Redirect-Max-Cache-Time ]
 *                                 *[ Proxy-Info ]
 *                                 *[ Route-Record ]
 *                                 *[ Failed-AVP ]
 *                                 *[ AVP ]
 * </pre>
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlAnswer extends CreditControlMessage {

  /**
   * Returns the value of the CC-Session-Failover AVP, of type Enumerated.
   * 
   * @return
   */
  CcSessionFailoverType getCcSessionFailover();
  
  /**
   * Sets the value of the CC-Session-Failover AVP, of type Enumerated.
   * 
   * @param ccSessionFailover
   * @throws IllegalStateException 
   */
  void setCcSessionFailover(CcSessionFailoverType ccSessionFailover) throws IllegalStateException;

  /**
   * Returns true if the CC-Session-Failover AVP is present in the message.
   * 
   * @return
   */
  boolean hasCcSessionFailover();

  /**
   * Returns the value of the Check-Balance-Result AVP, of type Enumerated.
   * 
   * @return
   */
  CheckBalanceResultType getCheckBalanceResult();
  
  /**
   * Sets the value of the Check-Balance-Result AVP, of type Enumerated.
   * 
   * @param checkBalanceResult
   * @throws IllegalStateException 
   */
  void setCheckBalanceResult(CheckBalanceResultType checkBalanceResult) throws IllegalStateException;

  /**
   * Returns true if the Check-Balance-Result AVP is present in the message.
   * 
   * @return
   */
  boolean hasCheckBalanceResult();

  /**
   * Returns the value of the Cost-Information AVP, of type Grouped.
   * 
   * @return
   */
  CostInformationAvp getCostInformation();
  
  /**
   * Sets the value of the Cost-Information AVP, of type Grouped.
   * 
   * @param costInformation
   * @throws IllegalStateException 
   */
  void setCostInformation(CostInformationAvp costInformation) throws IllegalStateException;

  /**
   * Returns true if the Cost-Information AVP is present in the message.
   * 
   * @return
   */
  boolean hasCostInformation();

  /**
   * Returns the value of the Credit-Control-Failure-Handling AVP, of type Enumerated.
   * 
   * @return
   */
  CreditControlFailureHandlingType getCreditControlFailureHandling();
  
  /**
   * Sets the value of the Credit-Control-Failure-Handling AVP, of type Enumerated.
   * 
   * @param creditControlFailureHandling
   * @throws IllegalStateException 
   */
  void setCreditControlFailureHandling(CreditControlFailureHandlingType creditControlFailureHandling) throws IllegalStateException;

  /**
   * Returns true if the Credit-Control-Failure-Handling AVP is present in the message.
   * 
   * @return
   */
  boolean hasCreditControlFailureHandling();

  /**
   * Returns the value of the Direct-Debiting-Failure-Handling AVP, of type Enumerated.
   * 
   * @return
   */
  DirectDebitingFailureHandlingType getDirectDebitingFailureHandling();
  
  /**
   * Sets the value of the Direct-Debiting-Failure-Handling AVP, of type Enumerated.
   * 
   * @param directDebitingFailureHandling
   * @throws IllegalStateException 
   */
  void setDirectDebitingFailureHandling(DirectDebitingFailureHandlingType directDebitingFailureHandling) throws IllegalStateException;

  /**
   * Returns true if the Direct-Debiting-Failure-Handling AVP is present in the message.
   * 
   * @return
   */
  boolean hasDirectDebitingFailureHandling();

  /**
   * Returns the set of Failed-AVP AVPs.
   * 
   * @return
   */
  FailedAvp[] getFailedAvps();
  
  /**
   * Sets a single Failed-AVP AVP in the message, of type Grouped.
   * 
   * @param failedAvp
   * @throws IllegalStateException 
   */
  void setFailedAvp(FailedAvp failedAvp) throws IllegalStateException;

  /**
   * Sets the set of Failed-AVP AVPs, with all the values in the given array.
   * 
   * @param failedAvps
   * @throws IllegalStateException 
   */
  void setFailedAvps(FailedAvp[] failedAvps) throws IllegalStateException;

  /**
   * Returns the value of the Final-Unit-Indication AVP, of type Grouped.
   * 
   * @return
   */
  FinalUnitIndicationAvp getFinalUnitIndication();
  
  /**
   * Sets the value of the Final-Unit-Indication AVP, of type Grouped.
   * 
   * @param finalUnitIndication
   * @throws IllegalStateException 
   */
  void setFinalUnitIndication(FinalUnitIndicationAvp finalUnitIndication) throws IllegalStateException;

  /**
   * Returns true if the Final-Unit-Indication AVP is present in the message.
   * 
   * @return
   */
  boolean hasFinalUnitIndication();

  /**
   * Returns the value of the Granted-Service-Unit AVP, of type Grouped.
   * 
   * @return
   */
  GrantedServiceUnitAvp getGrantedServiceUnit();
  
  /**
   * Sets the value of the Granted-Service-Unit AVP, of type Grouped.
   * 
   * @param grantedServiceUnit
   * @throws IllegalStateException 
   */
  void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit) throws IllegalStateException;

  /**
   * Returns true if the Granted-Service-Unit AVP is present in the message.
   * 
   * @return
   */
  boolean hasGrantedServiceUnit();

  /**
   * Returns the set of Redirect-Host AVPs.
   * 
   * @return
   */
  net.java.slee.resource.diameter.base.events.avp.DiameterURI[] getRedirectHosts();;
  
  /**
   * Sets a single Redirect-Host AVP in the message, of type DiameterURI.
   * 
   * @param redirectHost
   * @throws IllegalStateException 
   */
  void setRedirectHost(DiameterURI redirectHost) throws IllegalStateException;

  /**
   * Sets the set of Redirect-Host AVPs, with all the values in the given array.
   * 
   * @param redirectHosts
   * @throws IllegalStateException 
   */
  void setRedirectHosts(DiameterURI[] redirectHosts) throws IllegalStateException;

  /**
   * Returns the value of the Redirect-Host-Usage AVP, of type Enumerated.
   * 
   * @return
   */
  RedirectHostUsageType getRedirectHostUsage();
  
  /**
   * Sets the value of the Redirect-Host-Usage AVP, of type Enumerated.
   * 
   * @param redirectHostUsage
   * @throws IllegalStateException 
   */
  void setRedirectHostUsage(RedirectHostUsageType redirectHostUsage) throws IllegalStateException;

  /**
   * Returns true if the Redirect-Host-Usage AVP is present in the message.
   * 
   * @return
   */
  boolean hasRedirectHostUsage();

  /**
   * Returns the value of the Redirect-Max-Cache-Time AVP, of type Unsigned32.
   * 
   * @return
   */
  long getRedirectMaxCacheTime();
  
  /**
   * Sets the value of the Redirect-Max-Cache-Time AVP, of type Unsigned32.
   * 
   * @param redirectMaxCacheTime
   * @throws IllegalStateException 
   */
  void setRedirectMaxCacheTime(long redirectMaxCacheTime) throws IllegalStateException;

  /**
   * Returns true if the Redirect-Max-Cache-Time AVP is present in the message.
   * 
   * @return
   */
  boolean hasRedirectMaxCacheTime();

  /**
   * Returns the value of the Result-Code AVP, of type Unsigned32.
   * 
   * @return
   */
  long getResultCode();
  
  /**
   * Sets the value of the Result-Code AVP, of type Unsigned32.
   * 
   * @param resultCode
   * @throws IllegalStateException 
   */
  void setResultCode(long resultCode) throws IllegalStateException;

  /**
   * Returns true if the Result-Code AVP is present in the message.
   * 
   * @return
   */
  boolean hasResultCode();

  /**
   * Returns the value of the Validity-Time AVP, of type Unsigned32.
   * 
   * @return
   */
  long getValidityTime();
  
  /**
   * Sets the value of the Validity-Time AVP, of type Unsigned32.
   * 
   * @param validityTime
   * @throws IllegalStateException 
   */
  void setValidityTime(long validityTime) throws IllegalStateException;

  /**
   * Returns true if the Validity-Time AVP is present in the message.
   * 
   * @return
   */
  boolean hasValidityTime();
}
