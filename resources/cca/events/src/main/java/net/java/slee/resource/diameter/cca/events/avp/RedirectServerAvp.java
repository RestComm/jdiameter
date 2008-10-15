package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;


/**
 *<pre> <b>8.37. Redirect-Server AVP</b>
 *
 *
 *   The Redirect-Server AVP (AVP Code 434) is of type Grouped and
 *   contains the address information of the redirect server (e.g., HTTP
 *   redirect server, SIP Server) with which the end user is to be
 *   connected when the account cannot cover the service cost.  It MUST be
 *   present when the Final-Unit-Action AVP is set to REDIRECT.
 *
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 *
 *      Redirect-Server ::= < AVP Header: 434 >
 *                          { Redirect-Address-Type }
 *                          { Redirect-Server-Address }
 *	</pre>
 * @author baranowb
 *
 */
public interface RedirectServerAvp extends GroupedAvp {

	/**
	 * Returns the value of the Redirect-Address-Type AVP, of type Enumerated.
	 * <br>See: {@link RedirectAddressType}
	 * @return
	 */
	RedirectAddressType getRedirectAddressType();

	/**
	 * Returns the value of the Redirect-Server-Address AVP, of type UTF8String. If return value is null it implies that value has not been set.
	 * 
	 * @return
	 */
	java.lang.String getRedirectServerAddress();

	/**
	 * Returns true if the Redirect-Address-Type AVP is present in the message.
	 * 
	 * @return
	 */
	boolean hasRedirectAddressType();

	/**
	 * Returns true if the Redirect-Server-Address AVP is present in the
	 * message.
	 * 
	 * @return
	 */
	boolean hasRedirectServerAddress();

	/**
	 * Sets the value of the Redirect-Address-Type AVP, of type Enumerated.
	 * <br>See: {@link RedirectAddressType}
	 * @param redirectAddressType
	 */
	void setRedirectAddressType(RedirectAddressType redirectAddressType);

	/**
	 * Sets the value of the Redirect-Server-Address AVP, of type UTF8String.
	 * 
	 * @param redirectServerAddress
	 */
	void setRedirectServerAddress(java.lang.String redirectServerAddress);

}
