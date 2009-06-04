package net.java.slee.resource.diameter.sh.client;

import java.io.IOException;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

/**
 * Activity used by a Diameter Sh client to represent a subscription to changes in user data in an HSS.
 * Push-Notification-Request messages are fired on this activity as events of type org.jainslee.resources.diameter.sh.SubscribedPushNotificationRequest and Subscribe-Notifications-Answer messages are fired as events of type org.jainslee.resources.diameter.sh.SubscribeNotificationsAnswer.
 * 
 * This activity is created by a call to ShClientProvider.createShClientSubscriptionActivity().
 *  
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ShClientSubscriptionActivity extends DiameterActivity {

  /**
   * Return the User-Identity for the subscription in the HSS represented by this activity.
   * 
   * @return the User-Identity AVP sent in the initial Subscription-Notifications-Request passed to sendSubscribeNotificationsRequest(net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest).
   */
	public UserIdentityAvp getSubscribedUserIdendity();
	
	/**
	 * Send a Subscribe-Notifications-Request message. 
	 * 
	 * FIXME: Alexandre: This method name should be sendSubscribeNotificationsRequest!
	 * 
	 * @param request request message to send
	 * @throws IOException if the message could not be sent
	 */
	public void sendSubscriptionNotificationRequest(SubscribeNotificationsRequest request) throws IOException;
	
	/**
	 * Send a manually-constructed PushNotificationAnswer to the peer that sent the PushNotificationRequest. 
	 * 
	 * @param answer the message to be sent
	 * @throws IOException if the message could not be sent
	 */
	public void sendPushNotificationAnswer(PushNotificationAnswer answer) throws IOException;
	
	/**
	 * Convenience method to create and send a PushNotificationAnswer containing a Result-Code or Experimental-Result AVP populated with the given value.
	 * 
	 * @param resultCode
	 * @param isExperimentalResultCode
	 * @throws IOException if the message could not be sent
	 */
	public void sendPushNotificationAnswer(long resultCode,boolean isExperimentalResultCode) throws IOException;
	
	/**
	 * Send a Subscribe-Notifications-Request message containing the AVPs required to UNSUBSCRIBE from the user that this activity represents a subscription to. 
	 * 
	 * @throws IOException if the request message could not be sent
	 */
	void 	sendUnsubscribeRequest() throws IOException;
	
	/**
	 * Creates PUA for receive PNR. It returns null if there is not PNR received.
	 * @return
	 */
	public PushNotificationAnswer createPushNotificationAnswer();
	/**
	 * Creates PUA for receive PNR. It returns null if there is not PNR received.
	 * @param resultCode - result code to be added
	 * @param isExperimaental - true if result code is experimetnal result code 
	 * @return
	 */
	public PushNotificationAnswer createPushNotificationAnswer(long resultCode, boolean isExperimaental);
	
//	public ProfileUpdateRequest createProfileUpdateRequest();
//	public UserDataRequest createUserDataRequest();
	/**
	 * Send user data request.
	 */
	public void sendUserDataRequest(UserDataRequest message) throws IOException;
	/**
	 * Send profile update request.
	 * @param message
	 * @throws IOException
	 */
	public void sendProfileUpdateRequest(ProfileUpdateRequest message) throws IOException;
}
