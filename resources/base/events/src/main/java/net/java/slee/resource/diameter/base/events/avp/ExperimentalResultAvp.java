package net.java.slee.resource.diameter.base.events.avp;



/**
 * Defines an interface representing the Experimental-Result grouped AVP type.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 7.6.  Experimental-Result AVP
 * 
 *    The Experimental-Result AVP (AVP Code 297) is of type Grouped, and
 *    indicates whether a particular vendor-specific request was completed
 *    successfully or whether an error occurred.  Its Data field has the
 *    following ABNF grammar:
 * 
 *    AVP Format
 * 
 *       Experimental-Result ::= &lt; AVP Header: 297 &gt;
 *                                  { Vendor-Id }
 *                                  { Experimental-Result-Code }
 *    The Vendor-Id AVP (see Section 5.3.3) in this grouped AVP identifies
 *    the vendor responsible for the assignment of the result code which
 *    follows.  All Diameter answer messages defined in vendor-specific
 *    applications MUST include either one Result-Code AVP or one
 *    Experimental-Result AVP.
 * 
 * </pre>
 */
public interface ExperimentalResultAvp extends GroupedAvp {

    /**
     * Returns true if the Vendor-Id AVP is present in the message.
     */
    public boolean hasVendorId();

    /**
     * Sets the value of the Vendor-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setVendorId has already been called
     */
    public void setVendorId(long vendorId);

    /**
     * Returns true if the Experimental-Result-Code AVP is present in the message.
     */
    public boolean hasExperimentalResultCode();

    /**
     * Returns the value of the Experimental-Result-Code AVP, of type Unsigned32.
     * A return value of -1 implies that the AVP has not been set or some error has been encountered.
     */
    public long getExperimentalResultCode();

    /**
     * Sets the value of the Experimental-Result-Code AVP, of type Unsigned32.
     * @throws IllegalStateException if setExperimentalResultCode has already been called
     */
    public void setExperimentalResultCode(long experimentalResultCode);

}
