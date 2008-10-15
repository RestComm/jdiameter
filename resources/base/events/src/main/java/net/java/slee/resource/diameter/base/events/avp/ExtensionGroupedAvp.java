package net.java.slee.resource.diameter.base.events.avp;




/**
 * An extension of the GroupedAvp interface to represent extension AVPs--those not defined
 * by the Diameter RA being used.
 */
public interface ExtensionGroupedAvp extends GroupedAvp {
	//TODO: Remove this - overlaping interface?
    /**
     * Returns the set of extension AVPs. The returned array contains the extension AVPs
     * in the order they appear in the message.
     * A return value of null implies that no extensions AVPs have been set.
     */
    public DiameterAvp[] getExtensionAvps();

    /**
     * Sets the set of extension AVPs with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getExtensionAvps() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws AvpNotAllowedException if an AVP is encountered of a type already known to this class
     *   (i.e. an AVP for which get/set methods already appear in this class)
     * @throws IllegalStateException if setExtensionAvps has already been called
     */
    public void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException;

}
