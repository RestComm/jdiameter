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
 * As for 3GPP TS 29.172 v13.0.0, the Provide Subscriber Location operation is used by a GMLC to request the location of a
 * target UE from the MME or SGSN at any time, as part of EPC-MT-LR (Evolved Packet Core Mobile Terminated Location Request) or
 * PS-MT-LR (Packet Switched Mobile Terminated Location Request) positioning procedures. The response contains a location
 * estimate of the target UE and other additional information. This operation is also used by a GMLC to request the location of
 * the target UE from the SGSN at any time, as part of deferred MT-LR procedure. The response contains the acknowledgment of the
 * receipt of the request and other additional information.
 *
 * The Provide-Location-Answer (PLA) command, indicated by the Command-Code field set to 8388620 and the "R" bit cleared in the
 * Command Flags field, is sent by the MME or SGSN to the GMLC in response to the Provide-Location-Request command (Provide
 * Subscriber Location operation answer)
 */

public interface ProvideLocationAnswer extends AppAnswerEvent{

  String _SHORT_NAME = "PLA";
  String _LONG_NAME = "Provide-Location-Answer";

  int code = 8388620;

}
