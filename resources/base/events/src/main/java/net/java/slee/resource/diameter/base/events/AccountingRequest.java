package net.java.slee.resource.diameter.base.events;




import java.util.Date;
import java.util.Iterator;


import net.java.slee.resource.diameter.base.events.avp.AccountingRealtimeRequiredType;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

/**
 * Defines an interface representing the Accounting-Request command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 9.7.1.  Accounting-Request
 * 
 *    The Accounting-Request (ACR) command, indicated by the Command-Code
 *    field set to 271 and the Command Flags' 'R' bit set, is sent by a
 *    Diameter node, acting as a client, in order to exchange accounting
 *    information with a peer.
 * 
 *    One of Acct-Application-Id and Vendor-Specific-Application-Id AVPs
 *    MUST be present.  If the Vendor-Specific-Application-Id grouped AVP
 *    is present, it must have an Acct-Application-Id inside.
 * 
 *    The AVP listed below SHOULD include service specific accounting AVPs,
 *    as described in Section 9.3.
 * 
 *    Message Format
 * 
 *       &lt;Accounting-Request&gt; ::= &lt; Diameter Header: 271, REQ, PXY &gt;
 *                 &lt; Session-Id &gt;
 *                 { Origin-Host }
 *                 { Origin-Realm }
 *                 { Destination-Realm }
 *                 { Accounting-Record-Type }
 *                 { Accounting-Record-Number }
 *                 [ Acct-Application-Id ]
 *                 [ Vendor-Specific-Application-Id ]
 *                 [ User-Name ]
 *                 [ Accounting-Sub-Session-Id ]
 *                 [ Accounting-Session-Id ]
 *                 [ Acct-Multi-Session-Id ]
 *                 [ Acct-Interim-Interval ]
 *                 [ Accounting-Realtime-Required ]
 *                 [ Origin-State-Id ]
 *                 [ Event-Timestamp ]
 *               * [ Proxy-Info ]
 *               * [ Route-Record ]
 *               * [ AVP ]
 * </pre>
 */
public interface AccountingRequest extends DiameterMessage {

    int commandCode = 271;

   

    /**
     * Returns true if the Accounting-Record-Type AVP is present in the message.
     */
    boolean hasAccountingRecordType();

    /**
     * Returns the value of the Accounting-Record-Type AVP, of type Enumerated.
     * @return the value of the Accounting-Record-Type AVP or null if it has not been set on this message
     */
    AccountingRecordType getAccountingRecordType();
    /**
     * Returns true if the Destination-Realm AVP is present in the message.
     */
    boolean hasDestinationRealm();
    /**
     * Sets the value of the Accounting-Record-Type AVP, of type Enumerated.
     * @throws IllegalStateException if setAccountingRecordType has already been called
     */
    void setAccountingRecordType(AccountingRecordType accountingRecordType);

    /**
     * Returns true if the Accounting-Record-Number AVP is present in the message.
     */
    boolean hasAccountingRecordNumber();

    /**
     * Returns the value of the Accounting-Record-Number AVP, of type Unsigned32.
     * Use {@link #hasAccountingRecordNumber()} to check the existence of this AVP.  
     * @return the value of the Accounting-Record-Number AVP
     * @throws IllegalStateException if the Accounting-Record-Number AVP has not been set on this message
     */
    long getAccountingRecordNumber();

    /**
     * Sets the value of the Accounting-Record-Number AVP, of type Unsigned32.
     * @throws IllegalStateException if setAccountingRecordNumber has already been called
     */
    void setAccountingRecordNumber(long accountingRecordNumber);

    /**
     * Returns true if the Acct-Application-Id AVP is present in the message.
     */
    boolean hasAcctApplicationId();

    /**
     * Returns the value of the Acct-Application-Id AVP, of type Unsigned32.
     * Use {@link #hasAcctApplicationId()} to check the existence of this AVP.  
     * @return the value of the Acct-Application-Id AVP
     * @throws IllegalStateException if the Acct-Application-Id AVP has not been set on this message
     */
    long getAcctApplicationId();

    /**
     * Sets the value of the Acct-Application-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setAcctApplicationId has already been called
     */
    void setAcctApplicationId(long acctApplicationId);

    /**
     * Returns true if the Vendor-Specific-Application-Id AVP is present in the message.
     */
    boolean hasVendorSpecificApplicationId();

    /**
     * Returns the value of the Vendor-Specific-Application-Id AVP, of type Grouped.
     * @return the value of the Vendor-Specific-Application-Id AVP or null if it has not been set on this message
     */
    VendorSpecificApplicationIdAvp getVendorSpecificApplicationId();

