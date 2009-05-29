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
package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.avp.ReAuthRequestType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

/**
 * 
 * Start time:16:56:38 2009-05-22<br>
 * Project: diameter-parent<br>
 * 
 * 
 * Implementation of {@link ReAuthRequest}
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @see DiameterMessageImpl
 */
public class ReAuthRequestImpl extends DiameterMessageImpl implements ReAuthRequest {
	public ReAuthRequestImpl(Message message) {
		super(message);
	}

	public boolean hasReAuthRequestType() {
		return message.getAvps().getAvp(Avp.RE_AUTH_REQUEST_TYPE) != null;
	}

  public ReAuthRequestType getReAuthRequestType() {
    return ReAuthRequestType.fromInt(getAvpAsInteger32(Avp.RE_AUTH_REQUEST_TYPE));
  }

	public void setReAuthRequestType(ReAuthRequestType reAuthRequestType) {
		addAvp(Avp.RE_AUTH_REQUEST_TYPE, reAuthRequestType.getValue());
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
