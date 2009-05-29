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

import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

/**
 * <br>Project: mobicents-diameter-server
 * <br>11:21:03 AM May 25, 2009 
 * 
 *Implementation of {@link SessionTerminationRequest}.
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @see SessionTerminationMessageImpl
 */
public class SessionTerminationRequestImpl  extends SessionTerminationMessageImpl implements SessionTerminationRequest{

  public SessionTerminationRequestImpl(Message message) {
    super(message);
  }

  @Override
  public String getLongName() {
    return "Session-Termination-Request";
  }

  @Override
  public String getShortName() {
    return "STR";
  }

  public boolean hasTerminationCause() {
    return message.getAvps().getAvp(Avp.TERMINATION_CAUSE) != null;
  }

  public TerminationCauseType getTerminationCause() {
    return TerminationCauseType.fromInt(getAvpAsInteger32(Avp.TERMINATION_CAUSE));
  }

  public void setTerminationCause(TerminationCauseType terminationCause) {
    addAvp(Avp.TERMINATION_CAUSE, terminationCause.getValue());
  }

}
