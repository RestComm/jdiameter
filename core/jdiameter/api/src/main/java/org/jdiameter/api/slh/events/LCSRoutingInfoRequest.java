/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.jdiameter.api.slh.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

/*
 * As for 3GPP TS 29.173 v13.0.0, the LCS-Routing-Info-Request (RIR) command, indicated by the Command-Code field set to 8388622
 * and the "R" bit set in the Command Flags field, is sent from GMLC to HSS. The procedure invoked by the GMLC is used for
 * retrieving routing information for LCS (Location Services) for a specified user from the HSS.
 *
 */
public interface LCSRoutingInfoRequest extends AppRequestEvent {

  String _SHORT_NAME = "RIR";
  String _LONG_NAME = "LCS-Routing-Info-Request";

  int code = 8388622;

}