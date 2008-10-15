package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 *  &lt;b&gt;8.18. Requested-Service-Unit AVP&lt;/b&gt;
 * 
 * 
 *   The Requested-Service-Unit AVP (AVP Code 437) is of type Grouped and
 *   contains the amount of requested units specified by the Diameter
 *   credit-control client.  A server is not required to implement all the
 *   unit types, and it must treat unknown or unsupported unit types as
 *   invalid AVPs.
 * 
 *   The Requested-Service-Unit AVP is defined as follows (per the
 *   grouped-avp-def of RFC 3588 [DIAMBASE]):
 * 
 *      Requested-Service-Unit ::= &lt; AVP Header: 437 &gt;
 *                                 [ CC-Time ]
 *                                 [ CC-Money ]
 *                                 [ CC-Total-Octets ]
 *                                 [ CC-Input-Octets ]
 *                                 [ CC-Output-Octets ]
 *                                 [ CC-Service-Specific-Units ]
 *                                *[ AVP ]
 * </pre>
 * 
 * @author baranowb
 * 
 */
public interface RequestedServiceUnitAvp extends GroupedAvp {

	/**
	 * Sets the value of the CC-Money AVP, of type Grouped. <br>
	 * See: {@link CcMoneyAvp}
	 * 
	 * @param ccm
	 */
	public void setCreditControlMoneyAvp(CcMoneyAvp ccm);

	/**
	 * Returns the value of the CC-Money AVP, of type Grouped. A return value of
	 * null implies that the AVP has not been set. <br>
	 * See: {@link CcMoneyAvp}
	 * 
	 * @return
	 */
	public CcMoneyAvp getCreditControlMoneyAvp();

	/**
	 * Returns true if the CC-Money AVP is present in the message.
	 * 
	 * @return
	 */
	public boolean hasCreditControlMoneyAvp();

	/**
	 * Sets the value of the CC-Total-Octets AVP, of type Unsigned64.
	 * 
	 * @param ccto
	 */
	public void setCreditControlTotalOctets(long ccto);

	/**
	 * Returns the value of the CC-Total-Octets AVP, of type Unsigned64.
	 * 
	 * @return
	 */
	public long getCreditControlTotalOctets();

	/**
	 * Returns true if the CC-Total-Octets AVP is present in the message.
	 * 
	 * @return
	 */
	public boolean hasCreditControlTotalOctets();

	/**
	 * Sets the value of the CC-Input-Octets AVP, of type Unsigned64.
	 * 
	 * @param ttc
	 */
	public void setCreditControlInputOctets(long ttc);

	/**
	 * Returns the value of the CC-Input-Octets AVP, of type Unsigned64.
	 * 
	 * @return
	 */
	public long getCreditControlInputOctets();

	/**
	 * Returns true if the CC-Input-Octets AVP is present in the message.
	 * 
	 * @return
	 */
	public boolean hasCreditControlInputOctets();

	/**
	 * Sets the value of the CC-Output-Octets AVP, of type Unsigned64.
	 * 
	 * @param ccoo
	 */
	public void setCreditControlOutputOctets(long ccoo);

	/**
	 * Returns the value of the CC-Output-Octets AVP, of type Unsigned64.
	 * 
	 * @return
	 */
	public long getCreditControlOutputOctets();

	/**
	 * Returns true if the CC-Output-Octets AVP is present in the message.
	 * 
	 * @return
	 */
	public boolean hasCreditControlOutputOctets();

	/**
	 * Sets the value of the CC-Time AVP, of type Unsigned32.
	 * 
	 * @param cct
	 */
	public void setCreditControlTime(long cct);

	/**
	 * Returns the value of the CC-Time AVP, of type Unsigned32.
	 * 
	 * @return
	 */
	public long getCreditControlTime();

	/**
	 * Returns true if the CC-Time AVP is present in the message.
	 * 
	 * @return
	 */
	public boolean hasCreditControlTime();

	/**
	 * Sets the value of the CC-Service-Specific-Units AVP, of type Unsigned64.
	 * 
	 * @param ccssu
	 */
	public void setCreditControlServiceSpecificUnits(long ccssu);

	/**
	 * Returns the value of the CC-Service-Specific-Units AVP, of type
	 * Unsigned64.
	 * 
	 * @return
	 */
	public long getCreditControlServiceSpecificUnits();

	/**
	 * Returns true if the CC-Service-Specific-Units AVP is present in the
	 * message.
	 * 
	 * @return
	 */
	public boolean hasCreditControlServiceSpecificUnits();

	// public void setExtensionsAvps(DiameterAvp[] extensions);
	// public DiameterAvp[] getExtensionsAvps();
	// public boolean hasExtensionsAvps();

}
