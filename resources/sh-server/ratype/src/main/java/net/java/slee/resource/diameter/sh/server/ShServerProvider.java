/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package net.java.slee.resource.diameter.sh.server;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;

/**
 * The ShServerProvider can be used by a SLEE service to provide HSS functions on an IMS network.
 *
 */
public interface ShServerProvider {

    /**
     * Provides the ServerMessageFactory
     *
     * @return ServerMesageFactory
     */
    ShServerMessageFactory getServerMessageFactory();
    
    DiameterShAvpFactory getAvpFactory();
    
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

    /**
     * Create a new Sh server activity to send and receive Diameter Sh messages.
     * @return an ShServerSubscriptionActivity 
     * @throws CreateActivityException if the RA could not create the activity for any reason
     */
    //ShServerSubscriptionActivity createShServerSubscriptionActivity() throws CreateActivityException;
    
    /**
     * Create a new Sh server activity to send and receive Diameter Sh messages.
     * @return an ShServerSubscriptionActivity 
     * @throws CreateActivityException if the RA could not create the activity for any reason
     */
    //ShServerActivity createShServerActivity() throws CreateActivityException;

    /**
     * Sends a synchronous PushNotificationRequest which will block until an answer is received from the peer.
     *
     * @param message a PushNotificationRequest created by the ServerMessageFactory
     * @return Push-Notification-Answer message from AS
     * @throws IOException if there is an error sending the message
     */
    PushNotificationAnswer pushNotificationRequest(PushNotificationRequest message) throws IOException;
}
