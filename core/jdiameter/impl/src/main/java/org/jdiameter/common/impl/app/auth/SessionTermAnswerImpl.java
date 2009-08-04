package org.jdiameter.common.impl.app.auth;

import org.jdiameter.api.Message;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class SessionTermAnswerImpl extends AppAnswerEventImpl implements SessionTermAnswer {

  private static final long serialVersionUID = 1L;

  public SessionTermAnswerImpl(Message message) {
    super(message);
  }
}
