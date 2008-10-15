/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
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

/**
 * @author Open Cloud
 */
public class DiameterResultCode {

/*
    Informational

    Errors that fall within this category are used to inform the
    requester that a request could not be satisfied, and additional
    action is required on its part before access is granted.
*/

    /**
     * This informational error is returned by a Diameter server to
     * inform the access device that the authentication mechanism being
     * used requires multiple round trips, and a subsequent request needs
     * to be issued in order for access to be granted.
     */
    public static final int DIAMETER_MULTI_ROUND_AUTH = 1001;

/*
    Success

    Errors that fall within the Success category are used to inform a
    peer that a request has been successfully completed.
*/

    /**
     * The Request was successfully completed.
     */
    public static final int DIAMETER_SUCCESS = 2001;

    /**
     * When returned, the request was successfully completed, but
     * additional processing is required by the application in order to
     * provide service to the user.
     */
    public static final int DIAMETER_LIMITED_SUCCESS = 2002;

/*
   Protocol Errors

    Errors that fall within the Protocol Error category SHOULD be treated
    on a per-hop basis, and Diameter proxies MAY attempt to correct the
    error, if it is possible.  Note that these and only these errors MUST
    only be used in answer messages whose 'E' bit is set.
*/

    /**
     * The Request contained a Command-Code that the receiver did not
     * recognize or support.  This MUST be used when a Diameter node
     * receives an experimental command that it does not understand.
     */
    public static final int DIAMETER_COMMAND_UNSUPPORTED = 3001;

    /**
     * This error is given when Diameter can not deliver the message to
     * the destination, either because no host within the realm
     * supporting the required application was available to process the
     * request, or because Destination-Host AVP was given without the
     * associated Destination-Realm AVP.
     */
    public static final int DIAMETER_UNABLE_TO_DELIVER = 3002;

    /**
     * The intended realm of the request is not recognized.
     */
    public static final int DIAMETER_REALM_NOT_SERVED = 3003;

    /**
     * When returned, a Diameter node SHOULD attempt to send the message
     * to an alternate peer.  This error MUST only be used when a
     * specific server is requested, and it cannot provide the requested
     * service.
     */
    public static final int DIAMETER_TOO_BUSY = 3004;

    /**
     * An agent detected a loop while trying to get the message to the
     * intended recipient.  The message MAY be sent to an alternate peer,
     * if one is available, but the peer reporting the error has
     * identified a configuration problem.
     */
    public static final int DIAMETER_LOOP_DETECTED = 3005;

    /**
     * A redirect agent has determined that the request could not be
     * satisfied locally and the initiator of the request should direct
     * the request directly to the server, whose contact information has
     * been added to the response.  When set, the Redirect-Host AVP MUST
     * be present.
     */
    public static final int DIAMETER_REDIRECT_INDICATION = 3006;

    /**
     * A request was sent for an application that is not supported.
     */
    public static final int DIAMETER_APPLICATION_UNSUPPORTED = 3007;

    /**
     * A request was received whose bits in the Diameter header were
     * either set to an invalid combination, or to a value that is
     * inconsistent with the command code's definition.
     */
    public static final int DIAMETER_INVALID_HDR_BITS = 3008;

    /**
     * A request was received that included an AVP whose flag bits are
     * set to an unrecognized value, or that is inconsistent with the
     * AVP's definition.
     */
    public static final int DIAMETER_INVALID_AVP_BITS = 3009;

    /**
     * A CER was received from an unknown peer.
     */
    public static final int DIAMETER_UNKNOWN_PEER = 3010;

/*
    Transient Failures

    Errors that fall within the transient failures category are used
    to inform a peer that the request could not be satisfied at the
    time it was received, but MAY be able to satisfy the request in
    the future.
*/

    /**
     * The authentication process for the user failed, most likely due to
     * an invalid password used by the user.  Further attempts MUST only
     * be tried after prompting the user for a new password.
     */
    public static final int DIAMETER_AUTHENTICATION_REJECTED = 4001;

    /**
     * A Diameter node received the accounting request but was unable to
     * commit it to stable storage due to a temporary lack of space.
     */
    public static final int DIAMETER_OUT_OF_SPACE = 4002;

    /**
     * The peer has determined that it has lost the election process and
     * has therefore disconnected the transport connection.
     */
    public static final int ELECTION_LOST = 4003;

/*
    Permanent Failures

    Errors that fall within the permanent failures category are used
    to inform the peer that the request failed, and should not be
    attempted again.
*/

