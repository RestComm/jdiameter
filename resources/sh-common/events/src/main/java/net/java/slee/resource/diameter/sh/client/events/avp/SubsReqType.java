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
 * AVP representation of Subs-Req AVP. Defined in 3GPP TS 29.329 section 6.3.7.<br>
 * The Subs-Req-Type AVP is of type Enumerated, and indicates the type of the
 * subscription-to-notifications request. The following values are defined:
 * <ul>
 * <li>Subscribe (0) - This value is used by an AS to subscribe to notifications
 * of changes in data.</li>
 * <li>Unsubscribe (1) - This value is used by an AS to unsubscribe to
 * notifications of changes in data.</li>
 * </ul>
 */

public class SubsReqType implements Serializable, Enumerated {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7748012167965997571L;
	/**
	 * Int value equal in diameter message - it indicates subscription request - subscirbe to updates - see TS29.328 for description
	 */
	public static final int _SUBSCRIBE = 0;
	/**
	 * Int value equal in diameter message - it indicates subscription request - unsubscirbe to updates - see TS29.328 for description
	 */
	public static final int _UNSUBSCRIBE = 1;

	/**
     * Singleton representation of {@link _SUBSCRIBE}
     */
	public static final SubsReqType SUBSCRIBE = new SubsReqType(_SUBSCRIBE);

	/**
     * Singleton representation of {@link _UNSUBSCRIBE}
     */
	public static final SubsReqType UNSUBSCRIBE = new SubsReqType(_UNSUBSCRIBE);

	private SubsReqType(int value) {
		this.value = value;
	}

	public static SubsReqType fromInt(int type) {
		switch (type) {
		case _SUBSCRIBE:
			return SUBSCRIBE;
		case _UNSUBSCRIBE:
			return UNSUBSCRIBE;
		default:
			throw new IllegalArgumentException("Invalid SubsReqType value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _SUBSCRIBE:
			return "SUBSCRIBE";
		case _UNSUBSCRIBE:
			return "UNSUBSCRIBE";
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
