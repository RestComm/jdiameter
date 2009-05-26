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

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

/**
 * The ShClientProvider is used by a Diameter Sh Client (e.g., an AS) in an IMS network to create and send
 * requests to a Diameter Sh Server (e.g., an HSS).
 *
 * @author Open Cloud
 */
public interface ShClientProvider {
    /**
     * Get access to the Sh Diameter client message factory.
     *
     * @return client message factory for Sh Diameter
     */
    ShClientMessageFactory getClientMessageFactory();
    
    DiameterShAvpFactory getClientAvpFactory();

    /**
     * Create a new Sh client activity to send and receive Diameter Sh messages.
     * @throws CreateActivityException if the RA could not create the activity for any reason
     * @return an ShClientActivity 
     */
    ShClientActivity createShClientActivity() throws CreateActivityException;
    
    
    /**
     * Create a new Sh client activity to send and receive Diameter Sh messages.
     * @throws CreateActivityException if the RA could not create the activity for any reason
     * @return an ShClientActivity 
     */
    ShClientSubscriptionActivity createShClientSubscriptionActivity() throws CreateActivityException;
    
    /**
     * Sends a synchronous UserDataRequest which will block until an answer is received from the peer.
     *
     * @param message created using the MessageFactory
     * @return answer received from HSS
     * @throws IOException if there was a problem sending the request
     */
    UserDataAnswer userDataRequest(UserDataRequest message) throws IOException;

    /**
     * Sends a synchronous ProfileUpdateRequest which will block until an answer is received from the peer.
     *
     * @param message created using the MessageFactory
     * @return answer received from HSS
     * @throws IOException if there was a problem sending the request
     */
    ProfileUpdateAnswer profileUpdateRequest(ProfileUpdateRequest message) throws IOException;

    /**
     * Sends a synchronous SubscribeNotificationsRequest which will block until an answer is received from the peer.
     *
     * @param message created using the MessageFactory
     * @return answer received from HSS
     * @throws IOException if there was a problem sending the request
     */
    SubscribeNotificationsAnswer subscribeNotificationsRequest(SubscribeNotificationsRequest message) throws IOException;
    
    /**
	 * Return the number of peers this Diameter resource adaptor is connected
	 * to.
	 * 
	 * @return connected peer count
	 */
	int getPeerCount();

	/**
	 * Returns array containing identities of connected peers FIXME: baranowb; -
	 * should it be InetAddres, Port pair?
	 * 
	 * @return
	 */
	DiameterIdentity[] getConnectedPeers();
    
}
