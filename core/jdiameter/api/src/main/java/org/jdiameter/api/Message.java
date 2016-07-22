 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.api;

import java.io.Serializable;
import java.util.List;

/**
 * A Diameter message is either a request from a client to a server, or a response from a server to a client.
 * Both Request and Answer messages use the basic format of  RFC 3588
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * The message must support adaptable operation to Answer and Request interfaces
 * Serializable interface allows use this class in SLEE Event objects
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @version 1.5.1 Final
 */
public interface Message extends Wrapper, Serializable {

  /**
   * The Abort-Session-Request message code
   */
  int ABORT_SESSION_REQUEST = 274;

  /**
   * The Abort-Session-Answer message code
   */
  int ABORT_SESSION_ANSWER = 274;

  /**
   * The Accounting-Request message code
   */
  int ACCOUNTING_REQUEST = 271;

  /**
   * The Accounting-Answer message code
   */
  int ACCOUNTING_ANSWER = 271;

  /**
   * The Capabilities-Exchange-Request message code
   */
  int CAPABILITIES_EXCHANGE_REQUEST = 257;

  /**
   * The Capabilities-Exchange-Answer message code
   */
  int CAPABILITIES_EXCHANGE_ANSWER = 257;

  /**
   * The Device-Watchdog-Request message code
   */
  int DEVICE_WATCHDOG_REQUEST = 280;

  /**
   * The Device-Watchdog-Answer message code
   */
  int DEVICE_WATCHDOG_ANSWER = 280;

  /**
   * The Disconnect-Peer-Request message code
   */
  int DISCONNECT_PEER_REQUEST = 282;

  /**
   * The Disconnect-Peer-Answer message code
   */
  int DISCONNECT_PEER_ANSWER = 282;

  /**
   * The Re-Auth-Request message code
   */
  int RE_AUTH_REQUEST = 258;

  /**
   * The Re-Auth-Answer message code
   */
  int RE_AUTH_ANSWER = 258;

  /**
   * The Session-Termination-Request message code
   */
  int SESSION_TERMINATION_REQUEST = 275;

  /**
   * The Session-Termination-Answer message code
   */
  int SESSION_TERMINATION_ANSWER = 275;

  /**
   * @return version of message (version filed in header)
   */
  byte getVersion();

  /**
   * @return value of R bit from header of message
   */
  boolean isRequest();

  /**
   * Set 1 or 0 to R bit field of header
   * @param value true == 1 or false = 0
   */
  void setRequest(boolean value);

  /**
   * @return value of P bit from header of message
   */
  boolean isProxiable();

  /**
   * Set 1 or 0 to P bit field of header
   * @param value true == 1 or false = 0
   */
  void setProxiable(boolean value);

  /**
   * @return value of E bit from header of message
   */
  boolean isError();

  /**
   * Set 1 or 0 to E bit field of header
   * @param value true == 1 or false = 0
   */
  void setError(boolean value);

  /**
   * @return value of T bit from header of message
   */
  boolean isReTransmitted();

  /**
   * Set 1 or 0 to T bit field of header
   * @param value true == 1 or false = 0
   */
  void setReTransmitted(boolean value);

  /**
   * @return command code from header of message
   */
  int getCommandCode();

  /**
   * Return message Session Id avp Value (null if avp not set)
   * @return session id avp of message
   */
  String getSessionId();

  /**
   * Return ApplicationId value from message header
   * @return ApplicationId value from message header
   */
  long getApplicationId();

  /**
   * Returns ordered list of Application-Id avps (Auth-Application-Id, Acc-Appplication-Id and Vendor-Specific-Application-Id avps) from message
   * @return list of Application-Id avps
   */
  List<ApplicationId> getApplicationIdAvps();

  /**
   * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in
   * network byte order) and aids in matching requests and replies. The
   * sender MUST ensure that the Hop-by-Hop identifier in a request is
   * unique on a given connection at any given time, and MAY attempt to
   * ensure that the number is unique across reboots.
   * @return hop by hop identifier from header of message
   */
  long getHopByHopIdentifier();

  /**
   * The End-to-End Identifier is an unsigned 32-bit integer field (in
   * network byte order) and is used to detect duplicate messages. Upon
   * reboot implementations MAY set the high order 12 bits to contain
   * the low order 12 bits of current time, and the low order 20 bits
   * to a random value. Senders of request messages MUST insert a
   * unique identifier on each message.
   * @return end to end identifier from header of message
   */
  long getEndToEndIdentifier();

  /**
   * @return Set of message Avps
   */
  AvpSet getAvps();
}
