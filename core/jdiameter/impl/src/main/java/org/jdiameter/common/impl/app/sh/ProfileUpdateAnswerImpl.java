package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class ProfileUpdateAnswerImpl extends AppAnswerEventImpl implements ProfileUpdateAnswer {

  private static final long serialVersionUID = 1L;

  public ProfileUpdateAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  public ProfileUpdateAnswerImpl(Answer answer) {
    super(answer);
  }

}
