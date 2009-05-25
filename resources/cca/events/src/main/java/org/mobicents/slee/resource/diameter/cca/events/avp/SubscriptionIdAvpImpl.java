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
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:25:13 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link SubscriptionIdAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscriptionIdAvpImpl extends GroupedAvpImpl implements SubscriptionIdAvp {

	private static transient Logger logger = Logger.getLogger(SubscriptionIdAvpImpl.class);

	public SubscriptionIdAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * getSubscriptionIdData()
	 */
	public String getSubscriptionIdData() {
		if (hasSubscriptionIdData()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Subscription_Id_Data);
			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Subscription_Id_Data);
				logger.error("Failure while trying to obtain Subscription-Id-Data AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * getSubscriptionIdType()
	 */
	public SubscriptionIdType getSubscriptionIdType() {
		if (hasSubscriptionIdType()) {
			int v = (int) super.getAvpAsUInt32(CreditControlAVPCodes.Subscription_Id_Type);
			return SubscriptionIdType.END_USER_E164.fromInt(v);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * hasSubscriptionIdData()
	 */
	public boolean hasSubscriptionIdData() {
		return super.hasAvp(CreditControlAVPCodes.Subscription_Id_Data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * hasSubscriptionIdType()
	 */
	public boolean hasSubscriptionIdType() {
		return super.hasAvp(CreditControlAVPCodes.Subscription_Id_Type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * setSubscriptionIdData(java.lang.String)
	 */
	public void setSubscriptionIdData(String data) {
		addAvp(CreditControlAVPCodes.Subscription_Id_Data, 10415L, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * setSubscriptionIdType
	 * (net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType)
	 */
	public void setSubscriptionIdType(SubscriptionIdType type) {
	  addAvp(CreditControlAVPCodes.Subscription_Id_Type, 10415L, type.getValue());
	}

}
