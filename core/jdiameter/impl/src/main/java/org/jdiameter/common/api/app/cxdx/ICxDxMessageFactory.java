package org.jdiameter.common.api.app.cxdx;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;

public interface ICxDxMessageFactory {

	//I just wonder if we really need this?
	AppRequestEvent createLocationInfoRequest(Request request);
	AppRequestEvent createUserAuthorizationRequest(Request request);
	AppRequestEvent createServerAssignmentRequest(Request request);
	AppRequestEvent createRegistrationTerminationRequest(Request request);
	AppRequestEvent createMultimediaAuthRequest(Request request);
	AppEvent createPushProfileRequest(Request request);
	
	
	AppEvent createPushProfileAnswer(Answer answer);
	AppAnswerEvent createLocationInfoAnswer(Answer answer);
	AppAnswerEvent createUserAuthorizationAnswer(Answer answer);
	AppAnswerEvent createServerAssignmentAnswer(Answer answer);
	AppAnswerEvent createRegistrationTerminationAnswer(Answer answer);
	AppAnswerEvent createMultimediaAuthAnswer(Answer answer);
	long getApplicationId();
	/**
	 * @param request
	 * @return
	 */
	
}
