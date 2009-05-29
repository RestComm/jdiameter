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
package org.mobicents.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;

/**
 * 
 * Start time:12:40:14 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link UserDataAnswer} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UserDataAnswerImpl extends DiameterShMessageImpl implements UserDataAnswer {

  private static transient Logger logger = Logger.getLogger(UserDataAnswerImpl.class);

  /**
   * 
   * @param msg
   */
  public UserDataAnswerImpl(Message msg) {
    super(msg);
    msg.setRequest(false);
    super.longMessageName = "User-Data-Answer";
    super.shortMessageName = "UDA";
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.UserDataAnswer#hasUserData()
   */
  public boolean hasUserData() {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.UserDataAnswer#getUserData()
   */
  public String getUserData() {
    if (hasUserData()) {
      try {
        return new String(super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA).getRaw());
      }
      catch (AvpDataException e) {
        logger.error("Unable to decode User-Data AVP contents.", e);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.UserDataAnswer#setUserData(byte[])
   */
  public void setUserData(byte[] userData) {
    addAvp(DiameterShAvpCodes.USER_DATA, 10415L, userData);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.UserDataAnswer#getExperimentalResult()
   */
  public ExperimentalResultAvp getExperimentalResult() {
    ExperimentalResultAvp avp = null;

    try {
      Avp rawAvp = super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);

      if (rawAvp != null) {
        Avp ercAvp = rawAvp.getGrouped().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT_CODE);
        Avp vidAvp = rawAvp.getGrouped().getAvp(DiameterAvpCodes.VENDOR_ID);

        avp = new ExperimentalResultAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, new byte[] {});

        if (ercAvp != null) {
          avp.setExperimentalResultCode(ercAvp.getUnsigned32());
        }

        if (vidAvp != null) {
          avp.setVendorIdAVP(vidAvp.getUnsigned32());
        }
      }
    }
    catch (AvpDataException e) {
      logger.error("Unable to decode Experimental-Result AVP contents.", e);
    }

    return avp;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.UserDataAnswer#hasExperimentalResult()
   */
  public boolean hasExperimentalResult() {
    return super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT) != null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.UserDataAnswer#setExperimentalResult(net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp)
   */
  public void setExperimentalResult(ExperimentalResultAvp experimentalResult) {
    setAvpAsGrouped(experimentalResult.getCode(), experimentalResult.getVendorId(), experimentalResult.getExtensionAvps(), experimentalResult.getMandatoryRule() == 0, experimentalResult.getProtectedRule() == 0);
  }
}
