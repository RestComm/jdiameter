package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the Location-Type grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.54 Location-Type AVP The Location-Type AVP (AVP code 1244) is of type Grouped and indicates the type of location estimate required by the LCS client. It has the following ABNF grammar: Location-Type::= AVP Header: 1244 [ Location-Estimate-Type ] [ Deferred-Location-Event-Type ]
 */
public interface LocationType extends GroupedAvp{
    /**
     * Returns the value of the Deferred-Location-Event-Type AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getDeferredLocationEventType();

    /**
     * Returns the value of the Location-Estimate-Type AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.LocationEstimateType getLocationEstimateType();

    /**
     * Returns true if the Deferred-Location-Event-Type AVP is present in the message.
     */
    abstract boolean hasDeferredLocationEventType();

    /**
     * Returns true if the Location-Estimate-Type AVP is present in the message.
     */
    abstract boolean hasLocationEstimateType();

    /**
     * Sets the value of the Deferred-Location-Event-Type AVP, of type UTF8String.
     */
    abstract void setDeferredLocationEventType(java.lang.String deferredLocationEventType);

    /**
     * Sets the value of the Location-Estimate-Type AVP, of type Enumerated.
     */
    abstract void setLocationEstimateType(net.java.slee.resource.diameter.ro.events.avp.LocationEstimateType locationEstimateType);

}
