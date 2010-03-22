package org.jdiameter.api.cca;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.cca.events.JCreditControlRequest;

public interface ServerCCASessionListener {

	void doCreditControlRequest(ServerCCASession session,
			JCreditControlRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException;

	void doReAuthAnswer(ServerCCASession session, ReAuthRequest request,
			ReAuthAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException;

	/**
	 * Notifies this ServerCCASessionListener that the ServerCCASession has
	 * recived not CCA message, now it can be even RAA.
	 * 
	 * @param session
	 *            parent application session (FSM)
	 * @param request
	 *            request object
	 * @param answer
	 *            answer object
	 * @throws InternalException
	 *             The InternalException signals that internal error is
	 *             occurred.
	 * @throws IllegalDiameterStateException
	 *             The IllegalStateException signals that session has incorrect
	 *             state (invalid).
	 * @throws RouteException
	 *             The NoRouteException signals that no route exist for a given
	 *             realm.
	 * @throws OverloadException
	 *             The OverloadException signals that destination host is
	 *             overloaded.
	 */
	void doOtherEvent(AppSession session, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException;

}
