/**
 * Start time:18:58:59 2009-05-22<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package net.java.slee.resource.diameter.base.events;

/**
 * Start time:18:58:59 2009-05-22<br>
 * Project: diameter-parent<br>
 * Super interface fir STX messages
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface SessionTerminationMessage extends DiameterMessage {

	/**
     * Returns true if the User-Name AVP is present in the message.
     */
    boolean hasUserName();

    /**
     * Returns the value of the User-Name AVP, of type UTF8String.
     * @return the value of the User-Name AVP or null if it has not been set on this message
     */
    String getUserName();

    /**
     * Sets the value of the User-Name AVP, of type UTF8String.
     * @throws IllegalStateException if setUserName has already been called
     */
    void setUserName(String userName);

	/**
	 * Returns the set of Class AVPs. The returned array contains the AVPs in
	 * the order they appear in the message. A return value of null implies that
	 * no Class AVPs have been set. The elements in the given array are byte[]
	 * objects.
	 */
	byte[][] getClassAvps();

	/**
	 * Sets a single Class AVP in the message, of type OctetString.
	 * 
	 * @throws IllegalStateException
	 *             if setClassAvp or setClassAvps has already been called
	 */
	void setClassAvp(byte[] classAvp);
	/**
	 * Gets a single Class AVP in the message, of type OctetString.
	 * 
	 * 
	 */
	byte[] getClassAvp();
	/**
	 * Sets the set of Class AVPs, with all the values in the given array. The
	 * AVPs will be added to message in the order in which they appear in the
	 * array.
	 * 
	 * Note: the array must not be altered by the caller following this call,
	 * and getClassAvps() is not guaranteed to return the same array instance,
	 * e.g. an "==" check would fail.
	 * 
	 * @throws IllegalStateException
	 *             if setClassAvp or setClassAvps has already been called
	 */
	void setClassAvps(byte[][] classAvps);
	/**
	 * Returns true if Class AVP is present.
	 * @return
	 */
	boolean hasClassAvp();
	
}
