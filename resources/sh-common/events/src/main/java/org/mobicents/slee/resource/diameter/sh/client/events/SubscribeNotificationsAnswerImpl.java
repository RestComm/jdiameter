/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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
package org.mobicents.slee.resource.diameter.sh.client.events;

import java.util.Date;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;

/**
 * 
 * Start time:12:40:30 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link SubscribeNotificationsAnswer} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscribeNotificationsAnswerImpl extends DiameterShMessageImpl implements SubscribeNotificationsAnswer {

	private static transient Logger logger = Logger.getLogger(SubscribeNotificationsAnswerImpl.class);

	public SubscribeNotificationsAnswerImpl(Message msg) {
		super(msg);
		msg.setRequest(false);
		super.longMessageName = "Subscribe-Notification-Answer";
		super.shortMessageName = "SNA";
	}

	public Date getExpiryTime() {
		return this.hasExpiryTime() ? super.getAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME) : null;
	}

	public boolean hasExpiryTime() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.EXPIRY_TIME) != null;
	}

	public void setExpiryTime(Date expiryTime) {
		super.setAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME, expiryTime, true, true);
	}

	public ExperimentalResultAvp getExperimentalResult() {
		ExperimentalResultAvp avp = null;

		try {
			Avp rawAvp = super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);

			if (rawAvp != null) {
				Avp ercAvp = rawAvp.getGrouped().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT_CODE);
				Avp vidAvp = rawAvp.getGrouped().getAvp(DiameterAvpCodes.VENDOR_ID);

				avp = new ExperimentalResultAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, new byte[] {});

				if (ercAvp != null) {
					avp.setExperimentalResultCode(ercAvp.getUnsigned32());
				}

				if (vidAvp != null) {
					avp.setVendorId(vidAvp.getUnsigned32());
				}
			}
		} catch (AvpDataException e) {
			logger.error("Unable to decode Experimental-Result AVP contents.", e);
		}

		return avp;
	}

	public boolean hasExperimentalResult() {
		return super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT) != null;
	}

	public void setExperimentalResult(ExperimentalResultAvp experimentalResult) {
		super.setAvpAsGroup(experimentalResult.getCode(), experimentalResult.getExtensionAvps(), experimentalResult.getMandatoryRule() == 1, true);
	}

}
