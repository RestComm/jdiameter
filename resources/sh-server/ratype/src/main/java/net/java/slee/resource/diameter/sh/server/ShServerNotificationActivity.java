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

import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;

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
public interface ShServerNotificationActivity {

    /**
     * Get a message factory to create answer messages and AVPs (if necessary).
     */
    ShServerMessageFactory getServerMessageFactory();

    /**
     * Sends a push notification request asynchronously.
     *
     * @param message message to send
     * @throws IOException if there is an error sending the message
     */
    void sendPushNotificationRequest(PushNotificationRequest message) throws IOException;

}