    /**
     * The peer received a message that contained an AVP that is not
     * recognized or supported and was marked with the Mandatory bit.  A
     * Diameter message with this error MUST contain one or more Failed-
     * AVP AVP containing the AVPs that caused the failure.
     */
    public static final int DIAMETER_AVP_UNSUPPORTED = 5001;

    /**
     * The request contained an unknown Session-Id.
     */
    public static final int DIAMETER_UNKNOWN_SESSION_ID = 5002;

    /**
     * A request was received for which the user could not be authorized.
     * This error could occur if the service requested is not permitted
     * to the user.
     */
    public static final int DIAMETER_AUTHORIZATION_REJECTED = 5003;

    /**
     * The request contained an AVP with an invalid value in its data
     * portion.  A Diameter message indicating this error MUST include
     * the offending AVPs within a Failed-AVP AVP.
     */
    public static final int DIAMETER_INVALID_AVP_VALUE = 5004;

    /**
     * The request did not contain an AVP that is required by the Command
     * Code definition.  If this value is sent in the Result-Code AVP, a
     * Failed-AVP AVP SHOULD be included in the message.  The Failed-AVP
     * AVP MUST contain an example of the missing AVP complete with the
     * Vendor-Id if applicable.  The value field of the missing AVP
     * should be of correct minimum length and contain zeroes.
     */
    public static final int DIAMETER_MISSING_AVP = 5005;

    /**
     * A request was received that cannot be authorized because the user
     * has already expended allowed resources.  An example of this error
     * condition is a user that is restricted to one dial-up PPP port,
     * attempts to establish a second PPP connection.
     */
    public static final int DIAMETER_RESOURCES_EXCEEDED = 5006;

    /**
     * The Home Diameter server has detected AVPs in the request that
     * contradicted each other, and is not willing to provide service to
     * the user.  One or more Failed-AVP AVPs MUST be present, containing
     * the AVPs that contradicted each other.
     */
    public static final int DIAMETER_CONTRADICTING_AVPS = 5007;

    /**
     * A message was received with an AVP that MUST NOT be present.  The
     * Failed-AVP AVP MUST be included and contain a copy of the
     * offending AVP.
     */
    public static final int DIAMETER_AVP_NOT_ALLOWED = 5008;

    /**
     * A message was received that included an AVP that appeared more
     * often than permitted in the message definition.  The Failed-AVP
     * AVP MUST be included and contain a copy of the first instance of
     * the offending AVP that exceeded the maximum number of occurrences
     */
    public static final int DIAMETER_AVP_OCCURS_TOO_MANY_TIMES = 5009;

    /**
     * This error is returned when a CER message is received, and there
     * are no common applications supported between the peers.
     */
    public static final int DIAMETER_NO_COMMON_APPLICATION = 5010;

    /**
     * This error is returned when a request was received, whose version
     * number is unsupported.
     */
    public static final int DIAMETER_UNSUPPORTED_VERSION = 5011;

    /**
     * This error is returned when a request is rejected for unspecified
     * reasons.
     */
    public static final int DIAMETER_UNABLE_TO_COMPLY = 5012;

    /**
     * This error is returned when an unrecognized bit in the Diameter
     * header is set to one (1).
     */
    public static final int DIAMETER_INVALID_BIT_IN_HEADER = 5013;

    /**
     * The request contained an AVP with an invalid length.  A Diameter
     * message indicating this error MUST include the offending AVPs
     * within a Failed-AVP AVP.
     */
    public static final int DIAMETER_INVALID_AVP_LENGTH = 5014;

    /**
     * This error is returned when a request is received with an invalid
     * message length.
     */
    public static final int DIAMETER_INVALID_MESSAGE_LENGTH = 5015;

    /**
     * The request contained an AVP with which is not allowed to have the
     * given value in the AVP Flags field.  A Diameter message indicating
     * this error MUST include the offending AVPs within a Failed-AVP
     * AVP.
     */
    public static final int DIAMETER_INVALID_AVP_BIT_COMBO = 5016;

    /**
     This error is returned when a CER message is received, and there
     are no common security mechanisms supported between the peers.  A
     Capabilities-Exchange-Answer (CEA) MUST be returned with the
     Result-Code AVP set to DIAMETER_NO_COMMON_SECURITY.
     */
    public static final int DIAMETER_NO_COMMON_SECURITY = 5017;
}
