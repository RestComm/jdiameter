package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the LCS-Client-Name grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.44 LCS-Client-Name AVP The LCS-Client-Name AVP (AVP code 1235) is of type Grouped and contains the information related to the name of the LCS Client. It has the following ABNF grammar: LCS-Client-Name ::= AVP Header: 1235 [ LCS-Data-Coding-Scheme ] [ LCS-Name-String ] [ LCS-Format-Indicator ]
 */
public interface LcsClientName extends GroupedAvp{
    /**
     * Returns the value of the LCS-Data-Coding-Scheme AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getLcsDataCodingScheme();

    /**
     * Returns the value of the LCS-Format-Indicator AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator getLcsFormatIndicator();

    /**
     * Returns the value of the LCS-Name-String AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getLcsNameString();

    /**
     * Returns true if the LCS-Data-Coding-Scheme AVP is present in the message.
     */
    abstract boolean hasLcsDataCodingScheme();

    /**
     * Returns true if the LCS-Format-Indicator AVP is present in the message.
     */
    abstract boolean hasLcsFormatIndicator();

    /**
     * Returns true if the LCS-Name-String AVP is present in the message.
     */
    abstract boolean hasLcsNameString();

    /**
     * Sets the value of the LCS-Data-Coding-Scheme AVP, of type UTF8String.
     */
    abstract void setLcsDataCodingScheme(java.lang.String lcsDataCodingScheme);

    /**
     * Sets the value of the LCS-Format-Indicator AVP, of type Enumerated.
     */
    abstract void setLcsFormatIndicator(net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator lcsFormatIndicator);

    /**
     * Sets the value of the LCS-Name-String AVP, of type UTF8String.
     */
    abstract void setLcsNameString(java.lang.String lcsNameString);

}
