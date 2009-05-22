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
 * AVP representation of Current-Location AVP. Defined in 3GPP TS 29.329 section
 * 6.3.8.<br>
 * The Current-Location AVP is of type Enumerated, and indicates whether an
 * active location retrieval has to be initiated or not: 
 * <pre>
 *      <b>DoNotNeedInitiateActiveLocationRetrieval (0)</b> : 
 *          The request indicates that the initiation of an active location retrieval is not required. 
 *      <b>InitiateActiveLocationRetrieval (1)</b> : 
 *          It is requested that an active location retrieval is initiated.
 * </pre>
 * 
 */

public class CurrentLocationType implements Serializable, Enumerated {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1583473527497727782L;
	public static final int _DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL = 0;
	public static final int _INITIATE_ACTIVE_LOCATION_RETRIEVAL = 1;

	/**
	 * The request indicates that the initiation of an active location retrieval
	 * is not required.
	 */
	public static final CurrentLocationType DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL = new CurrentLocationType(_DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL);

	/**
	 * It is requested that an active location retrieval is initiated.
	 */
	public static final CurrentLocationType INITIATE_ACTIVE_LOCATION_RETRIEVAL = new CurrentLocationType(_INITIATE_ACTIVE_LOCATION_RETRIEVAL);

	private CurrentLocationType(int value) {
		this.value = value;
	}

	public static CurrentLocationType fromInt(int type) {
		switch (type) {
		case _DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL:
			return DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL;
		case _INITIATE_ACTIVE_LOCATION_RETRIEVAL:
			return INITIATE_ACTIVE_LOCATION_RETRIEVAL;
		default:
			throw new IllegalArgumentException("Invalid CurrentLocation value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL:
			return "DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL";
		case _INITIATE_ACTIVE_LOCATION_RETRIEVAL:
			return "INITIATE_ACTIVE_LOCATION_RETRIEVAL";
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
