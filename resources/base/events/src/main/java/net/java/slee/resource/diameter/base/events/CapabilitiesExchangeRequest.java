package net.java.slee.resource.diameter.base.events;


import java.util.Iterator;


import net.java.slee.resource.diameter.base.events.avp.AddressAvp;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

/**
 * Defines an interface representing the Capabilities-Exchange-Request command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 5.3.1.  Capabilities-Exchange-Request
 * 
 *    The Capabilities-Exchange-Request (CER), indicated by the Command-
 *    Code set to 257 and the Command Flags' 'R' bit set, is sent to
 *    exchange local capabilities.  Upon detection of a transport failure,
 *    this message MUST NOT be sent to an alternate peer.
 * 
 *    When Diameter is run over SCTP [SCTP], which allows for connections
 *    to span multiple interfaces and multiple IP addresses, the
 *    Capabilities-Exchange-Request message MUST contain one Host-IP-
 *    Address AVP for each potential IP address that MAY be locally used
 *    when transmitting Diameter messages.
 * 
 *    Message Format
 * 
 *       &lt;Capabilities-Exchange-Request&gt; ::= &lt; Diameter Header: 257, REQ &gt;
 *                 { Origin-Host }
 *                 { Origin-Realm }
 *              1* { Host-IP-Address }
 *                 { Vendor-Id }
 *                 { Product-Name }
 *                 [ Origin-State-Id ]
 *               * [ Supported-Vendor-Id ]
 *               * [ Auth-Application-Id ]
 *               * [ Inband-Security-Id ]
 *               * [ Acct-Application-Id ]
 *               * [ Vendor-Specific-Application-Id ]
 *                 [ Firmware-Revision ]
 *               * [ AVP ]
 * </pre>
 */
public interface CapabilitiesExchangeRequest extends DiameterMessage {

    int commandCode = 257;


    /**
     * Returns the set of Host-IP-Address AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Host-IP-Address AVPs have been set.
     * The elements in the given array are Address objects.
     */
    AddressAvp[] getHostIpAddresses();

    /**
     * Sets a single Host-IP-Address AVP in the message, of type Address.
     * @throws IllegalStateException if setHostIpAddress or setHostIpAddresses
     *  has already been called
     */
    void setHostIpAddress(AddressAvp hostIpAddress);

    /**
     * Sets the set of Host-IP-Address AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getHostIpAddresses() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setHostIpAddress or setHostIpAddresses
     *  has already been called
     */
    void setHostIpAddresses(AddressAvp[] hostIpAddresses);

    /**
     * Returns true if the Vendor-Id AVP is present in the message.
     */
    boolean hasVendorId();

    /**
     * Returns the value of the Vendor-Id AVP, of type Unsigned32.
     * Use {@link #hasVendorId()} to check the existence of this AVP.  
     * @return the value of the Vendor-Id AVP
     * @throws IllegalStateException if the Vendor-Id AVP has not been set on this message
     */
    long getVendorId();

    /**
     * Sets the value of the Vendor-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setVendorId has already been called
     */
    void setVendorId(long vendorId);

    /**
     * Returns true if the Product-Name AVP is present in the message.
     */
    boolean hasProductName();

    /**
     * Returns the value of the Product-Name AVP, of type UTF8String.
     * @return the value of the Product-Name AVP or null if it has not been set on this message
     */
    String getProductName();

    /**
     * Sets the value of the Product-Name AVP, of type UTF8String.
     * @throws IllegalStateException if setProductName has already been called
     */
    void setProductName(String productName);

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
     * Returns the set of Supported-Vendor-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Supported-Vendor-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    long[] getSupportedVendorIds();

    /**
     * Sets a single Supported-Vendor-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setSupportedVendorId or setSupportedVendorIds
     *  has already been called
     */
    void setSupportedVendorId(long supportedVendorId);

    /**
     * Sets the set of Supported-Vendor-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getSupportedVendorIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setSupportedVendorId or setSupportedVendorIds
     *  has already been called
     */
    void setSupportedVendorIds(long[] supportedVendorIds);

    /**
     * Returns the set of Auth-Application-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Auth-Application-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    long[] getAuthApplicationIds();

    /**
     * Sets a single Auth-Application-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setAuthApplicationId or setAuthApplicationIds
     *  has already been called
     */
    void setAuthApplicationId(long authApplicationId);

    /**
     * Sets the set of Auth-Application-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getAuthApplicationIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setAuthApplicationId or setAuthApplicationIds
     *  has already been called
     */
    void setAuthApplicationIds(long[] authApplicationIds);

    /**
     * Returns the set of Inband-Security-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Inband-Security-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    long[] getInbandSecurityIds();

    /**
     * Sets a single Inband-Security-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setInbandSecurityId or setInbandSecurityIds
     *  has already been called
     */
    void setInbandSecurityId(long inbandSecurityId);

    /**
     * Sets the set of Inband-Security-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getInbandSecurityIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setInbandSecurityId or setInbandSecurityIds
     *  has already been called
     */
    void setInbandSecurityIds(long[] inbandSecurityIds);

    /**
     * Returns the set of Acct-Application-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Acct-Application-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    long[] getAcctApplicationIds();

    /**
     * Sets a single Acct-Application-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setAcctApplicationId or setAcctApplicationIds
     *  has already been called
     */
    void setAcctApplicationId(long acctApplicationId);

    /**
     * Sets the set of Acct-Application-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getAcctApplicationIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setAcctApplicationId or setAcctApplicationIds
     *  has already been called
     */
    void setAcctApplicationIds(long[] acctApplicationIds);

    /**
     * Returns the set of Vendor-Specific-Application-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Vendor-Specific-Application-Id AVPs have been set.
     * The elements in the given array are VendorSpecificApplicationId objects.
     */
    VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds();

    /**
     * Sets a single Vendor-Specific-Application-Id AVP in the message, of type Grouped.
     * @throws IllegalStateException if setVendorSpecificApplicationId or setVendorSpecificApplicationIds
     *  has already been called
     */
    void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId);

    /**
     * Sets the set of Vendor-Specific-Application-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getVendorSpecificApplicationIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setVendorSpecificApplicationId or setVendorSpecificApplicationIds
     *  has already been called
     */
    void setVendorSpecificApplicationIds(VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds);

    /**
     * Returns true if the Firmware-Revision AVP is present in the message.
     */
    boolean hasFirmwareRevision();

    /**
     * Returns the value of the Firmware-Revision AVP, of type Unsigned32.
     * Use {@link #hasFirmwareRevision()} to check the existence of this AVP.  
     * @return the value of the Firmware-Revision AVP
     * @throws IllegalStateException if the Firmware-Revision AVP has not been set on this message
     */
    long getFirmwareRevision();

    /**
     * Sets the value of the Firmware-Revision AVP, of type Unsigned32.
     * @throws IllegalStateException if setFirmwareRevision has already been called
     */
    void setFirmwareRevision(long firmwareRevision);


}
