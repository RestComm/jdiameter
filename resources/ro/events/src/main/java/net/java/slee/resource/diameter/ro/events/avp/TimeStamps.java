package net.java.slee.resource.diameter.ro.events.avp;
/**
 * Defines an interface representing the Time-Stamps grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.111 Time-Stamps AVP The Time-Stamps AVP (AVP code 833) is of type Grouped and holds the time of the initial SIP request and the time of the response to the initial SIP Request. It has the following ABNF grammar: Time-Stamps ::= AVP Header: 833 [ SIP-Request-Timestamp ] [ SIP-Response-Timestamp ]
 */
public interface TimeStamps extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
    /**
     * Returns the value of the SIP-Request-Timestamp AVP, of type Time. A return value of null implies that the AVP has not been set.
     */
    abstract java.util.Date getSipRequestTimestamp();

    /**
     * Returns the value of the SIP-Response-Timestamp AVP, of type Time. A return value of null implies that the AVP has not been set.
     */
    abstract java.util.Date getSipResponseTimestamp();

    /**
     * Returns true if the SIP-Request-Timestamp AVP is present in the message.
     */
    abstract boolean hasSipRequestTimestamp();

    /**
     * Returns true if the SIP-Response-Timestamp AVP is present in the message.
     */
    abstract boolean hasSipResponseTimestamp();

    /**
     * Sets the value of the SIP-Request-Timestamp AVP, of type Time.
     */
    abstract void setSipRequestTimestamp(java.util.Date sipRequestTimestamp);

    /**
     * Sets the value of the SIP-Response-Timestamp AVP, of type Time.
     */
    abstract void setSipResponseTimestamp(java.util.Date sipResponseTimestamp);

}
