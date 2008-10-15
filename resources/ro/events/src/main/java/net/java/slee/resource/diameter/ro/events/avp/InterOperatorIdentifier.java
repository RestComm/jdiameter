package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Inter-Operator-Identifier grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.39 Inter-Operator-Identifier AVP The Inter-Operator-Identifier AVP (AVP code 838) is of type Grouped and holds the identification of the network neighbours (originating and terminating) as exchanged via SIP signalling and described in [404]. It has the following ABNF grammar: Inter-Operator-Identifier ::= AVP Header: 838 [ Originating-IOI ] [ Terminating-IOI ]
 */
public interface InterOperatorIdentifier extends GroupedAvp{
    /**
     * Returns the value of the Originating-IOI AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getOriginatingIoi();

    /**
     * Returns the value of the Terminating-IOI AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getTerminatingIoi();

    /**
     * Returns true if the Originating-IOI AVP is present in the message.
     */
    abstract boolean hasOriginatingIoi();

    /**
     * Returns true if the Terminating-IOI AVP is present in the message.
     */
    abstract boolean hasTerminatingIoi();

    /**
     * Sets the value of the Originating-IOI AVP, of type UTF8String.
     */
    abstract void setOriginatingIoi(java.lang.String originatingIoi);

    /**
     * Sets the value of the Terminating-IOI AVP, of type UTF8String.
     */
    abstract void setTerminatingIoi(java.lang.String terminatingIoi);

}
