package org.jdiameter.common.impl.app.cca;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * JCreditControlAnswerImpl.java
 *
 * <br>Super project:  mobicents
 * <br>5:04:44 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class JCreditControlAnswerImpl extends AppAnswerEventImpl implements JCreditControlAnswer {

  private static final long serialVersionUID = 1L;

  protected Logger logger = LoggerFactory.getLogger(JCreditControlAnswerImpl.class);

  private static final int CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE = 427; 
  private static final int DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE = 428;
  private static final int REQUESTED_ACTION_AVP_CODE = 436;
  private static final int CC_REQUEST_TYPE_AVP_CODE = 416;
  private static final int VALIDITY_TIME_AVP_CODE = 448;

  public JCreditControlAnswerImpl(Request message, long resultCode) {
    super(message.createAnswer(resultCode));
  }

  public JCreditControlAnswerImpl(Answer message )
  {
    super(message);
  }

  public int getCredidControlFailureHandlingAVPValue()
  {
    if(isCreditControlFailureHandlingAVPPresent()) {
      try {
        return super.message.getAvps().getAvp(CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE).getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Credit-Control-Failure-Handling AVP value", e);
      }
    }

    return -1;
  }

  public int getDirectDebitingFailureHandlingAVPValue()
  {
    if(isDirectDebitingFailureHandlingAVPPresent()) {
      try {
        return super.message.getAvps().getAvp(DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE).getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Direct-Debiting-Failure-Handling AVP value", e);
      }
    }

    return -1;
  }

  public boolean isCreditControlFailureHandlingAVPPresent() {
    return super.message.getAvps().getAvp(CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE) != null;
  }

  public boolean isDirectDebitingFailureHandlingAVPPresent() {
    return super.message.getAvps().getAvp(DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE) != null;
  }

  public int getRequestedActionAVPValue() {
    if(isRequestedActionAVPPresent()) {
      try {
        return super.message.getAvps().getAvp(REQUESTED_ACTION_AVP_CODE).getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Requested-Action AVP value", e);
      }
    }

    return -1;
  }

  public boolean isRequestedActionAVPPresent() {
    return super.message.getAvps().getAvp(REQUESTED_ACTION_AVP_CODE) != null;
  }

  public int getRequestTypeAVPValue() {
    if(isRequestTypeAVPPresent()) {
      try {
        return super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE).getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain CC-Request-Type AVP value", e);
      }
    }

    return -1;
  }

  public Avp getValidityTimeAvp() {
    return super.message.getAvps().getAvp(VALIDITY_TIME_AVP_CODE);
  }

  public boolean isRequestTypeAVPPresent() {
    return super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE) != null;
  }
}
