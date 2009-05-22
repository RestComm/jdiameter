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
 * 
 * AVP representation of Send-Data-Indication AVP. Defined in 3GPP TS 29.329
 * section 6.3.17.<br>
 * 
 * The Send-Data-Indication AVP is of type Enumerated. If present it indicates
 * that the sender requests the User-Data. The following values are defined:
 * <ul>
 * <li>USER_DATA_NOT_REQUESTED (0)</li>
 * <li>USER_DATA_REQUESTED (1)</li>
 * </ul>
 */
public class SendDataIndicationType implements Serializable, Enumerated {

	/**
	 * 
	 */
	private static final long serialVersionUID = 220559116111005191L;
	/**
	 * Value indicating that user data has not been requested by sender
	 */
	public static final int _USER_DATA_NOT_REQUESTED = 0;
	/**
	 * Value indicating that user data has been requested by sender
	 */
	public static final int _USER_DATA_REQUESTED = 1;

	/**
     * Static class representing 
     */
	public static final SendDataIndicationType USER_DATA_NOT_REQUESTED = new SendDataIndicationType(_USER_DATA_NOT_REQUESTED);

	/**
     * 
     */
	public static final SendDataIndicationType USER_DATA_REQUESTED = new SendDataIndicationType(_USER_DATA_REQUESTED);

	private SendDataIndicationType(int value) {
		this.value = value;
	}

	public static SendDataIndicationType fromInt(int type) {
		switch (type) {
		case _USER_DATA_NOT_REQUESTED:
			return USER_DATA_NOT_REQUESTED;
		case _USER_DATA_REQUESTED:
			return USER_DATA_REQUESTED;
		default:
			throw new IllegalArgumentException("Invalid SendDataIndication value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _USER_DATA_NOT_REQUESTED:
			return "USER_DATA_NOT_REQUESTED";
		case _USER_DATA_REQUESTED:
			return "USER_DATA_REQUESTED";
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
