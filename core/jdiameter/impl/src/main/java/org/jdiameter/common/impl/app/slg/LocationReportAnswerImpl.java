/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.common.impl.app.slg;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
 *
 */

public class LocationReportAnswerImpl extends AppRequestEventImpl implements LocationReportAnswer {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(LocationReportAnswerImpl.class);

  /**
   *
   * @param answer
   */
  public LocationReportAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public LocationReportAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  public Avp getResultCodeAvp() throws AvpDataException {
    return null;
  }

  @Override
  public boolean isGMLCAddressAVPPresent(){
    return super.message.getAvps().getAvp(Avp.GMLC_ADDRESS) != null;
  }

  @Override
  public java.net.InetAddress getGMLCAddress(){
    Avp lcsGMLCAddressAvp = super.message.getAvps().getAvp(Avp.GMLC_ADDRESS);
    if (lcsGMLCAddressAvp != null) {
      try {
        return lcsGMLCAddressAvp.getAddress();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLRAFlagsAVPPresent(){
    return super.message.getAvps().getAvp(Avp.LRA_FLAGS) != null;
  }

  @Override
  public long getLRAFLags(){
    Avp lcsLRAFlagsAvp = super.message.getAvps().getAvp(Avp.LRA_FLAGS);
    if (lcsLRAFlagsAvp != null) {
      try {
        return lcsLRAFlagsAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS LRA Flags AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isReportingPLMNListAVPPresent(){
    return super.message.getAvps().getAvp(Avp.REPORTING_PLMN_LIST) != null;
  }

  @Override
  public boolean isPLMNIDListAVPPresent(){
    return super.message.getAvps().getAvp(Avp.PLMN_ID_LIST) != null;
  }

  @Override
  public byte[] getVisitedPLMNId() {
    Avp lcsVisitedPLMNIdAvp = super.message.getAvps().getAvp(Avp.VISITED_PLMN_ID);
    if (lcsVisitedPLMNIdAvp != null) {
      try {
        return lcsVisitedPLMNIdAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Visited PLMN ID AVP value", e);
      }
    }
    return null;
  }

  @Override
  public int getPeriodicLocationSupportIndicator() {
    Avp lcsPeriodicLocationSupportIndicatorAvp = super.message.getAvps().getAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR);
    if (lcsPeriodicLocationSupportIndicatorAvp != null) {
      try {
        return lcsPeriodicLocationSupportIndicatorAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Periodic Location Support Indicator AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isLCSReferenceNumberAVPPresent(){
    return super.message.getAvps().getAvp(Avp.LCS_REFERENCE_NUMBER) != null;
  }

  @Override
  public byte[] getLCSReferenceNumber(){
    Avp lcsLCSReferenceNumberAvp = super.message.getAvps().getAvp(Avp.LCS_REFERENCE_NUMBER);
    if (lcsLCSReferenceNumberAvp != null) {
      try {
        return lcsLCSReferenceNumberAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Reference Number AVP value", e);
      }
    }
    return null;
  }

}
