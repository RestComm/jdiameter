package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Message-Class grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.59 Message-Class AVP The Message-Class AVP (AVP code 1213) is of type Grouped. It has the following ABNF grammar: Message-Class ::= AVP Header: 1213 [ Class-Identifier ] [ Token-Text ]
 */
public interface MessageClass extends GroupedAvp{
    /**
     * Returns the value of the Class-Identifier AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier getClassIdentifier();

    /**
     * Returns the value of the Token-Text AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getTokenText();

    /**
     * Returns true if the Class-Identifier AVP is present in the message.
     */
    abstract boolean hasClassIdentifier();

    /**
     * Returns true if the Token-Text AVP is present in the message.
     */
    abstract boolean hasTokenText();

    /**
     * Sets the value of the Class-Identifier AVP, of type Enumerated.
     */
    abstract void setClassIdentifier(net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier classIdentifier);

    /**
     * Sets the value of the Token-Text AVP, of type UTF8String.
     */
    abstract void setTokenText(java.lang.String tokenText);

}
