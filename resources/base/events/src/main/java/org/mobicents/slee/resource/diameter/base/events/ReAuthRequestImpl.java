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

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.avp.ReAuthRequestTypeAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

public class ReAuthRequestImpl extends ExtensionDiameterMessageImpl implements ReAuthRequest
{

	
	public ReAuthRequestImpl(Message message) {
        super(message);
    }

    public boolean hasReAuthRequestType() {
        return message.getAvps().getAvp(Avp.RE_AUTH_REQUEST_TYPE) != null;
    }

    public ReAuthRequestTypeAvp getReAuthRequestType() {
        return ReAuthRequestTypeAvp.fromInt(getAvpAsInt32(Avp.RE_AUTH_REQUEST_TYPE));
    }

    public void setReAuthRequestType(ReAuthRequestTypeAvp reAuthRequestType) {
        setAvpAsInt32(Avp.RE_AUTH_REQUEST_TYPE, reAuthRequestType.getValue(), true,true);
    }

	
	@Override
	public String getLongName() {
		
		return "Re-Auth-Request";
	}

	@Override
	public String getShortName() {

		return "RAR";
	}

	
}
