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
package org.mobicents.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * Implementation of AVP: {@link SupportedFeaturesAvp} UserIdentityAvp.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UserIdentityAvpImpl extends GroupedAvpImpl implements UserIdentityAvp {

	public UserIdentityAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	public byte[] getMsisdn() {
		if (hasMsisdn()) {
			Avp rawAvp = super.avpSet.getAvp(DiameterShAvpCodes.MSISDN, 10415L);

			try {
				return rawAvp.getRaw();
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), DiameterShAvpCodes.MSISDN);
				// logger.error( "Failure while trying to obtain MSISDN AVP.", e
				// );
			}
		}

		return null;
	}

	public String getPublicIdentity() {
		if (hasPublicIdentity()) {
			Avp rawAvp = super.avpSet.getAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, 10415L);

			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), DiameterShAvpCodes.PUBLIC_IDENTITY);
				// logger.error( "Failure while trying to obtain MSISDN AVP.", e
				// );
			}
		}

		return null;
	}

	public boolean hasMsisdn() {
		return super.avpSet.getAvp(DiameterShAvpCodes.MSISDN, 10415L) != null;
	}

	public boolean hasPublicIdentity() {
		return super.avpSet.getAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, 10415L) != null;
	}

	public void setMsisdn(byte[] msisdn) {
		if (hasMsisdn()) {
			throw new IllegalStateException("AVP MSISDN is already present in message and cannot be overwritten.");
		} else {
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.MSISDN, 10415L);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

			// super.avpSet.removeAvp(DiameterShAvpCodes.MSISDN);
			super.avpSet.addAvp(DiameterShAvpCodes.MSISDN, msisdn, 10415L, mandatoryAvp == 1, protectedAvp == 1);
		}
	}

	public void setPublicIdentity(String publicIdentity) {
		if (hasPublicIdentity()) {
			throw new IllegalStateException("AVP Public-Identity is already present in message and cannot be overwritten.");
		} else {
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, 10415L);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

			// super.avpSet.removeAvp(DiameterShAvpCodes.PUBLIC_IDENTITY);
			super.avpSet.addAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, publicIdentity, 10415L, mandatoryAvp == 1, protectedAvp == 1, false);
		}
	}

}
