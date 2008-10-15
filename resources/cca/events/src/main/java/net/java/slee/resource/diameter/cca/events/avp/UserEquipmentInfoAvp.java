package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 *  &lt;b&gt;8.49. User-Equipment-Info AVP&lt;/b&gt;
 * 
 * 
 *   The User-Equipment-Info AVP (AVP Code 458) is of type Grouped and
 *   allows the credit-control client to indicate the identity and
 *   capability of the terminal the subscriber is using for the connection
 *   to network.
 * 
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 * 
 *      User-Equipment-Info ::= &lt; AVP Header: 458 &gt;
 *                              { User-Equipment-Info-Type }
 *                              { User-Equipment-Info-Value }
 *                              
 * </pre>
 * 
 * @author baranowb
 * 
 */
public interface UserEquipmentInfoAvp extends GroupedAvp {

	/**
	 * Sets the value of the User-Equipment-Info-Type AVP, of type Enumerated.
	 * <br>
	 * See: {@link UserEquipmentInfoType}
	 * 
	 * @param type
	 */
	public void setUserEquipmentInfoType(UserEquipmentInfoType type);

	/**
	 * Returns the value of the User-Equipment-Info-Type AVP, of type
	 * Enumerated. A return value of null implies that the AVP has not been set.
	 * <br>
	 * See: {@link UserEquipmentInfoType}
	 * 
	 * @return
	 */
	public UserEquipmentInfoType getUserEquipmentInfoType();

	/**
	 * Returns true if the User-Equipment-Info-Type AVP is present in the
	 * message. <br>
	 * See: {@link UserEquipmentInfoType}
	 * 
	 * @return
	 */
	public boolean hasUserEquipmentInfoType();

	/**
	 * Sets the value of the User-Equipment-Info-Value AVP, of type OctetString.
	 * 
	 * @param value
	 */
	public void setUserEquipmentInfoValue(byte[] value);

	/**
	 * Returns the value of the User-Equipment-Info-Value AVP, of type
	 * OctetString. A return value of null implies that the AVP has not been
	 * set.
	 * 
	 * @return
	 */
	public byte[] getUserEquipmentInfoValue();

	/**
	 * Returns true if the User-Equipment-Info-Value AVP is present in the
	 * message.
	 * 
	 * @return
	 */
	public boolean hasUserEquipmentInfoValue();

}
