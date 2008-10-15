package org.jdiameter.common.api.app.auth;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;


public interface IAuthMessageFactory {

    public ApplicationId getApplicationId();

    public int getAuthMessageCommandCode();

    public AppRequestEvent createAuthRequest(Request request);

    public AppAnswerEvent createAuthAnswer(Answer answer);

}
