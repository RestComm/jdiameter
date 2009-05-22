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

import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

/**
 * 
 * Start time:12:40:46 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link PushNotificationRequest} interface.
 * 
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PushNotificationRequestImpl extends DiameterShMessageImpl implements PushNotificationRequest {

	private static transient Logger logger = Logger.getLogger(PushNotificationRequestImpl.class);

	public PushNotificationRequestImpl(Message msg) {
		super(msg);
		msg.setRequest(true);
		super.longMessageName = "Push-Notification-Request";
		super.shortMessageName = "PNR";
	}

	public UserIdentityAvp getUserIdentity() {
		if (!hasUserIdentity()) {
			return null;
		}

		Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);

		try {
			return new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
		} catch (AvpDataException e) {
			logger.error("Unable to decode User-Identity AVP contents.", e);
		}

		return null;
	}

	public boolean hasUserIdentity() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
	}

	public void setUserIdentity(UserIdentityAvp userIdentity) {
		if (hasUserIdentity()) {
			throw new IllegalStateException("AVP User-Identity is already present in message and cannot be overwritten.");
		} else {
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.USER_IDENTITY, 10415L);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			// int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 :
			// 0;

			// FIXME: Alexandre: Need to specify protected!
			super.setAvpAsGroup(avpRep.getCode(), avpRep.getVendorId(), userIdentity.getExtensionAvps(), mandatoryAvp == 1, false);
		}
	}

	public boolean hasUserData() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
	}

	public String getUserData() {
		try {
			return hasUserData() ? new String(super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getRaw()) : null;
		} catch (AvpDataException e) {
			logger.error("Unable to decode User-Data AVP contents.", e);
		}

		return null;
	}

	public void setUserData(byte[] userData) {
		super.message.getAvps().removeAvp(DiameterShAvpCodes.USER_DATA);
		super.message.getAvps().addAvp(DiameterShAvpCodes.USER_DATA, userData, 10415L, true, false);
	}

}
