package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
/**
 * <pre><b>8.22. CC-Money AVP</b>
 *
 *
 *   The CC-Money AVP (AVP Code 413) is of type Grouped and specifies the
 *   monetary amount in the given currency.  The Currency-Code AVP SHOULD
 *   be included.  It is defined as follows (per the grouped-avp-def of
 *   RFC 3588 [DIAMBASE]):
 *
 *      CC-Money ::= < AVP Header: 413 >
 *                   { Unit-Value }
 *                   [ Currency-Code ]
 *   <pre>
 * @author baranowb
 *
 */
public interface CcMoneyAvp extends GroupedAvp {

	/**
	 * Sets value of Init-Value avp of type Grouped AVP.
	 * See: {@link UnitValueAvp} .
	 * @param unitValue
	 */
	public void setUnitValue(UnitValueAvp unitValue);

	/**
	 * Return value of Unit-Value avp. Return value of null indicates its not present.
	 * See: {@link UnitValueAvp} .
	 * @return
	 */
	public UnitValueAvp getUnitValue();

	/**
	 * Returns true if Unit-Value avp is present
	 * @return
	 */
	public boolean hasUnitValue();

	/**
	 * Returns Currency-Code avp (Unsigned32) value in use, if present value is greater than 0.
	 * @return
	 */
	public long getCurrencyCode();

	/**
	 * Sets Currency-Code avp of type Unsigned32
	 * @param code
	 */
	public void setCurrencyCode(long code);

	/**
	 * Returns true if Currency-Code avp is present.
	 * @return
	 */
	public boolean hasCurrencyCode();

}
