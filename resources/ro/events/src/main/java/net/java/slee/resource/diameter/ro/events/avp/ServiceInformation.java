package net.java.slee.resource.diameter.ro.events.avp;
/**
 * Defines an interface representing the Service-Information grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.101 Service-Information AVP The Service-Information AVP (AVP code 873) is of type Grouped. Its purpose is to allow the transmission of additional 3GPP service specific information elements which are not described in this document. It has the following ABNF grammar: Service-Information ::= AVP Header: 873 [ PS-Information ] [ WLAN-Information ] [ IMS-Information ] [ MMS-Information ] [ LCS-Information ] [ PoC-Information ] [ MBMS-Information ] The format and the contents of the fields inside the Service-Information AVP are specified in the middle-tier documents which are applicable for the specific service. Note that the formats of the fields are service-specific, i.e. the format will be different for the various services. Further fields may be included in the Service-Information AVP when new services are introduced.
 */
public interface ServiceInformation extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
    /**
     * Returns the value of the IMS-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.ImsInformation getImsInformation();

    /**
     * Returns the value of the LCS-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LcsInformation getLcsInformation();

    /**
     * Returns the value of the MBMS-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.MbmsInformation getMbmsInformation();

    /**
     * Returns the value of the MMS-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.MmsInformation getMmsInformation();

    /**
     * Returns the value of the PoC-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.PocInformation getPocInformation();

    /**
     * Returns the value of the PS-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.PsInformation getPsInformation();

    /**
     * Returns the value of the WLAN-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.WlanInformation getWlanInformation();

    /**
     * Returns true if the IMS-Information AVP is present in the message.
     */
    abstract boolean hasImsInformation();

    /**
     * Returns true if the LCS-Information AVP is present in the message.
     */
    abstract boolean hasLcsInformation();

    /**
     * Returns true if the MBMS-Information AVP is present in the message.
     */
    abstract boolean hasMbmsInformation();

    /**
     * Returns true if the MMS-Information AVP is present in the message.
     */
    abstract boolean hasMmsInformation();

    /**
     * Returns true if the PoC-Information AVP is present in the message.
     */
    abstract boolean hasPocInformation();

    /**
     * Returns true if the PS-Information AVP is present in the message.
     */
    abstract boolean hasPsInformation();

    /**
     * Returns true if the WLAN-Information AVP is present in the message.
     */
    abstract boolean hasWlanInformation();

    /**
     * Sets the value of the IMS-Information AVP, of type Grouped.
     */
    abstract void setImsInformation(net.java.slee.resource.diameter.ro.events.avp.ImsInformation imsInformation);

    /**
     * Sets the value of the LCS-Information AVP, of type Grouped.
     */
    abstract void setLcsInformation(net.java.slee.resource.diameter.ro.events.avp.LcsInformation lcsInformation);

    /**
     * Sets the value of the MBMS-Information AVP, of type Grouped.
     */
    abstract void setMbmsInformation(net.java.slee.resource.diameter.ro.events.avp.MbmsInformation mbmsInformation);

    /**
     * Sets the value of the MMS-Information AVP, of type Grouped.
     */
    abstract void setMmsInformation(net.java.slee.resource.diameter.ro.events.avp.MmsInformation mmsInformation);

    /**
     * Sets the value of the PoC-Information AVP, of type Grouped.
     */
    abstract void setPocInformation(net.java.slee.resource.diameter.ro.events.avp.PocInformation pocInformation);

    /**
     * Sets the value of the PS-Information AVP, of type Grouped.
     */
    abstract void setPsInformation(net.java.slee.resource.diameter.ro.events.avp.PsInformation psInformation);

    /**
     * Sets the value of the WLAN-Information AVP, of type Grouped.
     */
    abstract void setWlanInformation(net.java.slee.resource.diameter.ro.events.avp.WlanInformation wlanInformation);

}
