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
 * Java class to represent the AccountingRecordType enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Accounting-Record-Type AVP (AVP Code 480) is of type Enumerated and contains the type of accounting record being sent. The following values are currently defined for the Accounting-Record-Type AVP: 
 *
 * @author Open Cloud
 */
public class AccountingRecordType implements Serializable, Enumerated {

  private static final long serialVersionUID = 1L;

  public static final int _EVENT_RECORD = 1;
  public static final int _START_RECORD = 2;
  public static final int _INTERIM_RECORD = 3;
  public static final int _STOP_RECORD = 4;

  /**
   * An Accounting Event Record is used to indicate that a one-time event has occurred (meaning that the start and end of the event are simultaneous). This record contains all information relevant to the service, and is the only record of the service. 
   */
  public static final AccountingRecordType EVENT_RECORD = new AccountingRecordType(_EVENT_RECORD);

  /**
   * An Accounting Start, Interim, and Stop Records are used to indicate that a service of a measurable length has been given. An Accounting Start Record is used to initiate an accounting session, and contains accounting information that is relevant to the initiation of the session. 
   */
  public static final AccountingRecordType START_RECORD = new AccountingRecordType(_START_RECORD);

  /**
   * An Interim Accounting Record contains cumulative accounting information for an existing accounting session. Interim Accounting Records SHOULD be sent every time a re-authentication or re-authorization occurs. Further, additional interim record triggers MAY be defined by application-specific Diameter applications. The selection of whether to use INTERIM_RECORD records is done by the Acct-Interim-Interval AVP. 
   */
  public static final AccountingRecordType INTERIM_RECORD = new AccountingRecordType(_INTERIM_RECORD);

  /**
   * An Accounting Stop Record is sent to terminate an accounting session and contains cumulative accounting information relevant to the existing session. 
   */
  public static final AccountingRecordType STOP_RECORD = new AccountingRecordType(_STOP_RECORD);

  private AccountingRecordType(int value) {
    this.value = value;
  }

  public static AccountingRecordType fromInt(int type) {
    switch(type) {
    case _EVENT_RECORD: return EVENT_RECORD;
    case _START_RECORD: return START_RECORD;
    case _INTERIM_RECORD: return INTERIM_RECORD;
    case _STOP_RECORD: return STOP_RECORD;
    default: throw new IllegalArgumentException("Invalid AccountingRecordType value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _EVENT_RECORD: return "EVENT_RECORD";
    case _START_RECORD: return "START_RECORD";
    case _INTERIM_RECORD: return "INTERIM_RECORD";
    case _STOP_RECORD: return "STOP_RECORD";
    default: return "<Invalid Value>";
    }
  }

  private Object readResolve() throws StreamCorruptedException {
    try {
      return fromInt(value);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: " + value);
    }
  }

  private int value;
}
