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

/**
 * This interface describe basic result codes
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @version 1.5.1 Final
 */
public interface ResultCode {

  /**
   * This informational error is returned by a Diameter server to
   * inform the access device that the authentication mechanism being
   * used requires multiple round trips, and a subsequent request needs
   * to be issued in order for access to be granted.
   */
  int MULTI_ROUND_AUTH = 1001;

  /**
   * The Request was successfully completed.
   */
  int SUCCESS = 2001;

  /**
   * When returned, the request was successfully completed, but
   * additional processing is required by the application in order to
   * provide service to the user.
   */
  int LIMITED_SUCCESS = 2002;

  /**
   * The Request contained a Command-Code that the receiver did not
   * recognize or support.  This MUST be used when a Diameter node
   * receives an experimental command that it does not understand.
   */
  int COMMAND_UNSUPPORTED = 3001;

  /**
   * This error is given when Diameter can not deliver the message to
   * the destination, either because no host within the realm
   * supporting the required application was available to process the
   * request, or because Destination-Host AVP was given without the
   * associated Destination-Realm AVP
   */
  int UNABLE_TO_DELIVER = 3002;

  /**
   * The intended realm of the request is not recognized.
   */
  int REALM_NOT_SERVED = 3003;

  /**
   * When returned, a Diameter node SHOULD attempt to send the message
   * to an alternate peer.  This error MUST only be used when a
   * specific server is requested, and it cannot provide the requested
   * service.
   */
  int TOO_BUSY = 3004;

  /**
   * An agent detected a loop while trying to get the message to the
   * intended recipient.  The message MAY be sent to an alternate peer,
   * if one is available, but the peer reporting the error has
   * identified a configuration problem.
   */
  int LOOP_DETECTED = 3005;

  /**
   * A redirect agent has determined that the request could not be
   * satisfied locally and the initiator of the request should direct
   * the request directly to the server, whose contact information has
   * been added to the response.  When set, the Redirect-Host AVP MUST
   * be present.
   */
  int REDIRECT_INDICATION = 3006;

  /**
   * A request was sent for an application that is not supported.
   */
  int APPLICATION_UNSUPPORTED = 3007;

  /**
   * A request was received whose bits in the Diameter header were
   * either set to an invalid combination, or to a value that is
   * inconsistent with the command code's definition.
   */
  int INVALID_HDR_BITS = 3008;

  /**
   * A request was received that included an AVP whose flag bits are
   * set to an unrecognized value, or that is inconsistent with the
   * AVP's definition.
   */
  int INVALID_AVP_BITS = 3009;

  /**
   * A CER was received from an unknown peer.
   */
  int UNKNOWN_PEER = 3010;

  /**
   * The authentication process for the user failed, most likely due to
   * an invalid password used by the user.  Further attempts MUST only
   * be tried after prompting the user for a new password.
   */
  int AUTHENTICATION_REJECTED = 4001;

  /**
   * A Diameter node received the accounting request but was unable to
   * commit it to stable storage due to a temporary lack of space.
   */
  int OUT_OF_SPACE = 4002;

  /**
   * The peer has determined that it has lost the election process and
   * has therefore disconnected the transport connection.
   */
  int ELECTION_LOST = 4003;

  /**
   * The peer received a message that contained an AVP that is not
   * recognized or supported and was marked with the Mandatory bit.  A
   * Diameter message with this error MUST contain one or more Failed-
   * AVP AVP containing the AVPs that caused the failure.
   */
  int AVP_UNSUPPORTED = 5001;

  /**
   * The request contained an unknown Session-Id.
   */
  int UNKNOWN_SESSION_ID = 5002;

  /**
   * A request was received for which the user could not be authorized.
   * This error could occur if the service requested is not permitted
   * to the user.
   */
  int AUTHORIZATION_REJECTED = 5003;

  /**
   * The request contained an AVP with an invalid value in its data
   * portion.  A Diameter message indicating this error MUST include
   * the offending AVPs within a Failed-AVP AVP.
   */
  int INVALID_AVP_VALUE = 5004;

  /**
   * The request did not contain an AVP that is required by the Command
   * Code definition.  If this value is sent in the Result-Code AVP, a
   * Failed-AVP AVP SHOULD be included in the message.  The Failed-AVP
   * AVP MUST contain an example of the missing AVP complete with the
   * Vendor-Id if applicable.  The value field of the missing AVP
   * should be of correct minimum length and contain zeroes.
   */
  int MISSING_AVP = 5005;

  /**
   * A request was received that cannot be authorized because the user
   * has already expended allowed resources.  An example of this error
   * condition is a user that is restricted to one dial-up PPP port,
   * attempts to establish a second PPP connection.
   */
  int RESOURCES_EXCEEDED = 5006;

  /**
   * The Home Diameter server has detected AVPs in the request that
   * contradicted each other, and is not willing to provide service to
   * the user.  One or more Failed-AVP AVPs MUST be present, containing
   * the AVPs that contradicted each other.
   */
  int CONTRADICTING_AVPS = 5007;

  /**
   * A message was received with an AVP that MUST NOT be present.  The
   * Failed-AVP AVP MUST be included and contain a copy of the
   * offending AVP.
   */
  int AVP_NOT_ALLOWED = 5008;

  /**
   * A message was received that included an AVP that appeared more
   * often than permitted in the message definition.  The Failed-AVP
   * AVP MUST be included and contain a copy of the first instance of
   * the offending AVP that exceeded the maximum number of occurrences
   */
  int AVP_OCCURS_TOO_MANY_TIMES = 5009;

  /**
   * This error is returned when a CER message is received, and there
   * are no common applications supported between the peers.
   */
  int NO_COMMON_APPLICATION = 5010;

  /**
   * This error is returned when a request was received, whose version
   * number is unsupported.
   */
  int UNSUPPORTED_VERSION = 5011;

  /**
   * This error is returned when a request is rejected for unspecified
   * reasons.
   */
  int UNABLE_TO_COMPLY = 5012;

  /**
   * This error is returned when an unrecognized bit in the Diameter
   * header is set to one (1).
   */
  int INVALID_BIT_IN_HEADER = 5013;

  /**
   * The request contained an AVP with an invalid length.  A Diameter
   * message indicating this error MUST include the offending AVPs
   * within a Failed-AVP AVP.
   */
  int INVALID_AVP_LENGTH = 5014;

  /**
   * This error is returned when a request is received with an invalid
   * message length.
   */
  int INVALID_MESSAGE_LENGTH = 5015;

  /**
   * The request contained an AVP with which is not allowed to have the
   * given value in the AVP Flags field.  A Diameter message indicating
   * this error MUST include the offending AVPs within a Failed-AVP
   * AVP.
   */
  int INVALID_AVP_BIT_COMBO = 5016;

  /**
   * This error is returned when a CER message is received, and there
   * are no common security mechanisms supported between the peers.  A
   * Capabilities-Exchange-Answer (CEA) MUST be returned with the
   * Result-Code AVP set to DIAMETER_NO_COMMON_SECURITY.
   */
  int NO_COMMON_SECURITY = 5017;

}
