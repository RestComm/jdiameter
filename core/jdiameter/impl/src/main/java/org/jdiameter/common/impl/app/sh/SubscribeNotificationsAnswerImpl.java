package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class SubscribeNotificationsAnswerImpl extends AppAnswerEventImpl implements SubscribeNotificationsAnswer {

  private static final long serialVersionUID = 1L;

  public SubscribeNotificationsAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  public SubscribeNotificationsAnswerImpl(Answer answer) {
    super(answer);
  }

}
