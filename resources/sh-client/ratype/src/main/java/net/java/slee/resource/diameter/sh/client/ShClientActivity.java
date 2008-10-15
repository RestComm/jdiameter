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
package net.java.slee.resource.diameter.sh.client;


import java.io.IOException;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

/**
 
 * These are stateless requests (the Diameter server does not maintain any state associated with these requests) 
 * so the activity ends when the answer event is fired.
 * 
 */
public interface ShClientActivity extends DiameterActivity{

    /**
     * Send a User-Data-Request message asynchronously.
     *
     * @param message request message to send
     * @throws IOException if the request message could not be sent
     */
    void sendUserDataRequest(UserDataRequest message) throws IOException;

    /**
     * Send a Profile-Update-Request message asynchronously.
     *
     * @param message request message to send
     * @throws IOException if the request message could not be sent
     */
    void sendProfileUpdateRequest(ProfileUpdateRequest message) throws IOException;


}
