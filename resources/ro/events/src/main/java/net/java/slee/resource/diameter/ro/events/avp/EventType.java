package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Event-Type grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.32 Event-Type AVP The Event-Type AVP (AVP code 823) is of type Grouped and contains information about the type of chargeable telecommunication service/event for which the accounting-request message is generated. It has the following ABNF grammar: Event-Type ::= AVP Header: 823 [ SIP-Method ] [ Event ] [ Expires ]
 */
public interface EventType extends GroupedAvp{
    /**
     * Returns the value of the Event AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getEvent();

    /**
     * Returns the value of the Expires AVP, of type Unsigned32. A return value of null implies that the AVP has not been set.
     */
    abstract long getExpires();

    /**
     * Returns the value of the SIP-Method AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getSipMethod();

    /**
     * Returns true if the Event AVP is present in the message.
     */
    abstract boolean hasEvent();

    /**
     * Returns true if the Expires AVP is present in the message.
     */
    abstract boolean hasExpires();

    /**
     * Returns true if the SIP-Method AVP is present in the message.
     */
    abstract boolean hasSipMethod();

    /**
     * Sets the value of the Event AVP, of type UTF8String.
     */
    abstract void setEvent(java.lang.String event);

    /**
     * Sets the value of the Expires AVP, of type Unsigned32.
     */
    abstract void setExpires(long expires);

    /**
     * Sets the value of the SIP-Method AVP, of type UTF8String.
     */
    abstract void setSipMethod(java.lang.String sipMethod);

}
