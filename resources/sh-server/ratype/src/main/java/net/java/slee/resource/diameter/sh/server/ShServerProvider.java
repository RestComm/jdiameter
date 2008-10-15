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

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;

/**
 * The ShServerProvider can be used by a SLEE service to provide HSS functions on an IMS network.
 *
 * @author Open Cloud
 */
public interface ShServerProvider {

    /**
     * Provides the ServerMessageFactory
     *
     * @return ServerMesageFactory
     */
    ShServerMessageFactory getServerMessageFactory();

    /**
     * Create a new Sh server activity to send and receive Diameter Sh messages.
     * @return an ShServerNotificationActivity 
     * @throws CreateActivityException if the RA could not create the activity for any reason
     */
    ShServerNotificationActivity createShServerNotifiicationActivity() throws CreateActivityException;

    /**
     * Sends a synchronous PushNotificationRequest which will block until an answer is received from the peer.
     *
     * @param message a PushNotificationRequest created by the ServerMessageFactory
     * @return Push-Notification-Answer message from AS
     * @throws IOException if there is an error sending the message
     */
    PushNotificationAnswer pushNotificationRequest(PushNotificationRequest message) throws IOException;
}
