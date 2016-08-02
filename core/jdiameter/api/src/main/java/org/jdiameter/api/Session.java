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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This interface append to base interface specific methods for
 * creating and send diameter requests and responses
 *
 * @version 1.5.1 Final
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface Session extends BaseSession, Wrapper  {

  /**
   * Set session request listener
   * @param listener session request listener
   */
  void setRequestListener(NetworkReqListener listener);

  /**
   * Returns a new Request object with the specified command code, applicationId, realm
   * and predefined system avps.
   * @param commandCode code of message
   * @param appId applicationId of destination application
   * @param destRealm name of destination Realm
   * @return new message object
   */
  Request createRequest(int commandCode, ApplicationId appId, String destRealm);

  /**
   * Returns a new Request object with the specified command code, applicationId, realm, host
   * and predefined system avps.
   * @param commandCode code of message
   * @param appId applicationId of destination application
   * @param destRealm name of destination Realm
   * @param destHost name of destination Host
   * @return new Request object
   */
  Request createRequest(int commandCode, ApplicationId appId, String destRealm, String destHost);

  /**
   * Returns a new Request object base on previous request.(header is copied)
   * @param prevRequest previous request (header is copied)
   * @return new Request object
   */
  Request createRequest(Request prevRequest);

  /**
   * Sends and wait response message with default timeout
   * @param message request/answer diameter message
   * @param listener event listener
   * @throws org.jdiameter.api.InternalException The InternalException signals that internal error is occurred.
   * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
   */
  void send(Message message, EventListener<Request, Answer> listener)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

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
  void send(Message message, EventListener<Request, Answer> listener, long timeOut, TimeUnit timeUnit)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

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
  Future<Message> send(Message message, long timeOut, TimeUnit timeUnit)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}