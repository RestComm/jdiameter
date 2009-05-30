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

import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.avp.DisconnectCauseType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

/**
 * 
 * <br>
 * Project: mobicents-diameter-server <br>
 * 2:37:56 PM May 25, 2009 <br>
 * 
 * Implementation of {@link DisconnectPeerRequest}.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @see DiameterMessageImpl
 */
public class DisconnectPeerRequestImpl extends DiameterMessageImpl implements DisconnectPeerRequest {

  public DisconnectPeerRequestImpl(Message message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getLongName() {
    return "Disconnect-Peer-Request";
  }

  @Override
  public String getShortName() {
    return "DPR";
  }

  public DisconnectCauseType getDisconnectCause() {
    return (DisconnectCauseType) getAvpAsEnumerated(Avp.DISCONNECT_CAUSE, DisconnectCauseType.class);
  }

  public boolean hasDisconnectCause() {
    return hasAvp(Avp.DISCONNECT_CAUSE);
  }

  public void setDisconnectCause(DisconnectCauseType disconnectCause) {
    addAvp(Avp.DISCONNECT_CAUSE, (long)disconnectCause.getValue());
  }

}
