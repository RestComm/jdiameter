package net.java.slee.resource.diameter.cca.events;

import java.util.Date;


import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;


/**
 * <pre> <b>3.  Credit-Control Messages</b>
 *   This section defines new Diameter message Command-Code values that
 *   MUST be supported by all Diameter implementations that conform to
 *   this specification.  The Command Codes are as follows:
 *   Command-Name                  Abbrev.    Code     Reference
 *   -----------------------------------------------------------
 *   Credit-Control-Request        CCR        272      3.1
 *   Credit-Control-Answer         CCA        272      3.2
 *   Diameter Base [DIAMBASE] defines in the section 3.2 the Command Code
 *   ABNF specification.  These formats are observed in Credit-Control
 *   messages.
 * </pre>
 * 
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlMessage extends DiameterMessage {
   final static int commandCode = 272;
  /**
   * Returns the value of the Acct-Multi-Session-Id AVP, of type UTF8String.
   * 
   * @return 
   */
  String getAcctMultiSessionId();
  
  /**
   * Sets the value of the Acct-Multi-Session-Id AVP, of type UTF8String.
   * 
   * @param acctMultiSessionId
   * @throws IllegalStateException 
   */
  void setAcctMultiSessionId(java.lang.String acctMultiSessionId) throws IllegalStateException;
  
  /**
   * Returns true if the Acct-Multi-Session-Id AVP is present in the message.
   * 
   * @return
   */
  boolean hasAcctMultiSessionId();

  /**
   * Returns the value of the Auth-Application-Id AVP, of type Unsigned32.
   * 
   * @return 
   */
  long getAuthApplicationId();
  
  /**
   * Sets the value of the Auth-Application-Id AVP, of type Unsigned32.
   * 
   * @param authApplicationId
   * @throws IllegalStateException 
   */
  void setAuthApplicationId(long authApplicationId) throws IllegalStateException;
    
  /**
   * Returns true if the Auth-Application-Id AVP is present in the message.
   * 
   * @return
   */
  boolean hasAuthApplicationId();

  /**
   * Returns the value of the CC-Request-Number AVP, of type Unsigned32.
   * 
   * @return 
   */
  long getCcRequestNumber();
  
  /**
   * Sets the value of the CC-Request-Number AVP, of type Unsigned32.
   * 
   * @param ccRequestNumber
   * @throws IllegalStateException 
   */
  void setCcRequestNumber(long ccRequestNumber) throws IllegalStateException;

  /**
   * Returns true if the CC-Request-Number AVP is present in the message.
   * 
   * @return
   */
  boolean hasCcRequestNumber();

  /**
   * Returns the value of the CC-Request-Type AVP, of type Enumerated.
   * 
   * @return
   */
  CcRequestType getCcRequestType();
  
  /**
   * Sets the value of the CC-Request-Type AVP, of type Enumerated.
   * 
   * @param ccRequestType
   * @throws IllegalStateException 
   */
  void setCcRequestType(CcRequestType ccRequestType) throws IllegalStateException;
  /**
   * Returns tru if Multiple-Services-Credit-Control AVP is present
   * @return
   */
  public boolean hasMultipleServicesCreditControl();
  
  /**
   * Returns true if the CC-Request-Type AVP is present in the message.
   * 
   * @return
   */
  boolean hasCcRequestType();

  /**
   * Returns the value of the CC-Sub-Session-Id AVP, of type Unsigned64.
   * @return
   */
  long getCcSubSessionId();
  
  /**
   * Sets the value of the CC-Sub-Session-Id AVP, of type Unsigned64.
   * 
   * @param ccSubSessionId
   * @throws IllegalStateException 
   */
  void setCcSubSessionId(long ccSubSessionId) throws IllegalStateException;

  /**
   * Returns true if the CC-Sub-Session-Id AVP is present in the message.
   * 
   * @return
   */
  boolean  hasCcSubSessionId();

  /**
   * Returns the value of the Event-Timestamp AVP, of type Time. 
   * 
   * @return
   */
  Date getEventTimestamp();

  /**
   * Sets the value of the Event-Timestamp AVP, of type Time.
   *  
   * @param eventTimestamp
   * @throws IllegalStateException 
   */
  void setEventTimestamp(java.util.Date eventTimestamp) throws IllegalStateException;

  /**
   * Returns true if the Event-Timestamp AVP is present in the message.
   * 
   * @return
   */
  boolean  hasEventTimestamp();

  /**
   * Returns the set of Multiple-Services-Credit-Control AVPs.
   * 
   * @return
   */
  MultipleServicesCreditControlAvp[] getMultipleServicesCreditControls();
    
  /**
   * Sets a single Multiple-Services-Credit-Control AVP in the message, of type Grouped.
   * 
   * @param multipleServicesCreditControl
   * @throws IllegalStateException 
   */
  void setMultipleServicesCreditControl(MultipleServicesCreditControlAvp multipleServicesCreditControl) throws IllegalStateException;

  /**
   * Sets the set of Multiple-Services-Credit-Control AVPs, with all the values in the given array.
   * 
   * @param multipleServicesCreditControls
   * @throws IllegalStateException 
   */
  void setMultipleServicesCreditControls(MultipleServicesCreditControlAvp[] multipleServicesCreditControls) throws IllegalStateException;

  /**
   * Returns the value of the Origin-State-Id AVP, of type Unsigned32.
   * 
   * @return
   */
  long getOriginStateId();
  
  /**
   * Sets the value of the Origin-State-Id AVP, of type Unsigned32.
   * 
   * @param originStateId
   * @throws IllegalStateException 
   */
  void setOriginStateId(long originStateId) throws IllegalStateException;

  /**
   * Returns true if the Origin-State-Id AVP is present in the message.
   * 
   * @return
   */
  boolean  hasOriginStateId();

  /**
   * Returns the set of Proxy-Info AVPs.
   * 
   * @return
   */
  ProxyInfoAvp[] getProxyInfos();
  
  /**
   * Sets a single Proxy-Info AVP in the message, of type Grouped.
   * 
   * @param proxyInfo
   * @throws IllegalStateException 
   */
  void setProxyInfo(ProxyInfoAvp proxyInfo) throws IllegalStateException;

  /**
   * Sets the set of Proxy-Info AVPs, with all the values in the given array.
   * 
   * @param proxyInfos
   * @throws IllegalStateException 
   */
  void setProxyInfos(ProxyInfoAvp[] proxyInfos) throws IllegalStateException;

  /**
   * Returns the set of Route-Record AVPs.
   * 
   * @return
   */
  DiameterIdentityAvp[] getRouteRecords();
  
  /**
   * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
   * 
   * @param routeRecord
   * @throws IllegalStateException 
   */
  void setRouteRecord(DiameterIdentityAvp routeRecord) throws IllegalStateException;

  /**
   * Sets the set of Route-Record AVPs, with all the values in the given array.
   * 
   * @param routeRecords
   * @throws IllegalStateException 
   */
  void setRouteRecords(DiameterIdentityAvp[] routeRecords) throws IllegalStateException;

  /**
   * Returns the value of the User-Name AVP, of type UTF8String.
   * 
   * @return
   */
  String getUserName();
  
  /**
   * Sets the value of the User-Name AVP, of type UTF8String.
   * 
   * @param userName
   * @throws IllegalStateException 
   */
  void setUserName(String userName) throws IllegalStateException;

  /**
   * Returns true if the User-Name AVP is present in the message.
   * 
   * @return
   */
  boolean  hasUserName();

}
