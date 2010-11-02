/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

/**
 * A Diameter Request is a request from a client to a server (or server to client - network request).
 * @version 1.5.1 Final
 */

public interface Request extends Message {

  /**
   * @return true if it is network request
   */
  boolean isNetworkRequest();

  /**
   * Creates an answer for this request with the specified result code.
   * Header and system AVPs from request are copied to answer.
   * @param resultCode result code of answer
   * @return answer object instance
   */
  Answer createAnswer(long resultCode);

  /**
   * Creates an answer for this request with the specified experimental result code.
   * Header and system AVPs from request are copied to answer.
   * @param vendorId vendorId
   * @param experimentalResultCode experimental result code of answer
   * @return answer object instance
   */
  Answer createAnswer(long vendorId, long experementalResultCode);
}
