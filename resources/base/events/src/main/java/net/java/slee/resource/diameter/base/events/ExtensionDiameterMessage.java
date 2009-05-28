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
package net.java.slee.resource.diameter.base.events;

/**
 * Defines an interface representing the Extension-Diameter-Message command.
 * 
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * 
 * <pre>
 * 9.7.0.  Extension-Diameter-Message
 * 
 *     An implementation of DiameterMessage for extension messages--those not defined by the
 *     Diameter RA being used.
 * 
 *     It follows the same pattern as the standard message types, but with the DiameterCommand supplied
 *     by the user.
 * 
 *     The AVPs are exposed as the set of 'extension AVP's', the same way as exposed for messages
 *     which define a &quot;* [ AVP ]&quot; line in the BNF definition of the message.
 * 
 *     Message Format
 * 
 *       &lt;Extension-Diameter-Message&gt; ::= &lt; Diameter Header: 0, PXY &gt;
 *                  &lt; Session-Id &gt;
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  { Destination-Host }
 *                  { Destination-Realm }
 *                * [ AVP ]
 * </pre>
 */
public interface ExtensionDiameterMessage extends DiameterMessage {

	// FIXME: baranowb - get code
	int commandCode = -2;

	/**
	 * Returns true if the Destination-Realm AVP is present in the message.
	 */
	boolean hasDestinationRealm();

	/**
	 * Returns true if the Destination-Host AVP is present in the message.
	 */
	boolean hasDestinationHost();
}
