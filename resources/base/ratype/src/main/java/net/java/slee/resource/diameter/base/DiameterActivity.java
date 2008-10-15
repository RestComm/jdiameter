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
package net.java.slee.resource.diameter.base;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.DiameterMessage;

/**
 * /**
 * Represents a session with a Diameter peer.  
 * DiameterMessages (both requests and responses) are received as events fired on DiameterActivity objects.
 * 
 * @author baranowb
 */
public interface DiameterActivity {

    /**
     * Return a DiameterMessageFactory implementation to be used to create
     * instances to of {@link DiameterMessage} object to be fired on this Activity.
     * <br> returned type depends on implementation
     * @return a DiameterMessageFactory implementation
     */
    Object getDiameterMessageFactory();
    /**
     * Returns a DiameterAvp factory which can be used to create instances of avps.
     * <br> Return type depends on implementing object.
     * @return
     */
    Object getDiameterAvpFactory();
    
    /**
     * Sends the given DiameterMessage on the DiameterActivity.
     * The response to the message (if any) will be fired on this activity.
     * @param message the Diameter message to send
     */
    void sendMessage(DiameterMessage message) throws IOException;

    /**
     * Return the Session ID for this activity.
     * @return the Session ID for this activity
     */
    String getSessionId();
    /**
     * Terminates underlying session
     */
    void endActivity();
    
    
}
