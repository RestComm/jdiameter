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
package net.java.slee.resource.diameter.base.events.avp;

/**
 * Java class to represent the DiameterIdentity AVP type.
 * <p/>
 * The DiameterIdentity format is derived from the OctetString AVP Base Format.
 * <p/>
 * DiameterIdentity  = FQDN
 * <p/>
 * DiameterIdentity value is used to uniquely identify a Diameter
 * node for purposes of duplicate connection and routing loop
 * detection.
 * <p/>
 * The contents of the string MUST be the FQDN of the Diameter node.
 * If multiple Diameter nodes run on the same host, each Diameter
 * node MUST be assigned a unique DiameterIdentity.  If a Diameter
 * node can be identified by several FQDNs, a single FQDN should be
 * picked at startup, and used as the only DiameterIdentity for that
 * node, whatever the connection it is sent on.
 *
 *
 */

public interface DiameterIdentityAvp extends DiameterAvp{

	//Lets leave this as type safe set for setters, we wont have to worry
	
}
