/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.jdiameter.common.impl.app.slh;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdiameter.api.Message;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class LCSRoutingInfoRequestImpl extends AppRequestEventImpl implements LCSRoutingInfoRequest {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(LCSRoutingInfoRequestImpl.class);

  public LCSRoutingInfoRequestImpl(Message message) {
    super(message);
    message.setRequest(true);
  }

  @Override
  public boolean isUserNameAVPPresent() {
    return super.message.getAvps().getAvp(Avp.USER_NAME) != null; // IE: IMSI
  }

  @Override
  public String getUserName() {
    Avp userNameAvp = super.message.getAvps().getAvp(Avp.USER_NAME);
    if (userNameAvp != null) {
      try {
        return userNameAvp.getUTF8String(); // IE: IMSI mapped to User-Name
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain User-Name AVP value (IMSI)", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMSISDNAVPPresent() {
    return super.message.getAvps().getAvp(Avp.MSISDN) != null;
  }

  @Override
  public byte[] getMSISDN() {
    Avp msisdnAvp = super.message.getAvps().getAvp(Avp.MSISDN);
    if (msisdnAvp != null) {
      try {
        return msisdnAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSISDN AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isGMLCNumberAVPPresent() {
    return super.message.getAvps().getAvp(Avp.GMLC_NUMBER) != null;
  }

  @Override
  public byte[] getGMLCNumber() {
    Avp gmlcNumberAvp = super.message.getAvps().getAvp(Avp.GMLC_NUMBER);
    if (gmlcNumberAvp != null) {
      try {
        return gmlcNumberAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC-Number AVP value", e);
      }
    }
    return null;
  }

}