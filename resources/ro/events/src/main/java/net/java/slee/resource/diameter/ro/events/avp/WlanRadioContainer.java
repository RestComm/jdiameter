package net.java.slee.resource.diameter.ro.events.avp;
/**
 * Defines an interface representing the WLAN-Radio-Container grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.122 WLAN-Radio-Container AVP The WLAN-Radio-Container AVP (AVP code 892) is of type Grouped. The WLAN- Radio-Container AVP has the following format: The Operator-Name, Location-Type and Location-Information AVPs are defined in TS 29.234 [212]. WLAN-Radio-Container ::= AVP Header: 892 [ Operator-Name ] #exclude [ Location-Type ] [ Location-Information ] #exclude [ WLAN-Technology ]
 */
public interface WlanRadioContainer extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
    /**
     * Returns the value of the Location-Type AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LocationType getLocationType();

    /**
     * Returns the value of the WLAN-Technology AVP, of type Unsigned32. A return value of null implies that the AVP has not been set.
     */
    abstract long getWlanTechnology();

    /**
     * Returns true if the Location-Type AVP is present in the message.
     */
    abstract boolean hasLocationType();

    /**
     * Returns true if the WLAN-Technology AVP is present in the message.
     */
    abstract boolean hasWlanTechnology();

    /**
     * Sets the value of the Location-Type AVP, of type Grouped.
     */
    abstract void setLocationType(net.java.slee.resource.diameter.ro.events.avp.LocationType locationType);

    /**
     * Sets the value of the WLAN-Technology AVP, of type Unsigned32.
     */
    abstract void setWlanTechnology(long wlanTechnology);

}
