package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 *<pre> <b>8.27. Tariff-Change-Usage AVP</b>
 *
 *
 *   The Tariff-Change-Usage AVP (AVP Code 452) is of type Enumerated and
 *   defines whether units are used before or after a tariff change, or
 *   whether the units straddled a tariff change during the reporting
 *   period.  Omission of this AVP means that no tariff change has
 *   occurred.
 *
 *   In addition, when present in answer messages as part of the
 *   Multiple-Services-Credit-Control AVP, this AVP defines whether units
 *   are allocated to be used before or after a tariff change event.
 *
 *   When the Tariff-Time-Change AVP is present, omission of this AVP in
 *   answer messages means that the single quota mechanism applies.
 *
 *   Tariff-Change-Usage can be one of the following:
 *
 *   <b>UNIT_BEFORE_TARIFF_CHANGE       0</b>
 *      When present in the Multiple-Services-Credit-Control AVP, this
 *      value indicates the amount of the units allocated for use before a
 *      tariff change occurs.
 *      When present in the Used-Service-Unit AVP, this value indicates
 *      the amount of resource units used before a tariff change had
 *      occurred.
 *
 *   <b>UNIT_AFTER_TARIFF_CHANGE        1</b>
 *      When present in the Multiple-Services-Credit-Control AVP, this
 *      value indicates the amount of the units allocated for use after a
 *      tariff change occurs.
 *
 *      When present in the Used-Service-Unit AVP, this value indicates
 *      the amount of resource units used after tariff change had
 *      occurred.
 *
 *   <b>UNIT_INDETERMINATE              2</b>
 *      The used unit contains the amount of units that straddle the
 *      tariff change (e.g., the metering process reports to the credit-
 *      control client in blocks of n octets, and one block straddled the
 *      tariff change).  This value is to be used only in the Used-
 *      Service-Unit AVP.
 *      </pre>
 * @author baranowb
 *
 */
public enum TariffChangeUsageType implements Enumerated {

	
	UNIT_BEFORE_TARIFF_CHANGE(0),UNIT_AFTER_TARIFF_CHANGE(1),UNIT_INDETERMINATE(2); 
	
	
	private int value=-1;
	
	
	private TariffChangeUsageType(int value)
	{
		this.value=value;
	}
	
	
	
	
	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: "
					+ value);
		}
	}

	public TariffChangeUsageType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return UNIT_BEFORE_TARIFF_CHANGE;
		case 1:
			return UNIT_AFTER_TARIFF_CHANGE;
		case 2:
			return UNIT_INDETERMINATE;
		
		

		default:
			throw new IllegalArgumentException();

		}

	}
	
	
	

	public int getValue() {
		
		return this.value;
	}

	
	
}
