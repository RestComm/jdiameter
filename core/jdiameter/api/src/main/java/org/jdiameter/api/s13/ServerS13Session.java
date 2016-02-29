package org.jdiameter.api.s13;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;

public interface ServerS13Session extends AppSession, StateMachine {
	/**
	* Send ME-Identity-Check-Answer to client
	* 
	* @param answer ME-Identity-Check-Answer event instance
	* @throws InternalException The InternalException signals that internal error is occurred.
	* @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
	* @throws RouteException The NoRouteException signals that no route exist for a given realm.
	* @throws OverloadException The OverloadException signals that destination host is overloaded.
	*/
	void sendMEIdentityCheckAnswer(JMEIdentityCheckAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
