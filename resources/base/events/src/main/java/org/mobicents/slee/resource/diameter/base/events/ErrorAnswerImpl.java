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

import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

import org.jdiameter.api.Message;

public class ErrorAnswerImpl extends DiameterMessageImpl implements
		ErrorAnswer {

	public ErrorAnswerImpl(Message message) {
		super(message);
		
	}

	public ProxyInfoAvp getProxyInfo() {
		if(hasProxyInfo())
			return super.getProxyInfos()[0];
		else
			return null;
	}

	public boolean hasProxyInfo() {
		ProxyInfoAvp[] infos = super.getProxyInfos();
		if (infos != null && infos.length > 0)
			return true;
		else
			return false;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
	 */
	@Override
	public String getLongName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
	 */
	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}

}
