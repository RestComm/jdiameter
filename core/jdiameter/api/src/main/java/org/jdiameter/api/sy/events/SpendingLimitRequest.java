/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.api.sy.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Spending-Limit-Request (SLR) message, indicated by the Command-Code field set to 8388635 is sent
 * by the PCRF ot the OCS to retrieve subscription information that indicates that policy decision depends
 * on policy counters held on the OCS.
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public interface SpendingLimitRequest extends AppRequestEvent {

  String _SHORT_NAME = "SLR";
  String _LONG_NAME = "Spending-Limit-Request";

  int code = 8388635;

  int getSLRequestTypeAVPValue();
}
