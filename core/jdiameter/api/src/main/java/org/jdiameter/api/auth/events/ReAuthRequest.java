/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.auth.events;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * A ReAuthentication Request is a request from a client to a server
 * 
 * @version 1.5.1 Final
 */

public interface ReAuthRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "RAR";
  public static final String _LONG_NAME = "Re-Auth-Request";

  public static final int code = 258;

  boolean hasAcctInterimInterval();

  long getAcctInterimInterval() throws AvpDataException;

  void setAcctInterimInterval(long val);

  boolean hasAccountingRealtimeRequired();

  int getAccountingRealtimeRequired() throws AvpDataException;

  void setAccountingRealtimeRequired(int val);

  long getOriginStateId() throws AvpDataException;

  boolean hasOriginStateId();

  void setOriginStateId(long val);

  /**
   * Return re-authentication request type
   * 
   * @return re-authentication request type
   * @throws org.jdiameter.api.AvpDataException
   *             if avp is not integer
   */
  int getReAuthRequestType() throws AvpDataException;

  boolean hasReAuthRequestType();

  void setReAuthRequestType(int val);

  /**
   * Return Auth-Application-Id value of request
   * 
   * @return Auth-Application-Id value of request
   * @throws org.jdiameter.api.AvpDataException
   *             if avp is not integer
   */
  long getAuthApplicationId() throws AvpDataException;

  boolean hasAuthApplicationId();

  void setAuthApplicationId(long val);

  boolean hasUserName();

  String getUserName() throws AvpDataException;

  void setUserName(String val);
}
