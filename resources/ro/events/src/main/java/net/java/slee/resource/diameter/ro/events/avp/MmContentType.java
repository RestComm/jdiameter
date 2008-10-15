package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the MM-Content-Type grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.63 MM-Content-Type AVP The MM-Content-Type AVP (AVP code 1203) is of type Grouped and indicates the overall content type of the MM content and includes information about all the contents of an MM. It has the following ABNF grammar: MM-Content-Type ::= AVP Header: 1203 [ Type-Number ] [ Additional-Type-Information ] [ Content-Size ] * [ Additional-Content-Information ]
 */
public interface MmContentType extends GroupedAvp{
    /**
     * Returns the set of Additional-Content-Information AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no Additional-Content-Information AVPs have been set. The elements in the given array are AdditionalContentInformation objects.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation[] getAdditionalContentInformations();

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
     * Sets a single Additional-Content-Information AVP in the message, of type Grouped.
     */
    abstract void setAdditionalContentInformation(net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation additionalContentInformation);

    /**
     * Sets the set of Additional-Content-Information AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getAdditionalContentInformations() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
     */
    abstract void setAdditionalContentInformations(net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation[] additionalContentInformations);

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
