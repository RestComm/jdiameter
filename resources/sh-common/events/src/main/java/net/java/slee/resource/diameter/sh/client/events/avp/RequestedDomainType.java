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
package net.java.slee.resource.diameter.sh.client.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * AVP representation of Requested-Domain AVP. Defined in 3GPP TS 29.329 section
 * 6.3.7.<br>
 * The Requested-Domain AVP is of type Enumerated, and indicates the access
 * domain for which certain data (e.g. user state) are requested. The following
 * values are defined:
 * <ul>
 * <li>CS-Domain (0) - The requested data apply to the CS domain.</li>
 * <li>PS-Domain (1) - The requested data apply to the PS domain.</li>
 * </ul>
 */

public class RequestedDomainType implements Serializable, Enumerated {


	/**
	 * Int value equal in diameter message - it indicates domain for which certain data has been requested - CS - see TS29.328 for description
	 */
	public static final int _CS_DOMAIN = 0;
	/**
	 * Int value equal in diameter message - it indicates domain for which certain data has been requested - PS - see TS29.328 for description
	 */
	public static final int _PS_DOMAIN = 1;

	/**
     * Singleton representation of {@link _CS_DOMAIN}
     */
	public static final RequestedDomainType CS_DOMAIN = new RequestedDomainType(_CS_DOMAIN);

	/**
     * Singleton representation of {@link _PS_DOMAIN}
     */
	public static final RequestedDomainType PS_DOMAIN = new RequestedDomainType(_PS_DOMAIN);

	private RequestedDomainType(int value) {
		this.value = value;
	}

	public static RequestedDomainType fromInt(int type) {
		switch (type) {
		case _CS_DOMAIN:
			return CS_DOMAIN;
		case _PS_DOMAIN:
			return PS_DOMAIN;
		default:
			throw new IllegalArgumentException("Invalid RequestedDomain value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _CS_DOMAIN:
			return "CS_DOMAIN";
		case _PS_DOMAIN:
			return "PS_DOMAIN";
		default:
			return "<Invalid Value>";
		}
	}

	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: " + value);
		}
	}

	private int value;
}
