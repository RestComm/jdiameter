package org.jdiameter.common.impl.app.auth;

import org.jdiameter.api.Message;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class SessionTermAnswerImpl extends AppAnswerEventImpl implements SessionTermAnswer {

    public static int code = 275;

    public SessionTermAnswerImpl(Message message) {
        super(message);
    }
}
