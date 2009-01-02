package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 *<pre> <b>8.8. Unit-Value AVP</b>
 *
 *
 *   Unit-Value AVP is of type Grouped (AVP Code 445) and specifies the
 *   units as decimal value.  The Unit-Value is a value with an exponent;
 *   i.e., Unit-Value = Value-Digits AVP * 10^Exponent.  This
 *   representation avoids unwanted rounding off.  For example, the value
 *   of 2,3 is represented as Value-Digits = 23 and Exponent = -1.  The
 *   absence of the exponent part MUST be interpreted as an exponent equal
 *   to zero.
 *
 *   It is defined as follows (per the grouped-avp-def of
 *   RFC 3588 [DIAMBASE]):
 *
 *                    Unit-Value ::= < AVP Header: 445 >
 *                                   { Value-Digits }
 *                                   [ Exponent ]
 *	</pre>
 * @author baranowb
 *
 */
public interface UnitValueAvp extends GroupedAvp {

	/**
	 * Sets the value of the Value-Digits AVP, of type Integer64.
	 * 
	 * @param digits
	 */
	public void setValueDigits(long digits);

	/**
	 * Returns the value of the Value-Digits AVP, of type Integer64.
	 * 
	 * @return
	 */
	public long getValueDigits();

	/**
	 * Sets the value of the Exponent AVP, of type Integer32.
	 * 
	 * @param expotent
	 */
	public void setExponent(int exponent);

	/**
	 * Returns the value of the Exponent AVP, of type Integer32.
	 * 
	 * @return
	 */
	public int getExponent();

	/**
	 * Returns true if the Exponent AVP is present in the message.
	 * 
	 * @return
	 */
	boolean hasExponent();

	/**
	 * Returns true if the Value-Digits AVP is present in the message.
	 * 
	 * @return
	 */
	boolean hasValueDigits();

}
