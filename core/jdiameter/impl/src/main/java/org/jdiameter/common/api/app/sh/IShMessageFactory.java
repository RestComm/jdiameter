package org.jdiameter.common.api.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;

public interface IShMessageFactory {

	
	AppRequestEvent createProfileUpdateRequest(Request request);
	AppRequestEvent createPushNotificationRequest(Request request);
	AppRequestEvent createSubscribeNotificationsRequest(Request request);
	AppRequestEvent createUserDataRequest(Request request);
	
	
	AppAnswerEvent createProfileUpdateAnswer(Answer answer);
	AppAnswerEvent createPushNotificationAnswer(Answer answer);
	AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer);
	AppAnswerEvent createUserDataAnswer(Answer answer);
	
	long getApplicationId();
	long getMessageTimeout();
}