    /**
     * Sets the value of the Vendor-Specific-Application-Id AVP, of type Grouped.
     * @throws IllegalStateException if setVendorSpecificApplicationId has already been called
     */
    void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId);

    /**
     * Returns true if the User-Name AVP is present in the message.
     */
    boolean hasUserName();

    /**
     * Returns the value of the User-Name AVP, of type UTF8String.
     * @return the value of the User-Name AVP or null if it has not been set on this message
     */
    String getUserName();

    /**
     * Sets the value of the User-Name AVP, of type UTF8String.
     * @throws IllegalStateException if setUserName has already been called
     */
    void setUserName(String userName);

    /**
     * Returns true if the Accounting-Sub-Session-Id AVP is present in the message.
     */
    boolean hasAccountingSubSessionId();

    /**
     * Returns the value of the Accounting-Sub-Session-Id AVP, of type Unsigned64.
     * Use {@link #hasAccountingSubSessionId()} to check the existence of this AVP.  
     * @return the value of the Accounting-Sub-Session-Id AVP
     * @throws IllegalStateException if the Accounting-Sub-Session-Id AVP has not been set on this message
     */
    long getAccountingSubSessionId();

    /**
     * Sets the value of the Accounting-Sub-Session-Id AVP, of type Unsigned64.
     * @throws IllegalStateException if setAccountingSubSessionId has already been called
     */
    void setAccountingSubSessionId(long accountingSubSessionId);

    /**
     * Returns true if the Accounting-Session-Id AVP is present in the message.
     */
    boolean hasAccountingSessionId();

    /**
     * Returns the value of the Accounting-Session-Id AVP, of type OctetString.
     * @return the value of the Accounting-Session-Id AVP or null if it has not been set on this message
     */
    byte[] getAccountingSessionId();

    /**
     * Sets the value of the Accounting-Session-Id AVP, of type OctetString.
     * @throws IllegalStateException if setAccountingSessionId has already been called
     */
    void setAccountingSessionId(byte[] accountingSessionId);

    /**
     * Returns true if the Acct-Multi-Session-Id AVP is present in the message.
     */
    boolean hasAcctMultiSessionId();

    /**
     * Returns the value of the Acct-Multi-Session-Id AVP, of type UTF8String.
     * @return the value of the Acct-Multi-Session-Id AVP or null if it has not been set on this message
     */
    String getAcctMultiSessionId();

    /**
     * Sets the value of the Acct-Multi-Session-Id AVP, of type UTF8String.
     * @throws IllegalStateException if setAcctMultiSessionId has already been called
     */
    void setAcctMultiSessionId(String acctMultiSessionId);

    /**
     * Returns true if the Acct-Interim-Interval AVP is present in the message.
     */
    boolean hasAcctInterimInterval();

    /**
     * Returns the value of the Acct-Interim-Interval AVP, of type Unsigned32.
     * Use {@link #hasAcctInterimInterval()} to check the existence of this AVP.  
     * @return the value of the Acct-Interim-Interval AVP
     * @throws IllegalStateException if the Acct-Interim-Interval AVP has not been set on this message
     */
    long getAcctInterimInterval();

    /**
     * Sets the value of the Acct-Interim-Interval AVP, of type Unsigned32.
     * @throws IllegalStateException if setAcctInterimInterval has already been called
     */
    void setAcctInterimInterval(long acctInterimInterval);

    /**
     * Returns true if the Accounting-Realtime-Required AVP is present in the message.
     */
    boolean hasAccountingRealtimeRequired();

    /**
     * Returns the value of the Accounting-Realtime-Required AVP, of type Enumerated.
     * @return the value of the Accounting-Realtime-Required AVP or null if it has not been set on this message
     */
    AccountingRealtimeRequiredType getAccountingRealtimeRequired();

    /**
     * Sets the value of the Accounting-Realtime-Required AVP, of type Enumerated.
     * @throws IllegalStateException if setAccountingRealtimeRequired has already been called
     */
    void setAccountingRealtimeRequired(AccountingRealtimeRequiredType accountingRealtimeRequired);

    /**
     * Returns true if the Origin-State-Id AVP is present in the message.
     */
    boolean hasOriginStateId();

    /**
     * Returns the value of the Origin-State-Id AVP, of type Unsigned32.
     * Use {@link #hasOriginStateId()} to check the existence of this AVP.  
     * @return the value of the Origin-State-Id AVP
     * @throws IllegalStateException if the Origin-State-Id AVP has not been set on this message
     */
    long getOriginStateId();

    /**
     * Sets the value of the Origin-State-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setOriginStateId has already been called
     */
    void setOriginStateId(long originStateId);

    /**
     * Returns true if the Event-Timestamp AVP is present in the message.
     */
    boolean hasEventTimestamp();

    /**
     * Returns the value of the Event-Timestamp AVP, of type Time.
     * @return the value of the Event-Timestamp AVP or null if it has not been set on this message
     */
    Date getEventTimestamp();

    /**
     * Sets the value of the Event-Timestamp AVP, of type Time.
     * @throws IllegalStateException if setEventTimestamp has already been called
     */
    void setEventTimestamp(Date eventTimestamp);

    /**
     * Returns the set of Proxy-Info AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Proxy-Info AVPs have been set.
     * The elements in the given array are ProxyInfo objects.
     */
    ProxyInfoAvp[] getProxyInfos();

    /**
     * Sets a single Proxy-Info AVP in the message, of type Grouped.
     * @throws IllegalStateException if setProxyInfo or setProxyInfos
     *  has already been called
     */
    void setProxyInfo(ProxyInfoAvp proxyInfo);

    /**
     * Sets the set of Proxy-Info AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getProxyInfos() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setProxyInfo or setProxyInfos
     *  has already been called
     */
    void setProxyInfos(ProxyInfoAvp[] proxyInfos);

    /**
     * Returns the set of Route-Record AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Route-Record AVPs have been set.
     * The elements in the given array are DiameterIdentity objects.
     */
    DiameterIdentityAvp[] getRouteRecords();

    /**
     * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
     * @throws IllegalStateException if setRouteRecord or setRouteRecords
     *  has already been called
     */
    void setRouteRecord(DiameterIdentityAvp routeRecord);

    /**
     * Sets the set of Route-Record AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getRouteRecords() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setRouteRecord or setRouteRecords
     *  has already been called
     */
    void setRouteRecords(DiameterIdentityAvp[] routeRecords);


}
