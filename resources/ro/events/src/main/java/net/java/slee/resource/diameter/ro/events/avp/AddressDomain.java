package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Address-Domain grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.5 Address-Domain AVP The Address-Domain AVP (AVP code 898) is of type Grouped and indicates the domain/network to which the associated address resides. If this AVP is present, at least one of the AVPs described within the grouping must be included. It has the following ABNF: Address-Domain ::= AVP Header: 898 [ Domain-Name ] [ TGPP-IMSI-MCC-MNC ]
 */
public interface AddressDomain extends GroupedAvp{
    /**
     * Returns the value of the Domain-Name AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getDomainName();

    /**
     * Returns the value of the TGPP-IMSI-MCC-MNC AVP, of type OctetString. A return value of null implies that the AVP has not been set.
     */
    abstract byte[] getTgppImsiMccMnc();

    /**
     * Returns true if the Domain-Name AVP is present in the message.
     */
    abstract boolean hasDomainName();

    /**
     * Returns true if the TGPP-IMSI-MCC-MNC AVP is present in the message.
     */
    abstract boolean hasTgppImsiMccMnc();

    /**
     * Sets the value of the Domain-Name AVP, of type UTF8String.
     */
    abstract void setDomainName(java.lang.String domainName);

    /**
     * Sets the value of the TGPP-IMSI-MCC-MNC AVP, of type OctetString.
     */
    abstract void setTgppImsiMccMnc(byte[] tgppImsiMccMnc);

}
