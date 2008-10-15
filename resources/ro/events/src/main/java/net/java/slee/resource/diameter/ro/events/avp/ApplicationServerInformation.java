package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Application-Server-Information grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.12 Application-Server-Information AVP The Application-Server-Information AVP (AVP code 850) is of type Grouped and contains information about application servers visited through ISC interface. It has the following ABNF grammar: Application-Server-Information ::= AVP Header: 850 [ Application-Server ] * [ Application-Provided-Called-Party-Address ]
 */
public interface ApplicationServerInformation extends GroupedAvp{
    /**
     * Returns the set of Application-Provided-Called-Party-Address AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no Application-Provided-Called-Party-Address AVPs have been set. The elements in the given array are String objects.
     */
    abstract java.lang.String[] getApplicationProvidedCalledPartyAddresses();

    /**
     * Returns the value of the Application-Server AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getApplicationServer();

    /**
     * Returns true if the Application-Server AVP is present in the message.
     */
    abstract boolean hasApplicationServer();

    /**
     * Sets a single Application-Provided-Called-Party-Address AVP in the message, of type UTF8String.
     */
    abstract void setApplicationProvidedCalledPartyAddress(java.lang.String applicationProvidedCalledPartyAddress);

    /**
     * Sets the set of Application-Provided-Called-Party-Address AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getApplicationProvidedCalledPartyAddresses() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
     */
    abstract void setApplicationProvidedCalledPartyAddresses(java.lang.String[] applicationProvidedCalledPartyAddresses);

    /**
     * Sets the value of the Application-Server AVP, of type UTF8String.
     */
    abstract void setApplicationServer(java.lang.String applicationServer);

}
