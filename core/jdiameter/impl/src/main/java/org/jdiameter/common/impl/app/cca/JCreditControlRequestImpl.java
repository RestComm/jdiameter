package org.jdiameter.common.impl.app.cca;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * JCreditControlRequestImpl.java
 *
 * <br>Super project:  mobicents
 * <br>5:07:16 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class JCreditControlRequestImpl extends AppRequestEventImpl implements JCreditControlRequest {

  private static final long serialVersionUID = 1L;

  protected Logger logger = LoggerFactory.getLogger(JCreditControlRequestImpl.class);

  private static final int REQUESTED_ACTION_AVP_CODE = 436;
  private static final int CC_REQUEST_TYPE_AVP_CODE = 416;

  public JCreditControlRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

  public JCreditControlRequestImpl(Request request) {
    super(request);
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

  public boolean isRequestTypeAVPPresent() {
    return super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE) != null;
  }
}
