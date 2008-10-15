package net.java.slee.resource.diameter.sh.client;

import java.io.IOException;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;

public interface ShClientSubscriptionActivity extends DiameterActivity{

	public GroupedAvp getSubscribedUserIdendity();
	public void sendSubscriptionNotificationRequest(SubscribeNotificationsRequest request) throws IOException;
	public void sendPushNotificationAnswer(PushNotificationAnswer answer) throws IOException;
	public void sendPushNotificationAnswer(long resultCode,boolean isExperimentalResultCode) throws IOException;
	void 	sendUnsubscribeRequest() throws IOException;
	
}
