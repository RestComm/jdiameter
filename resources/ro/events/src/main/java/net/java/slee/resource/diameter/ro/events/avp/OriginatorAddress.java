package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Originator-Address grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.70 Originator-Address AVP The Originator-Address AVP (AVP code 886) is of type Grouped. Its purpose is to identify the originator of a MM. It has the following ABNF grammar: Originator-Address ::= AVP Header: 886 [ Address-Type ] [ Address-Data ] [ Address-Domain ]
 */
public interface OriginatorAddress extends GroupedAvp{
    /**
     * Returns the value of the Address-Data AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getAddressData();

    /**
     * Returns the value of the Address-Domain AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.AddressDomain getAddressDomain();

    /**
     * Returns the value of the Address-Type AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.AddressType getAddressType();

    /**
     * Returns true if the Address-Data AVP is present in the message.
     */
    abstract boolean hasAddressData();

    /**
     * Returns true if the Address-Domain AVP is present in the message.
     */
    abstract boolean hasAddressDomain();

    /**
     * Returns true if the Address-Type AVP is present in the message.
     */
    abstract boolean hasAddressType();

    /**
     * Sets the value of the Address-Data AVP, of type UTF8String.
     */
    abstract void setAddressData(java.lang.String addressData);

    /**
     * Sets the value of the Address-Domain AVP, of type Grouped.
     */
    abstract void setAddressDomain(net.java.slee.resource.diameter.ro.events.avp.AddressDomain addressDomain);

    /**
     * Sets the value of the Address-Type AVP, of type Enumerated.
     */
    abstract void setAddressType(net.java.slee.resource.diameter.ro.events.avp.AddressType addressType);

}
