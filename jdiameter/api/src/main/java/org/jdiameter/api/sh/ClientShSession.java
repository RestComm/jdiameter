package org.jdiameter.api.sh;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataRequest;

public interface ClientShSession extends AppSession, StateMachine{
	/**
     * Send profile update request to server
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendProfileUpdateRequest(ProfileUpdateRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	/**
     * Send sobscirption request to server
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendSubscribeNotificationsRequest(SubscribeNotificationsRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	/**
     * Send user data request to server
     * @param request Authentication-Request event instance
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     **/
	public void sendUserDataRequest(UserDataRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException; 
	
	public void sendPushNotificationAnswer(PushNotificationAnswer answer)  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
