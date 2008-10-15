package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Message-Body grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.58 Message-Body AVP The Message-Body AVP (AVP Code 889) is of type Grouped AVP and holds information about the message bodies including user-to-user data. It has the following ABNF grammar: Message-Body ::= AVP Header: 889 [ Content-Type ] [ Content-Length ] [ Content-Disposition ] [ Originator ]
 */
public interface MessageBody extends GroupedAvp{
    /**
     * Returns the value of the Content-Disposition AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getContentDisposition();

    /**
     * Returns the value of the Content-Length AVP, of type Unsigned32. A return value of null implies that the AVP has not been set.
     */
    abstract long getContentLength();

    /**
     * Returns the value of the Content-Type AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getContentType();

    /**
     * Returns the value of the Originator AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.Originator getOriginator();

    /**
     * Returns true if the Content-Disposition AVP is present in the message.
     */
    abstract boolean hasContentDisposition();

    /**
     * Returns true if the Content-Length AVP is present in the message.
     */
    abstract boolean hasContentLength();

    /**
     * Returns true if the Content-Type AVP is present in the message.
     */
    abstract boolean hasContentType();

    /**
     * Returns true if the Originator AVP is present in the message.
     */
    abstract boolean hasOriginator();

    /**
     * Sets the value of the Content-Disposition AVP, of type UTF8String.
     */
    abstract void setContentDisposition(java.lang.String contentDisposition);

    /**
     * Sets the value of the Content-Length AVP, of type Unsigned32.
     */
    abstract void setContentLength(long contentLength);

    /**
     * Sets the value of the Content-Type AVP, of type UTF8String.
     */
    abstract void setContentType(java.lang.String contentType);

    /**
     * Sets the value of the Originator AVP, of type Enumerated.
     */
    abstract void setOriginator(net.java.slee.resource.diameter.ro.events.avp.Originator originator);

}
