/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.common.impl.app.gx;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.gx.events.GxCreditControlAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:carl-magnus.bjorkell@emblacom.com"> Carl-Magnus Björkell </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class GxCreditControlAnswerImpl extends AppAnswerEventImpl implements GxCreditControlAnswer {

  private static final long serialVersionUID = 1L;
  protected static final Logger logger = LoggerFactory.getLogger(GxCreditControlAnswerImpl.class);
  private static final int CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE = 427;
  private static final int DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE = 428;
  private static final int REQUESTED_ACTION_AVP_CODE = 436;
  private static final int CC_REQUEST_TYPE_AVP_CODE = 416;
  private static final int VALIDITY_TIME_AVP_CODE = 448;

  /**
   * @param answer
   */
  public GxCreditControlAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   * @param request
   * @param vendorId
   * @param resultCode
   */
  public GxCreditControlAnswerImpl(Request request, long vendorId, long resultCode) {
    super(request, vendorId, resultCode);
  }

  /**
   * @param request
   * @param resultCode
   */
  public GxCreditControlAnswerImpl(Request request, long resultCode) {
    super(request, resultCode);
  }

  /**
   * @param request
   */
  public GxCreditControlAnswerImpl(Request request) {
    super(request);
  }

  @Override
  public boolean isCreditControlFailureHandlingAVPPresent() {
    return super.message.getAvps().getAvp(CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE) != null;
  }

  @Override
  public int getCredidControlFailureHandlingAVPValue() {
    Avp credidControlFailureHandlingAvp = super.message.getAvps().getAvp(CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE);
    if (credidControlFailureHandlingAvp != null) {
      try {
        return credidControlFailureHandlingAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Credit-Control-Failure-Handling AVP value", e);
      }
    }

    return -1;
  }

  @Override
  public boolean isDirectDebitingFailureHandlingAVPPresent() {
    return super.message.getAvps().getAvp(DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE) != null;
  }

  @Override
  public int getDirectDebitingFailureHandlingAVPValue() {
    Avp directDebitingFailureHandlingAvp = super.message.getAvps().getAvp(DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE);
    if (directDebitingFailureHandlingAvp != null) {
      try {
        return directDebitingFailureHandlingAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Direct-Debiting-Failure-Handling AVP value", e);
      }
    }

    return -1;
  }

  @Override
  public Avp getValidityTimeAvp() {
    return super.message.getAvps().getAvp(VALIDITY_TIME_AVP_CODE);
  }

  @Override
  public boolean isRequestTypeAVPPresent() {
    return super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE) != null;
  }

  @Override
  public int getRequestTypeAVPValue() {
    Avp requestTypeAvp = super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE);
    if (requestTypeAvp != null) {
      try {
        return requestTypeAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain CC-Request-Type AVP value", e);
      }
    }

    return -1;
  }

  public boolean isRequestedActionAVPPresent() {
    return super.message.getAvps().getAvp(REQUESTED_ACTION_AVP_CODE) != null;
  }

  public int getRequestedActionAVPValue() {
    Avp requestedActionAvp = super.message.getAvps().getAvp(REQUESTED_ACTION_AVP_CODE);
    if (requestedActionAvp != null) {
      try {
        return requestedActionAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Requested-Action AVP value", e);
      }
    }

    return -1;
  }
}
