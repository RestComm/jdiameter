package org.jdiameter.common.impl.app.auth;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Request;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class AbortSessionAnswerImpl extends AppAnswerEventImpl implements AbortSessionAnswer {

  private static final long serialVersionUID = 1L;

  public AbortSessionAnswerImpl(Request request, int authRequestType, long resultCode) {
    super(request.createAnswer(resultCode));
    try {
      getMessage().getAvps().addAvp(Avp.AUTH_REQUEST_TYPE, authRequestType);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public AbortSessionAnswerImpl(Answer message) {
    super(message);
  }
}
