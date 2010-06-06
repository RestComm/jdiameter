package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class PushNotificationAnswerImpl extends AppAnswerEventImpl implements PushNotificationAnswer {

  private static final long serialVersionUID = 1L;

  public PushNotificationAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  public PushNotificationAnswerImpl(Answer answer) {
    super(answer);
  }
}
