package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 *  &lt;b&gt;8.46. Subscription-Id AVP&lt;/b&gt;
 * 
 * 
 *   The Subscription-Id AVP (AVP Code 443) is used to identify the end
 *   user's subscription and is of type Grouped.  The Subscription-Id AVP
 *   includes a Subscription-Id-Data AVP that holds the identifier and a
 *   Subscription-Id-Type AVP that defines the identifier type.
 * 
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 * 
 *      Subscription-Id ::= &lt; AVP Header: 443 &gt;
 *                          { Subscription-Id-Type }
 *                          { Subscription-Id-Data }
 * </pre>
 * 
 * @author baranowb
 * 
 */
public interface SubscriptionIdAvp extends GroupedAvp {

	/**
	 * Sets the value of the Subscription-Id-Type AVP, of type Enumerated. <br>
	 * See:{@link SubscriptionIdType}.
	 * 
	 * @param type
	 */
	public void setSubscriptionIdType(SubscriptionIdType type);

	/**
	 * Returns the value of the Subscription-Id-Type AVP, of type Enumerated. A
	 * return value of null implies that the AVP has not been set.
	 * 
	 * @return
	 */
	public SubscriptionIdType getSubscriptionIdType();

	public boolean hasSubscriptionIdType();

	/**
	 * Sets the value of the Subscription-Id-Data AVP, of type UTF8String.
	 * 
	 * @param data
	 */
	public void setSubscriptionData(String data);

	/**
	 * Returns the value of the Subscription-Id-Data AVP, of type UTF8String. A
	 * return value of null implies that the AVP has not been set.
	 * 
	 * @return
	 */
	public String getSubscriptionData();

	/**
	 * Returns true if the Subscription-Id-Data AVP is present in the message.
	 * 
	 * @return
	 */
	public boolean hasSubscriptionData();
}
