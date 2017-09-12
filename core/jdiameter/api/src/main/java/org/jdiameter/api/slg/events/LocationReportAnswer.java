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

package org.jdiameter.api.slg.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

/*
 * As for 3GPP TS 29.172 v13.0.0, Subscriber Location Report operation is used by an MME or SGSN to provide the location of a
 * target UE to a GMLC, when a request for location has been implicitly issued or when a Delayed Location Reporting is triggered
 * after receipt of a request for location for a UE transiently not reachable.
 *
 * The Location-Report-Answer (LRA) command, indicated by the Command-Code field set to 8388621 and the "R" bit cleared in the
 * Command Flags field, is sent by the GMLC to the MME or SGSN in response to the Location-Report-Request command (Subscriber
 * Location Report operation answer).
 */

public interface LocationReportAnswer extends AppAnswerEvent{

  String _SHORT_NAME = "LRA";
  String _LONG_NAME = "Location-Report-Answer";

  int code = 8388621;

}
