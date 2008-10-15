package org.jdiameter.api.sh;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.UserDataAnswer;

public interface ServerShSession extends AppSession, StateMachine{
	
	
	/**
     * Send profile update notification request to client
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendPushNotificationRequest(PushNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	/**
     * Send profile update answer to client
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendProfileUpdateAnswer(ProfileUpdateAnswer answer )  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	/**
     * Send subscribe notification answer to client
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendSubscribeNotificationsAnswer(SubscribeNotificationsAnswer answer)  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	/**
     * Send user data answer to client
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendUserDataAnswer(UserDataAnswer answer)  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
