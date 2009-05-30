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
package org.mobicents.slee.resource.diameter.sh.client.events;

import java.util.Date;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;

/**
 * 
 * Start time:12:40:30 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link SubscribeNotificationsAnswer} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscribeNotificationsAnswerImpl extends DiameterShMessageImpl implements SubscribeNotificationsAnswer {

	public SubscribeNotificationsAnswerImpl(Message msg) {
		super(msg);
		msg.setRequest(false);
		super.longMessageName = "Subscribe-Notification-Answer";
		super.shortMessageName = "SNA";
	}

	public Date getExpiryTime() {
		return getAvpAsTime(DiameterShAvpCodes.EXPIRY_TIME, DiameterShAvpCodes.SH_VENDOR_ID);
	}

	public boolean hasExpiryTime() {
		return hasAvp(DiameterShAvpCodes.EXPIRY_TIME, DiameterShAvpCodes.SH_VENDOR_ID);
	}

	public void setExpiryTime(Date expiryTime) {
		addAvp(DiameterShAvpCodes.EXPIRY_TIME, DiameterShAvpCodes.SH_VENDOR_ID, expiryTime);
	}

  public boolean hasExperimentalResult() {
    return hasAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);
  }

  public ExperimentalResultAvp getExperimentalResult() {
    return (ExperimentalResultAvp) getAvpAsCustom(Avp.EXPERIMENTAL_RESULT, ExperimentalResultAvpImpl.class);
  }

  public void setExperimentalResult(ExperimentalResultAvp experimentalResult) {
    addAvp(Avp.EXPERIMENTAL_RESULT, experimentalResult.byteArrayValue());
  }

}
