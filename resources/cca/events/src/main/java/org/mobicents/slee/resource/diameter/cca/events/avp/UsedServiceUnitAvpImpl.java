package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;

/**
 * Start time:18:48:32 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link UsedServiceUnitAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UsedServiceUnitAvpImpl extends ServiceUnitAvpTypeImpl implements UsedServiceUnitAvp {

	private static transient Logger logger = Logger.getLogger(UsedServiceUnitAvpImpl.class);

	public UsedServiceUnitAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getTariffChangeUsage()
	 */
	public TariffChangeUsageType getTariffChangeUsage() {
		if (hasTariffChangeUsage()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Tariff_Change_Usage);
			try {
				return TariffChangeUsageType.UNIT_AFTER_TARIFF_CHANGE.fromInt((int) rawAvp.getUnsigned32());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Tariff_Change_Usage);
				logger.error("Failure while trying to obtain Tariff-Change-Usage AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp#
	 * hasTariffChangeUsage()
	 */
	public boolean hasTariffChangeUsage() {
		return super.hasAvp(CreditControlAVPCodes.Tariff_Change_Usage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp#
	 * setTariffChangeUsage
	 * (net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType)
	 */
	public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
		if (hasAvp(CreditControlAVPCodes.Tariff_Change_Usage)) {
			throw new IllegalStateException("AVP Tariff-Change-Usage is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Tariff_Change_Usage, tariffChangeUsage.getValue(), true);
	}

}
