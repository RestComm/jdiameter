package org.jdiameter.common.api.app.s13;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;

public interface IS13MessageFactory {
	JMEIdentityCheckRequest  createMEIdentityCheckRequest(Request request);
	JMEIdentityCheckAnswer  createMEIdentityCheckAnswer(Answer answer);
	
	/**
	* Returns the Application-Id that this message factory is related to
	* @return the Application-Id value
	*/
	long getApplicationId();
}
