/*
 * Mobicents, Communications Middleware, Diameter Base
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
package net.java.slee.resource.diameter.base;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

import java.io.IOException;

/**
 * The interface used by an SBB to interact with the Diameter RA.
 * @author baranowb
 * @author alexandrem
 */
public interface DiameterProvider {

	/**
	 * Return a DiameterMessageFactory implementation to be used to create
	 * {@link org.mobicents.slee.resource.diameter.base.DiameterMessage} objects.
	 * 
	 * @return a DiameterMessageFactory implementation
	 */
	DiameterMessageFactory getDiameterMessageFactory();

	/**
	 * Returns avp factory for base avp types and common types, like UNSIGNED_32
	 * type and similar.
	 * 
	 * @return
	 */
	DiameterAvpFactory getDiameterAvpFactory();

	/**
	 * Create a new activity to send and receive Diameter messages.
	 * 
	 * @return a DiameterActivity
	 * @throws CreateActivityException
	 *             if the RA could not create the activity for any reason
	 */
	DiameterActivity createActivity() throws CreateActivityException;

	/**
	 * Create a new activity to send and receive Diameter messages.
	 * 
	 * @param destinationHost
	 *            a destination host to automatically put in all messages
	 * @param destinationRealm
	 *            a destination realm to automatically put in all messages
	 * @return a DiameterActivity
	 * @throws CreateActivityException
	 *             if the RA could not create the activity for any reason
	 */
	DiameterActivity createActivity(DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm)
			throws CreateActivityException;

	AccountingClientSessionActivity createAccountingActivity(
			DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm)throws CreateActivityException ;

	AuthClientSessionActivity createAuthenticationActivity(
			DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm)throws CreateActivityException ;

	AccountingClientSessionActivity createAccountingActivity()throws CreateActivityException ;

	AuthClientSessionActivity createAuthenticationActivity()throws CreateActivityException ;

	/**
	 * Synchronously send a Diameter request and block until a response is
	 * received.
	 * 
	 * @param message
	 *            the Diameter message to send
	 * @return the Diameter message containing the response
	 */
	DiameterMessage sendSyncRequest(DiameterMessage message) throws IOException;

	/**
	 * Return the number of peers this Diameter resource adaptor is connected
	 * to.
	 * 
	 * @return connected peer count
	 */
	int getPeerCount();

	/**
	 * Returns array containing identities of connected peers 
	 * 
	 * @return
	 */
	DiameterIdentityAvp[] getConnectedPeers();
}
