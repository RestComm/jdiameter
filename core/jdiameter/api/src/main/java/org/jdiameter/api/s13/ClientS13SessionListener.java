package org.jdiameter.api.s13;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;

public interface ClientS13SessionListener {
	
	void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	void doMEIdentityCheckAnswerEvent(ClientS13Session session, JMEIdentityCheckRequest request, JMEIdentityCheckAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
