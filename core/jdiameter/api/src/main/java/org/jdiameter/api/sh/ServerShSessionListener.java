package org.jdiameter.api.sh;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;

public interface ServerShSessionListener {

	public void doSubscribeNotificationsRequestEvent(ServerShSession session, SubscribeNotificationsRequest request)throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	public void doProfileUpdateRequestEvent(ServerShSession session, ProfileUpdateRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	public void doPushNotificationAnswerEvent(ServerShSession session, PushNotificationRequest request, PushNotificationAnswer answer)throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	public void doUserDataRequestEvent(ServerShSession session,UserDataRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	
	/**
     * Notifies this ShSessionEventListener that the ServerShSession has recived not Sh message.
     * @param session parent application session (FSM)
     * @param request request object
     * @param answer answer object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	
	
	
}
