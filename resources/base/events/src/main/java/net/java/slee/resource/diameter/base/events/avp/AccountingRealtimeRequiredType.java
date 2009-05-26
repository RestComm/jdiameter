/*
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
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

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Java class to represent the AccountingRealtimeRequired enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Accounting-Realtime-Required AVP (AVP Code 483) is of type Enumerated and
 * is sent from the Diameter home authorization server to the Diameter client or
 * in the Accounting-Answer from the accounting server. The client uses
 * information in this AVP to decide what to do if the sending of accounting
 * records to the accounting server has been temporarily prevented due to, for
 * instance, a network problem.
 * 
 * @author Open Cloud
 */

public class AccountingRealtimeRequiredType implements Serializable, Enumerated {

  private static final long serialVersionUID = 1L;
  
  public static final int _DELIVER_AND_GRANT = 1;
	public static final int _GRANT_AND_STORE = 2;
	public static final int _GRANT_AND_LOSE = 3;

	/**
	 * The AVP with Value field set to DELIVER_AND_GRANT means that the service
	 * MUST only be granted as long as there is a connection to an accounting
	 * server. Note that the set of alternative accounting servers are treated
	 * as one server in this sense. Having to move the accounting record stream
	 * to a backup server is not a reason to discontinue the service to the
	 * user.
	 */
	public static final AccountingRealtimeRequiredType DELIVER_AND_GRANT = new AccountingRealtimeRequiredType(_DELIVER_AND_GRANT);

	/**
	 * The AVP with Value field set to GRANT_AND_STORE means that service SHOULD
	 * be granted if there is a connection, or as long as records can still be
	 * stored as described in Section 9.4. This is the default behavior if the
	 * AVP isn't included in the reply from the authorization server.
	 */
	public static final AccountingRealtimeRequiredType GRANT_AND_STORE = new AccountingRealtimeRequiredType(_GRANT_AND_STORE);

	/**
	 * The AVP with Value field set to GRANT_AND_LOSE means that service SHOULD
	 * be granted even if the records can not be delivered or stored.
	 */
	public static final AccountingRealtimeRequiredType GRANT_AND_LOSE = new AccountingRealtimeRequiredType(_GRANT_AND_LOSE);

	private AccountingRealtimeRequiredType(int value) {
		this.value = value;
	}

	public static AccountingRealtimeRequiredType fromInt(int type) {
		switch (type) {
		case _DELIVER_AND_GRANT:
			return DELIVER_AND_GRANT;
		case _GRANT_AND_STORE:
			return GRANT_AND_STORE;
		case _GRANT_AND_LOSE:
			return GRANT_AND_LOSE;
		default:
			throw new IllegalArgumentException("Invalid AccountingRealtimeRequired value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _DELIVER_AND_GRANT:
			return "DELIVER_AND_GRANT";
		case _GRANT_AND_STORE:
			return "GRANT_AND_STORE";
		case _GRANT_AND_LOSE:
			return "GRANT_AND_LOSE";
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
