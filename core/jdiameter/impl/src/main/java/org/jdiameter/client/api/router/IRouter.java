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

package org.jdiameter.client.api.router;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.RouteException;
import org.jdiameter.client.api.IAnswer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.controller.IRealmTable;

/**
 * This class describe Router functionality
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IRouter  {

  /**
   * Return peer from inner peer table by predefined parameters. Fetches peer based on message content, that is HBH or realm/host avp contents.
   * Takes into consideration ApplicationId present in message to pick correct realm definition from RealmTable.
   * This method should be called after {@link #updateRoute}.
   * @param message message with routed avps
   * @param manager instance of peer manager
   * @return peer instance
   * @throws RouteException
   * @throws AvpDataException
   */
  IPeer getPeer(IMessage message, IPeerTable manager) throws RouteException, AvpDataException;

  /**
   * Return realm table
   *
   * @return object representing realm table
   */
  IRealmTable getRealmTable();

  /**
   * Register route information by received request. This information will be used
   * during answer routing.
   * @param request request
   */
  void registerRequestRouteInfo(IRequest request);

  // PCB - Changed to use a better routing mechanism as hopbyhop was not always unique and the table could also grow too big
  /**
   * Return Request route info
   * @param hopByHopIndentifier Hop-by-Hop Identifier
   * @return Array (host and realm)
   */
  String[] getRequestRouteInfo(IMessage message);

  //PCB added
  void garbageCollectRequestRouteInfo(IMessage message);

  /**
   * Start inner time facilities
   */
  void start();

  /**
   * Stop inner time facilities
   */
  void stop();

  /**
   * Release all resources
   */
  void destroy();

  /**
   * Called when redirect answer is received for request. This method update redirect host information and routes to new destination.
   * @param request
   * @param answer
   * @param table
   */
  void processRedirectAnswer(IRequest request, IAnswer answer, IPeerTable table) throws InternalException, RouteException;

  /**
   * Based on Redirect entries or any other factors, this method changes route information.
   * @param message
   * @return
   * @throws RouteException
   * @throws AvpDataException
   */
  boolean updateRoute(IRequest message) throws RouteException, AvpDataException;

}
