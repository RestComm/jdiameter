package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 *<pre> <b>8.43. Service-Parameter-Info AVP</b>
 *
 *
 *   The Service-Parameter-Info AVP (AVP Code 440) is of type Grouped and
 *   contains service-specific information used for price calculation or
 *   rating.  The Service-Parameter-Type AVP defines the service parameter
 *   type, and the Service-Parameter-Value AVP contains the parameter
 *   value.  The actual contents of these AVPs are not within the scope of
 *   this document and SHOULD be defined in another Diameter application,
 *   in standards written by other standardization bodies, or in service-
 *   specific documentation.
 *
 *   In the case of an unknown service request (e.g., unknown Service-
 *   Parameter-Type), the corresponding answer message MUST contain the
 *   error code DIAMETER_RATING_FAILED.  A Credit-Control-Answer message
 *   with this error MUST contain one or more Failed-AVP AVPs containing
 *   the Service-Parameter-Info AVPs that caused the failure.
 *
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 *
 *      Service-Parameter-Info ::= < AVP Header: 440 >
 *                                 { Service-Parameter-Type }
 *                                 { Service-Parameter-Value }
 *                                 </pre>
 * @author baranowb
 *
 */
public interface ServiceParameterInfoAvp extends GroupedAvp {

	/**
	 * Returns the value of the Service-Parameter-Type AVP, of type Unsigned32.
	 * 
	 * @return
	 */
	long getServiceParameterType();

	/**
	 * Returns the value of the Service-Parameter-Value AVP, of type
	 * OctetString.
	 * 
	 * @return
	 */
	byte[] getServiceParameterValue();

	/**
	 * Returns true if the Service-Parameter-Type AVP is present in the message.
	 * 
	 * @return
	 */
	boolean hasServiceParameterType();

	/**
	 * Returns true if the Service-Parameter-Value AVP is present in the
	 * message.
	 * 
	 * @return
	 */
	boolean hasServiceParameterValue();

	/**
	 * Sets the value of the Service-Parameter-Type AVP, of type Unsigned32.
	 * 
	 * @param serviceParameterType
	 */
	void setServiceParameterType(long serviceParameterType);

	/**
	 * Sets the value of the Service-Parameter-Value AVP, of type OctetString.
	 * 
	 * @param serviceParameterValue
	 */
	void setServiceParameterValue(byte[] serviceParameterValue);

}
