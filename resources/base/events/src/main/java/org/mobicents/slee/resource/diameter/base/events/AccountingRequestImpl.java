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
package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AccountingRequest;

import org.jdiameter.api.Message;

/**
 * <br>
 * Super project: mobicents <br>
 * 5:57:35 PM Jun 20, 2008 <br>
 * Implementation of {@link AccountingRequest}
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Erick Svenson
 * @see AccountingMessageImpl
 */
public class AccountingRequestImpl extends AccountingMessageImpl implements AccountingRequest {

	public AccountingRequestImpl(Message message) {
		super(message);
	}

	@Override
	public String getLongName() {
		return "Accounting-Request";
	}

	@Override
	public String getShortName() {
		return "ACR";
	}

	public boolean isValid() {
		// One of Acct-Application-Id and Vendor-Specific-Application-Id AVPs
		// MUST be present. If the Vendor-Specific-Application-Id grouped AVP
		// is present, it must have an Acct-Application-Id inside.

		if (!this.message.isRequest()) {
			return false;
		} else if (!this.hasAccountingRealtimeRequired()) {
			if (!this.hasVendorSpecificApplicationId()) {
				return false;
			} else {
				if (this.getVendorSpecificApplicationId().getAcctApplicationId() == -1) {
					return false;
				}
			}
		}

		return true;
	}

}
