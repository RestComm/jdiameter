package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the LCS-Client-ID grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.43 LCS-Client-ID AVP The LCS-Client-Id AVP (AVP code 1232) is of type Grouped and holds information related to the identity of an LCS client. It has the following ABNF grammar: LCS-Client-ID ::= AVP Header: 1232 [ LCS-Client-Type ] [ LCS-Client-External-ID ] [ LCS-Client-Dialed-By-MS ] [ LCS-Client-Name ] [ LCS-APN ] [ LCS-Requestor-ID ]
 */
public interface LcsClientId extends GroupedAvp{
    /**
     * Returns the value of the LCS-APN AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getLcsApn();

    /**
     * Returns the value of the LCS-Client-Dialed-By-MS AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getLcsClientDialedByMs();

    /**
     * Returns the value of the LCS-Client-External-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getLcsClientExternalId();

    /**
     * Returns the value of the LCS-Client-Name AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LcsClientName getLcsClientName();

    /**
     * Returns the value of the LCS-Client-Type AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LcsClientType getLcsClientType();

    /**
     * Returns the value of the LCS-Requestor-ID AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId getLcsRequestorId();

    /**
     * Returns true if the LCS-APN AVP is present in the message.
     */
    abstract boolean hasLcsApn();

    /**
     * Returns true if the LCS-Client-Dialed-By-MS AVP is present in the message.
     */
    abstract boolean hasLcsClientDialedByMs();

    /**
     * Returns true if the LCS-Client-External-ID AVP is present in the message.
     */
    abstract boolean hasLcsClientExternalId();

    /**
     * Returns true if the LCS-Client-Name AVP is present in the message.
     */
    abstract boolean hasLcsClientName();

    /**
     * Returns true if the LCS-Client-Type AVP is present in the message.
     */
    abstract boolean hasLcsClientType();

    /**
     * Returns true if the LCS-Requestor-ID AVP is present in the message.
     */
    abstract boolean hasLcsRequestorId();

    /**
     * Sets the value of the LCS-APN AVP, of type UTF8String.
     */
    abstract void setLcsApn(java.lang.String lcsApn);

    /**
     * Sets the value of the LCS-Client-Dialed-By-MS AVP, of type UTF8String.
     */
    abstract void setLcsClientDialedByMs(java.lang.String lcsClientDialedByMs);

    /**
     * Sets the value of the LCS-Client-External-ID AVP, of type UTF8String.
     */
    abstract void setLcsClientExternalId(java.lang.String lcsClientExternalId);

    /**
     * Sets the value of the LCS-Client-Name AVP, of type Grouped.
     */
    abstract void setLcsClientName(net.java.slee.resource.diameter.ro.events.avp.LcsClientName lcsClientName);

    /**
     * Sets the value of the LCS-Client-Type AVP, of type Enumerated.
     */
    abstract void setLcsClientType(net.java.slee.resource.diameter.ro.events.avp.LcsClientType lcsClientType);

    /**
     * Sets the value of the LCS-Requestor-ID AVP, of type Grouped.
     */
    abstract void setLcsRequestorId(net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId lcsRequestorId);

}
