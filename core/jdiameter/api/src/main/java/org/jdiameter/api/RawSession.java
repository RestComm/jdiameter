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

import java.util.concurrent.TimeUnit;

/**
 * This interface append to base interface specific methods for
 * creating and send raw diameter messages
 * @version 1.5.1 Final
 */
public interface RawSession extends BaseSession {

    /**
     * Returns a new message object with the specified command code, applicationId
     * and predefined avps.
     * @param commandCode code of message
     * @param applicationId applicationId of destination application
     * @param avp array of avps
     * @return new message object
     */
    Message createMessage(int commandCode, ApplicationId applicationId, Avp... avp);

    /**
     * Returns a new message object with the predefined Command-code, ApplicationId, HopByHopIdentifier, EndToEndIdentifier
     * This method allow created message from storage or created specific message.
     * @param commandCode code of message
     * @param applicationId applicationId of destination application
     * @param hopByHopIdentifier hop by hop identifier of message
     * @param endToEndIdentifier end to end identifier of message
     * @param avp array of avps
     * @return new message object
     */
    Message createMessage(int commandCode, ApplicationId applicationId, long hopByHopIdentifier, long endToEndIdentifier, Avp... avp);

    /**
     * Returns a new message object with the copy of parent message header
     * @param message origination message
     * @param copyAvps if true all avps will be copy to new message
     * @return Returns a new message object with the copy of parent message header
     */
    Message createMessage(Message message, boolean copyAvps);

    /**
     * Sends and wait response message with default timeout
     * @param message request/answer diameter message
     * @param listener event listener
     * @throws org.jdiameter.api.InternalException The InternalException signals that internal error is occurred.
     * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
     */
    void send(Message message, EventListener<Message, Message> listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     * Sends and wait response message with defined timeout
     * @param message  request/answer diameter message
     * @param listener  event listener
     * @param timeOut value of timeout
     * @param timeUnit type of timeOut value
     * @throws org.jdiameter.api.InternalException  The InternalException signals that internal error is occurred.
     * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
     */
    void send(Message message, EventListener<Message, Message> listener, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}