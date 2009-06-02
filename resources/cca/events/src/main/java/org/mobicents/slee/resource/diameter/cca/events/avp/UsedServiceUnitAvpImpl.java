/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;

/**
 * Start time:18:48:32 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link UsedServiceUnitAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UsedServiceUnitAvpImpl extends ServiceUnitAvpTypeImpl implements UsedServiceUnitAvp {

	public UsedServiceUnitAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getTariffChangeUsage()
	 */
	public TariffChangeUsageType getTariffChangeUsage() {
		return (TariffChangeUsageType) getAvpAsEnumerated(CreditControlAVPCodes.Tariff_Change_Usage, TariffChangeUsageType.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp#hasTariffChangeUsage()
	 */
	public boolean hasTariffChangeUsage() {
		return hasAvp(CreditControlAVPCodes.Tariff_Change_Usage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp#setTariffChangeUsage
	 * (net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType)
	 */
	public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
		addAvp(CreditControlAVPCodes.Tariff_Change_Usage, (long)tariffChangeUsage.getValue());
	}

}
