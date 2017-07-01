/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, TeleStax Inc. and individual contributors
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
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   JBoss, Home of Professional Open Source
 *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
 *   by the @authors tag. See the copyright.txt in the distribution for a
 *   full listing of individual contributors.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
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
  public boolean isGMLCAddressAvpPresent() {
    return super.message.getAvps().getAvp(Avp.GMLC_ADDRESS) != null;
  }

  @Override
  public java.net.InetAddress getGMLCAddress() {
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
  public boolean isLRAFlagsAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LRA_FLAGS) != null;
  }

  @Override
  public long getLRAFLags() {
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
  public boolean isReportingPLMNListAvpPresent() {
    return super.message.getAvps().getAvp(Avp.REPORTING_PLMN_LIST) != null;
  }

  @Override
  public boolean isPrioritizedListIndicatorAvpPresent() {
    Avp lcsReportingPLMNListAvp = super.message.getAvps().getAvp(Avp.REPORTING_PLMN_LIST);
    if (lcsReportingPLMNListAvp != null) {
      try {
        return lcsReportingPLMNListAvp.getGrouped().getAvp(Avp.PRIORITIZED_LIST_INDICATOR) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Prioritized-List-Indicator AVP", e);
      }
    }
    return false;
  }

  @Override
  public int getPrioritizedListIndicator() {
    Avp lcsReportingPLMNListAvp = super.message.getAvps().getAvp(Avp.REPORTING_PLMN_LIST);
    if (lcsReportingPLMNListAvp != null) {
      try {
        Avp lcsPrioritizedListIndicatorAvp = lcsReportingPLMNListAvp.getGrouped().getAvp(Avp.PRIORITIZED_LIST_INDICATOR);
        if (lcsPrioritizedListIndicatorAvp != null){
          return lcsPrioritizedListIndicatorAvp.getInteger32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS GERAN-Positioning-Data AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isPLMNIDListAvpPresent() {
    Avp lcsReportingPLMNListAvp = super.message.getAvps().getAvp(Avp.REPORTING_PLMN_LIST);
    if (lcsReportingPLMNListAvp != null) {
      try {
        return lcsReportingPLMNListAvp.getGrouped().getAvp(Avp.PLMN_ID_LIST) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain PLMN-Id-List AVP", e);
      }
    }
    return false;
  }

  @Override
  public boolean isVisitedPLMNIdAvpPresent() {
    Avp lcsPLMNIdListAvp = super.message.getAvps().getAvp(Avp.PLMN_ID_LIST);
    if (lcsPLMNIdListAvp != null) {
      try {
        return lcsPLMNIdListAvp.getGrouped().getAvp(Avp.VISITED_PLMN_ID) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Visited-PLMN-Id AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getVisitedPLMNId() {
    Avp lcsPLMNIdListAvp = super.message.getAvps().getAvp(Avp.PLMN_ID_LIST);
    if (lcsPLMNIdListAvp != null) {
      try {
        Avp lcsVisitedPLMNIdAvp = lcsPLMNIdListAvp.getGrouped().getAvp(Avp.VISITED_PLMN_ID);
        if (lcsVisitedPLMNIdAvp != null){
          return lcsVisitedPLMNIdAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Visited PLMN ID AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isPeriodicLocationSupportIndicatorAvpPresent() {
    Avp lcsPLMNIdListAvp = super.message.getAvps().getAvp(Avp.PLMN_ID_LIST);
    if (lcsPLMNIdListAvp != null) {
      try {
        return lcsPLMNIdListAvp.getGrouped().getAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Periodic-Location-Support-Indicator AVP", e);
      }
    }
    return false;
  }

  @Override
  public int getPeriodicLocationSupportIndicator() {
    Avp lcsPLMNIdListAvp = super.message.getAvps().getAvp(Avp.PLMN_ID_LIST);
    if (lcsPLMNIdListAvp != null) {
      try {
        Avp lcsPeriodicLocationSupportIndicatorAvp = lcsPLMNIdListAvp.getGrouped().getAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR);
        if (lcsPeriodicLocationSupportIndicatorAvp != null){
          return lcsPeriodicLocationSupportIndicatorAvp.getInteger32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Periodic Location Support Indicator AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isLCSReferenceNumberAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LCS_REFERENCE_NUMBER) != null;
  }

  @Override
  public byte[] getLCSReferenceNumber() {
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