package org.jdiameter.common.impl.app.auth;

import org.jdiameter.api.Message;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

public class ReAuthAnswerImpl extends AppAnswerEventImpl implements ReAuthAnswer {

 

    public ReAuthAnswerImpl(Message message) {
        super(message);
    }

}
