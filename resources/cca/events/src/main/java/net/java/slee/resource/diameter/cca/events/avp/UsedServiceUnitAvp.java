package net.java.slee.resource.diameter.cca.events.avp;

/**
 * <pre>
 *  &lt;b&gt;8.19. Used-Service-Unit AVP&lt;/b&gt;
 * 
 * 
 *   The Used-Service-Unit AVP is of type Grouped (AVP Code 446) and
 *   contains the amount of used units measured from the point when the
 *   service became active or, if interim interrogations are used during
 *   the session, from the point when the previous measurement ended.
 * 
 *   The Used-Service-Unit AVP is defined as follows (per the grouped-
 *   avp-def of RFC 3588 [DIAMBASE]):
 * 
 *      Used-Service-Unit ::= &lt; AVP Header: 446 &gt;
 *                            [ Tariff-Change-Usage ]
 *                            [ CC-Time ]
 *                            [ CC-Money ]
 *                            [ CC-Total-Octets ]
 *                            [ CC-Input-Octets ]
 *                            [ CC-Output-Octets ]
 *                            [ CC-Service-Specific-Units ]
 *                           *[ AVP ]
 * </pre>
 * 
 * @author baranowb
 * 
 */
public interface UsedServiceUnitAvp extends RequestedServiceUnitAvp {
	// TODO: This extension implies another check in impl, but thats so much
	// easier... :]

	/**
	 * Sets the value of the Tariff-Change-Usage AVP, of type Enumerated. <br>
	 * See:{@link TariffChangeUsageType}
	 */
	public void setTariffChangeUsage(TariffChangeUsageType ttc);

	/**
	 * Returns the value of the Tariff-Change-Usage AVP, of type Enumerated.
	 * <br>
	 * See:{@link TariffChangeUsageType}
	 */
	public TariffChangeUsageType getTariffChangeUsage();

	/**
	 * Returns true if Tariff-Change-Usage AVP is present in message.
	 * 
	 * @return
	 */
	public boolean hasTariffChangeUsage();

}
