package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Additional-Content-Information grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.2 Additional-Content-Information AVP The Additional-Content-Information AVP (AVP code 1207) is of type Grouped and identifies any subsequent content types. It is used to identify each content (including re-occurences) within an MM when the Type-Number AVP or Additional-Type-Information AVP from the Content-Type AVP indicate a multi- part content. It has the following ABNF grammar: Additional-Content-Information::= AVP Header: 1207 [ Type-Number ] [ Additional-Type-Information ] [ Content-Size ]
 */
public interface AdditionalContentInformation extends GroupedAvp{
    /**
     * Returns the value of the Additional-Type-Information AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getAdditionalTypeInformation();

    /**
     * Returns the value of the Content-Size AVP, of type Unsigned32. A return value of null implies that the AVP has not been set.
     */
    abstract long getContentSize();

    /**
     * Returns the value of the Type-Number AVP, of type Integer32. A return value of null implies that the AVP has not been set.
     */
    abstract int getTypeNumber();

    /**
     * Returns true if the Additional-Type-Information AVP is present in the message.
     */
    abstract boolean hasAdditionalTypeInformation();

    /**
     * Returns true if the Content-Size AVP is present in the message.
     */
    abstract boolean hasContentSize();

    /**
     * Returns true if the Type-Number AVP is present in the message.
     */
    abstract boolean hasTypeNumber();

    /**
     * Sets the value of the Additional-Type-Information AVP, of type UTF8String.
     */
    abstract void setAdditionalTypeInformation(java.lang.String additionalTypeInformation);

    /**
     * Sets the value of the Content-Size AVP, of type Unsigned32.
     */
    abstract void setContentSize(long contentSize);

    /**
     * Sets the value of the Type-Number AVP, of type Integer32.
     */
    abstract void setTypeNumber(int typeNumber);

}
