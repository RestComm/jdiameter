/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.sh.server;

import java.io.IOException;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;

/**
 * Activity used by a Diameter Sh Server for Notifications.  
 * 
 * The following message can be fired as an event:
 * <UL>
 * <LI>PushNotificationAnswer
 * </UL>
 * <p/>
 * The following request can be sent:
 * <UL>
 * <LI>PushNotificationRequest
* </UL>
 *
 * @author Open Cloud
 */
public interface ShServerNotificationActivity extends DiameterActivity{

   

    /**
     * Sends a push notification request asynchronously.
     *
     * @param message message to send
     * @throws IOException if there is an error sending the message
     */
    void sendPushNotificationRequest(PushNotificationRequest message) throws IOException;

    /**
     * Create a SubscribeNotificationsAnswer containing a Result-Code or Experimental-Result AVP populated with the given value.
     * If <code>isExperimentalResultCode</code> is <code>true</code>, the <code>resultCode</code> parameter will be set
     * in a {@link org.mobicents.slee.resource.diameter.base.types.ExperimentalResultAvp} AVP, if it is <code>false</code> the
     * result code will be set in a Result-Code AVP. 
     * @return a SubscribeNotificationsAnswer object that can be sent using {@link ShServerActivity#sendSubscribeNotificationsAnswer(net.java.slee.resource.diameter.sh.types.SubscribeNotificationsAnswer)}
     */
    SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(long resultCode, boolean isExperimentalResult);

    /**
     * Create an empty SubscribeNotificationsAnswer that will need to have AVPs set on it before being sent.
     * @return a SubscribeNotificationsAnswer object that can be sent using {@link ShServerActivity#sendSubscribeNotificationsAnswer(net.java.slee.resource.diameter.sh.types.SubscribeNotificationsAnswer)}
     */
    SubscribeNotificationsAnswer createSubscribeNotificationsAnswer();
    /**
     * Send the SubscribeNotificationsAnswer to the peer that sent the SubscribeNotificationsRequest.
     */
    void sendSubscribeNotificationsAnswer(SubscribeNotificationsAnswer message) throws IOException;
    
}
