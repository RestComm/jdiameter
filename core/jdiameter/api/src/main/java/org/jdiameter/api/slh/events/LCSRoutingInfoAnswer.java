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

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

/*
 * As for 3GPP TS 29.173 v13.0.0, the LCS-Routing-Info-Answer (RIA) command, indicated by the Command-Code field set to 8388622
 * and the "R" bit cleared in the Command Flags field, is sent from HSS to GMLC. The procedure invoked by the GMLC is used for
 * retrieving routing information for LCS (Location Services) for a specified user from the HSS via a LCS-Routing-Info-Request
 * (RIR) command.
 *
 */
public interface LCSRoutingInfoAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "RIA";
  String _LONG_NAME = "LCS-Routing-Info-Answer";

  int code = 8388622;

}
