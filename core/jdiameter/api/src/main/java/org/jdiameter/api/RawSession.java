/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.api;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This interface append to base interface specific methods for
 * creating and send raw diameter messages
 * 
 * @version 1.5.1 Final
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RawSession extends BaseSession, Wrapper {

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

  /**
   * Sends and wait response message with default timeout
   * @param message request/answer diameter message
   * @return InFuture result of an asynchronous operation
   * @throws org.jdiameter.api.InternalException The InternalException signals that internal error is occurred.
   * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
   */
  Future<Message> send(Message message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Sends and wait response message with defined timeout
   * @param message  request/answer diameter message
   * @return InFuture result of an asynchronous operation
   * @param timeOut value of timeout
   * @param timeUnit type of timeOut value
   * @throws org.jdiameter.api.InternalException  The InternalException signals that internal error is occurred.
   * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
   */
  Future<Message> send(Message message, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}