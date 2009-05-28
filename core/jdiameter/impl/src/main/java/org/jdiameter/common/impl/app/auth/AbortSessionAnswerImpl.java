package org.jdiameter.common.impl.app.auth;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class AbortSessionAnswerImpl extends AppAnswerEventImpl implements AbortSessionAnswer {

    public AbortSessionAnswerImpl(Request request, int authRequestType, long resultCode) {
        super(request.createAnswer(resultCode));
        try {
            getMessage().getAvps().addAvp(Avp.AUTH_REQUEST_TYPE, authRequestType);
        }
        catch (Exception exc) {
            throw new IllegalArgumentException(exc);
        }
    }

    public AbortSessionAnswerImpl(Answer message) {
        super(message);
    }



}
