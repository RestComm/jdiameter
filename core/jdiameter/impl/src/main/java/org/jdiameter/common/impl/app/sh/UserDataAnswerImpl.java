package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class UserDataAnswerImpl extends AppAnswerEventImpl implements UserDataAnswer {

  private static final long serialVersionUID = 1L;

  public UserDataAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  public UserDataAnswerImpl(Answer answer) {
    super(answer);
  }

}
