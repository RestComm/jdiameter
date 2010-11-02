/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.acc.events;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * An Account Request is a request from a client to a server
 * 
 * @version 1.5.1 Final
 */
public interface AccountRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "ACR";
  public static final String _LONG_NAME = "Accounting-Request";

  public static final int code = 271;

  boolean hasAccountingRecordType();

  int getAccountingRecordType() throws AvpDataException;

  void setAccountingRecordType(int val);

  boolean hasAccountingRecordNumber();

  long getAccountingRecordNumber() throws AvpDataException;

  void setAccountingRecordNumber(long val);

  boolean hasAcctApplicationId();

  long getAcctApplicationId() throws AvpDataException;

  void setAcctApplicationId(long val);

  boolean hasVendorSpecificApplicationId();

  AvpSet getVendorSpecificApplicationId() throws AvpDataException;

  void setVendorSpecificApplicationId(long vendorId, long appId, boolean isAuthApp);

  boolean hasUserName();

  String getUserName() throws AvpDataException;

  void setUserName(String val);

  boolean hasAcctSubSessionId();

  long getAcctSubSessionId() throws AvpDataException;

  void setAcctSubSessionId(long val);

  boolean hasAcctSessionId();

  String getAcctSessionId() throws AvpDataException;

  void setAcctSessionId(String val);

  boolean hasAcctMultiSessionId();

  String getAcctMultiSessionId() throws AvpDataException;

  void setAcctMultiSessionId(String val);

  boolean hasAcctInterimInterval();

  long getAcctInterimInterval() throws AvpDataException;

  void setAcctInterimInterval(long val);

  boolean hasAccountingRealtimeRequired();

  int getAccountingRealtimeRequired() throws AvpDataException;

  void setAccountingRealtimeRequired(int val);

  long getOriginStateId() throws AvpDataException;

  boolean hasOriginStateId();

  void setOriginStateId(long val);

  // octet string
  boolean hasEventTimestamp();

  String getEventTimestamp() throws AvpDataException;

  void setEventTimestamp(String val);

}
