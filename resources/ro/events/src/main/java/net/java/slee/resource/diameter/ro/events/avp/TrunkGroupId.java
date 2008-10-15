package net.java.slee.resource.diameter.ro.events.avp;
/**
 * Defines an interface representing the Trunk-Group-ID grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.114 Trunk-Group-ID AVP The Trunk-Group-ID AVP (AVP code 851) is of type Grouped and identifies the incoming and outgoing PSTN legs. It has the following ABNF grammar: Trunk-Group-ID ::= AVP Header: 851 [ Incoming-Trunk-Group-ID ] [ Outgoing-Trunk-Group-ID ]
 */
public interface TrunkGroupId extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
    /**
     * Returns the value of the Incoming-Trunk-Group-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getIncomingTrunkGroupId();

    /**
     * Returns the value of the Outgoing-Trunk-Group-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getOutgoingTrunkGroupId();

    /**
     * Returns true if the Incoming-Trunk-Group-ID AVP is present in the message.
     */
    abstract boolean hasIncomingTrunkGroupId();

    /**
     * Returns true if the Outgoing-Trunk-Group-ID AVP is present in the message.
     */
    abstract boolean hasOutgoingTrunkGroupId();

    /**
     * Sets the value of the Incoming-Trunk-Group-ID AVP, of type UTF8String.
     */
    abstract void setIncomingTrunkGroupId(java.lang.String incomingTrunkGroupId);

    /**
     * Sets the value of the Outgoing-Trunk-Group-ID AVP, of type UTF8String.
     */
    abstract void setOutgoingTrunkGroupId(java.lang.String outgoingTrunkGroupId);

}
