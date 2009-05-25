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

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.SessionTerminationMessage;

/**
 * Start time:19:08:53 2009-05-22<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class SessionTerminationMessageImpl extends DiameterMessageImpl implements SessionTerminationMessage {
  
  private Logger logger = Logger.getLogger(SessionTerminationMessageImpl.class);

  /**
   * 
   * @param message
   */
  public SessionTerminationMessageImpl(Message message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public byte[][] getClassAvps() {
    if (hasClassAvp()) {
      AvpSet s = message.getAvps().getAvps(Avp.CLASS);

      byte[][] rc = new byte[s.size()][];

      for (int i = 0; i < s.size(); i++) {
        try {
          rc[i] = s.getAvpByIndex(i).getRaw();
        } catch (Exception e) {
          logger.error("Unable to obtain/decode AVP (code:" + Avp.CLASS + ")", e);
        }
      }

      return rc;
    } else {
      return null;
    }
  }

  public void setClassAvp(byte[] classAvp) {
    message.getAvps().addAvp(25, classAvp, true, false);
  }

  public void setClassAvps(byte[][] classAvps) {
    for (byte[] i : classAvps) {
      setClassAvp(i);
    }
  }

  public byte[] getClassAvp() {
    if (hasClassAvp()) {
      Avp s = message.getAvps().getAvp(Avp.CLASS);

      try {
        return s.getRaw();
      } catch (AvpDataException e) {
        logger.error("Unable to obtain/decode AVP (code:" + Avp.CLASS + ")", e);
        return null;
      }
    } else {
      return null;
    }
  }

  public boolean hasClassAvp() {
    return super.hasAvp(Avp.CLASS);
  }

}
