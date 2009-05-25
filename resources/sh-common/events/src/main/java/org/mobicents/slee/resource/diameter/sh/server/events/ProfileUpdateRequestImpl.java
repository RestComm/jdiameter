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
package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

/**
 * 
 * Start time:10:23:07 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link ProfileUpdateRequest} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ProfileUpdateRequestImpl extends DiameterShMessageImpl implements ProfileUpdateRequest {

	private static transient Logger logger = Logger.getLogger(ProfileUpdateRequestImpl.class);

	public ProfileUpdateRequestImpl(Message msg) {
		super(msg);

		msg.setRequest(true);

		super.longMessageName = "Profile-Update-Request";
		super.shortMessageName = "PUR";
	}

	public DataReferenceType getDataReference() {
		try {
			return hasDataReference() ? DataReferenceType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.DATA_REFERENCE).getInteger32()) : null;
		} catch (AvpDataException e) {
			logger.error("Unable to decode Data-Reference AVP contents.", e);
		}

		return null;
	}

	public UserIdentityAvp getUserIdentity() {
		if (hasUserIdentity()) {
			try {
				Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY, 10415L);

				UserIdentityAvpImpl a = new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, new byte[] {});

				for (Avp subAvp : rawAvp.getGrouped()) {
					try {
						a.setExtensionAvps(new DiameterAvp[] { new DiameterAvpImpl(subAvp.getCode(), subAvp.getVendorId(), subAvp.isMandatory() ? 1 : 0, subAvp.isEncrypted() ? 1 : 0, subAvp.getRaw(),
								null) });
					} catch (AvpNotAllowedException e) {
						logger.error("Unable to add child AVPs to User-Identity AVP.", e);
					}
				}

				return a;
			} catch (AvpDataException e) {
				logger.error("Unable to decode User-Identity AVP contents.", e);
			}
		}

		return null;
	}

	public boolean hasDataReference() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.DATA_REFERENCE) != null;
	}

	public boolean hasUserData() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
	}

	public boolean hasUserIdentity() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
	}

	public void setDataReference(DataReferenceType dataReference) {
		super.setAvpAsInt32(DiameterShAvpCodes.DATA_REFERENCE, dataReference.getValue(), true, true);
	}

	public void setUserIdentity(UserIdentityAvp userIdentity) {
		super.setAvpAsGroup(userIdentity.getCode(), new UserIdentityAvp[] { userIdentity }, true, true);
	}

	public String getUserData() {
		try {
			return hasUserData() ? super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getUTF8String() : null;
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
