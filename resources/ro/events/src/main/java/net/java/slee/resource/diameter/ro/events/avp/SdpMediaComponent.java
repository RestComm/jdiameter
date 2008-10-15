package net.java.slee.resource.diameter.ro.events.avp;
/**
 * Defines an interface representing the SDP-Media-Component grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.95 SDP-Media-Component AVP The SDP-Media-Component AVP (AVP code 843) is of type Grouped and contains information about media used for a IMS session. It has the following ABNF grammar: SDP-Media-Component ::= AVP Header: 843 [ SDP-Media-Name ] * [ SDP-Media-Description ] [ Media-Initiator-Flag] [ Authorized-QoS ] [ TGPP-Charging-Id ]
 */
public interface SdpMediaComponent extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
    /**
     * Returns the value of the Authorized-QoS AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getAuthorizedQos();

    /**
     * Returns the value of the Media-Initiator-Flag AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag getMediaInitiatorFlag();

    /**
     * Returns the set of SDP-Media-Description AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no SDP-Media-Description AVPs have been set. The elements in the given array are String objects.
     */
    abstract java.lang.String[] getSdpMediaDescriptions();

    /**
     * Returns the value of the SDP-Media-Name AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getSdpMediaName();

    /**
     * Returns the value of the TGPP-Charging-Id AVP, of type OctetString. A return value of null implies that the AVP has not been set.
     */
    abstract byte[] getTgppChargingId();

    /**
     * Returns true if the Authorized-QoS AVP is present in the message.
     */
    abstract boolean hasAuthorizedQos();

    /**
     * Returns true if the Media-Initiator-Flag AVP is present in the message.
     */
    abstract boolean hasMediaInitiatorFlag();

    /**
     * Returns true if the SDP-Media-Name AVP is present in the message.
     */
    abstract boolean hasSdpMediaName();

    /**
     * Returns true if the TGPP-Charging-Id AVP is present in the message.
     */
    abstract boolean hasTgppChargingId();

    /**
     * Sets the value of the Authorized-QoS AVP, of type UTF8String.
     */
    abstract void setAuthorizedQos(java.lang.String authorizedQos);

    /**
     * Sets the value of the Media-Initiator-Flag AVP, of type Enumerated.
     */
    abstract void setMediaInitiatorFlag(net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag mediaInitiatorFlag);

    /**
     * Sets a single SDP-Media-Description AVP in the message, of type UTF8String.
     */
    abstract void setSdpMediaDescription(java.lang.String sdpMediaDescription);

    /**
     * Sets the set of SDP-Media-Description AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getSdpMediaDescriptions() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
     */
    abstract void setSdpMediaDescriptions(java.lang.String[] sdpMediaDescriptions);

    /**
     * Sets the value of the SDP-Media-Name AVP, of type UTF8String.
     */
    abstract void setSdpMediaName(java.lang.String sdpMediaName);

    /**
     * Sets the value of the TGPP-Charging-Id AVP, of type OctetString.
     */
    abstract void setTgppChargingId(byte[] tgppChargingId);

}
