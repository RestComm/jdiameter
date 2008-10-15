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

import java.io.Serializable;
import java.util.Set;

/**
 * A Diameter message is either a request from a client to a server, or a response from a server to a client.
 * Both Request and Answer messages use the basic format of  RFC 3588
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * The message must support adaptable operation to Answer and Request interfaces
 * Serializable interface allows use this class in SLEE Event objects
 * @version 1.5.1 Final
 */

public interface Message extends Wrapper, Serializable {

    /**
     * The Abort-Session-Request message code
     */
    public static final int ABORT_SESSION_REQUEST = 274;
    /**
     * The Abort-Session-Answer message code
     */
    public static final int ABORT_SESSION_ANSWER = 274;
    /**
     * The Accounting-Request message code
     */
    public static final int ACCOUNTING_REQUEST = 271;
    /**
     * The Accounting-Answer message code
     */
    public static final int ACCOUNTING_ANSWER = 271;
    /**
     * The Capabilities-Exchange-Request message code
     */
    public static final int CAPABILITIES_EXCHANGE_REQUEST = 257;
    /**
     * The Capabilities-Exchange-Answer message code
     */
    public static final int CAPABILITIES_EXCHANGE_ANSWER = 257;
    /**
     * The Device-Watchdog-Request message code
     */
    public static final int DEVICE_WATCHDOG_REQUEST = 280;
    /**
     * The Device-Watchdog-Answer message code
     */
    public static final int DEVICE_WATCHDOG_ANSWER = 280;
    /**
     * The Disconnect-Peer-Request message code
     */
    public static final int DISCONNECT_PEER_REQUEST = 282;
    /**
     * The Disconnect-Peer-Answer message code
     */
    public static final int DISCONNECT_PEER_ANSWER = 282;
    /**
     * The Re-Auth-Request message code
     */
    public static final int RE_AUTH_REQUEST = 258;
    /**
     * The Re-Auth-Answer message code
     */
    public static final int RE_AUTH_ANSWER = 258;
    /**
     * The Session-Termination-Request message code
     */
    public static final int SESSION_TERMINATION_REQUEST = 275;
    /**
     * The Session-Termination-Answer message code
     */
    public static final int SESSION_TERMINATION_ANSWER = 275;

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
     * Returns set of Application-Id avps (Auth-Application-Id, Acc-Appplication-Id and Vendor-Specific-Application-Id avps) from message
     * @return set of Application-Id avps
     */    
    Set<ApplicationId> getApplicationIdAvps();

    /**
     * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in
     * network byte order) and aids in matching requests and replies. The
     * sender MUST ensure that the Hop-by-Hop identifier in a request is
     * unique on a given connection at any given time, and MAY attempt to
     * ensure that the number is unique across reboots. 
     * @return hop by hop indentifier from header of message
     */
    long getHopByHopIdentifier();

    /**
     * The End-to-End Identifier is an unsigned 32-bit integer field (in
     * network byte order) and is used to detect duplicate messages. Upon
     * reboot implementations MAY set the high order 12 bits to contain
     * the low order 12 bits of current time, and the low order 20 bits
     * to a random value. Senders of request messages MUST insert a
     * unique identifier on each message.
     * @return end to end indentifier from header of message
     */
    long getEndToEndIdentifier();

    /**
     * @return Set of message Avps
     */
    AvpSet getAvps();    
}
